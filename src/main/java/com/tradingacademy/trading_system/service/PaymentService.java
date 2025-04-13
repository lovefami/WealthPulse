package com.tradingacademy.trading_system.service;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.PurchaseUnitRequest;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.ApplicationContext;
import com.paypal.orders.OrdersCreateRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.tradingacademy.trading_system.model.dto.PaymentRequest;
import com.tradingacademy.trading_system.model.entity.Order; // Your custom Order entity
import com.tradingacademy.trading_system.model.entity.Payment;
import com.tradingacademy.trading_system.model.entity.User;
import com.tradingacademy.trading_system.repository.OrderRepository;
import com.tradingacademy.trading_system.repository.PaymentRepository;
import com.tradingacademy.trading_system.repository.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PayPalHttpClient payPalHttpClient;
    private final RabbitTemplate rabbitTemplate;

    private static final String PAYMENT_QUEUE = "payment-processing-queue";

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, UserRepository userRepository,
                          OrderRepository orderRepository, PayPalHttpClient payPalHttpClient,
                          RabbitTemplate rabbitTemplate) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.payPalHttpClient = payPalHttpClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    // Stripe 支付
    private boolean processStripePayment(String paymentMethodToken, Double amount) {
        try {
            long amountInCents = (long) (amount * 100);
            Map<String, Object> params = new HashMap<>();
            params.put("amount", amountInCents);
            params.put("currency", "USD");
            params.put("payment_method", paymentMethodToken);
            params.put("confirmation_method", "automatic");
            params.put("confirm", true);

            PaymentIntent paymentIntent = PaymentIntent.create(params);
            if ("succeeded".equals(paymentIntent.getStatus())) {
                System.out.println("Stripe payment succeeded: PaymentIntent ID=" + paymentIntent.getId());
                return true;
            } else {
                System.out.println("Stripe payment failed: Status=" + paymentIntent.getStatus());
                return false;
            }
        } catch (StripeException e) {
            System.out.println("Stripe payment failed: " + e.getMessage());
            return false;
        }
    }

    // PayPal 支付
    private boolean processPayPalPayment(Double amount) {
        try {
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.checkoutPaymentIntent("CAPTURE");

            List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
            PurchaseUnitRequest purchaseUnit = new PurchaseUnitRequest()
                    .amountWithBreakdown(new AmountWithBreakdown()
                            .currencyCode("USD")
                            .value(String.format("%.2f", amount)));
            purchaseUnits.add(purchaseUnit);
            orderRequest.purchaseUnits(purchaseUnits);

            ApplicationContext applicationContext = new ApplicationContext()
                    .returnUrl("http://localhost:8080/success")
                    .cancelUrl("http://localhost:8080/cancel");
            orderRequest.applicationContext(applicationContext);

            OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);
            // Explicitly specify the PayPal Order type
            HttpResponse<com.paypal.orders.Order> response = payPalHttpClient.execute(request);
            com.paypal.orders.Order payPalOrder = response.result();

            if ("CREATED".equals(payPalOrder.status())) {
                System.out.println("PayPal order created successfully: Order ID=" + payPalOrder.id());
                return true;
            } else {
                System.out.println("PayPal order creation failed: Status=" + payPalOrder.status());
                return false;
            }
        } catch (Exception e) {
            System.out.println("PayPal payment failed: " + e.getMessage());
            return false;
        }
    }

    // 处理支付网关（支持重试）
    private boolean processPaymentGateway(String paymentMethod, String paymentMethodToken, Double amount) {
        int maxRetries = 3;
        int retryCount = 0;
        boolean success = false;

        while (retryCount < maxRetries && !success) {
            try {
                switch (paymentMethod.toUpperCase()) {
                    case "STRIPE":
                        success = processStripePayment(paymentMethodToken, amount);
                        break;
                    case "PAYPAL":
                        success = processPayPalPayment(amount);
                        break;
                    default:
                        throw new IllegalArgumentException("Please select a valid payment method");
                }
            } catch (Exception e) {
                retryCount++;
                System.out.println("Payment attempt " + retryCount + "/" + maxRetries + " failed for method " + paymentMethod + ": " + e.getMessage());
                if (retryCount == maxRetries) {
                    System.out.println("Max retries reached for payment method " + paymentMethod + ". Failing payment.");
                    return false;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return success;
    }

    // 检查幂等性
    private boolean checkIdempotency(String idempotencyKey) {
        return orderRepository.findByIdempotencyKey(idempotencyKey).isPresent();
    }

    // 创建订单并处理支付
    @Transactional
    public Order createOrderAndProcessPayment(Long userId, String subscriptionType, Double amount, String paymentMethod, String paymentMethodToken, String idempotencyKey) {
        // 验证幂等性
        if (idempotencyKey == null || idempotencyKey.isEmpty()) {
            idempotencyKey = UUID.randomUUID().toString();
        }
        if (checkIdempotency(idempotencyKey)) {
            System.out.println("Duplicate order request detected for idempotencyKey=" + idempotencyKey);
            throw new IllegalStateException("Duplicate order request detected");
        }

        // 验证用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // 验证金额
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Order amount must be greater than zero");
        }

        // 创建支付记录
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod.toUpperCase());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus("PENDING");
        payment = paymentRepository.save(payment);

        // 创建订单
        Order order = new Order();
        order.setUser(user);
        order.setSubscriptionType(subscriptionType.toUpperCase());
        order.setAmount(amount);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setIdempotencyKey(idempotencyKey);
        order.setPayment(payment); // 关联 Payment
        order = orderRepository.save(order);

        // 异步处理支付
        PaymentRequest paymentRequest = new PaymentRequest(payment.getId(), userId, paymentMethod, paymentMethodToken, amount);
        rabbitTemplate.convertAndSend(PAYMENT_QUEUE, paymentRequest);
        System.out.println("Payment request queued for processing: Payment ID=" + payment.getId() + ", Order ID=" + order.getId());

        return order;
    }

    // 异步支付处理（消费者）
    public void processPaymentAsync(PaymentRequest request) {
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new IllegalStateException("Payment not found: " + request.getPaymentId()));

        if (!"PENDING".equals(payment.getStatus())) {
            System.out.println("Payment already processed: Payment ID=" + payment.getId() + ", Status=" + payment.getStatus());
            return;
        }

        // 直接通过 paymentId 查找关联的 Order
        Order order = orderRepository.findByPaymentId(payment.getId())
                .orElseThrow(() -> new IllegalStateException("Order not found for payment: " + payment.getId()));

        boolean paymentSuccess = processPaymentGateway(request.getPaymentMethod(), request.getPaymentMethodToken(), request.getAmount());
        payment.setStatus(paymentSuccess ? "SUCCESS" : "FAILED");
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);

        // 更新订单状态
        order.setStatus(paymentSuccess ? "COMPLETED" : "FAILED");
        orderRepository.save(order);

        if (!paymentSuccess) {
            System.out.println("Payment failed after processing: Payment ID=" + payment.getId());
            throw new IllegalStateException("Payment failed, please try again");
        }

        System.out.println("Payment processed successfully: Payment ID=" + payment.getId());
        // TODO: 发布支付完成事件，通知其他服务
    }

    // 获取用户的所有支付记录
    public List<Payment> getUserPayments(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        return paymentRepository.findByUserId(userId);
    }
}