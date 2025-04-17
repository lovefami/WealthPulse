package com.tradingacademy.trading_system.service;

import com.tradingacademy.trading_system.model.entity.Order;
import com.tradingacademy.trading_system.model.entity.Subscription;
import com.tradingacademy.trading_system.model.entity.SubscriptionPlan;
import com.tradingacademy.trading_system.model.entity.User;
import com.tradingacademy.trading_system.model.entity.Payment;
import com.tradingacademy.trading_system.repository.OrderRepository;
import com.tradingacademy.trading_system.repository.SubscriptionPlanRepository;
import com.tradingacademy.trading_system.repository.SubscriptionRepository;
import com.tradingacademy.trading_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                               SubscriptionPlanRepository subscriptionPlanRepository,
                               UserRepository userRepository,
                               PaymentService paymentService,
                               OrderRepository orderRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.userRepository = userRepository;
        this.paymentService = paymentService;
        this.orderRepository = orderRepository;
    }

    // 检查用户是否具有有效的订阅
    public boolean hasActiveSubscription(Long userId) {
        return subscriptionRepository.findByUserIdAndStatus(userId, "ACTIVE")
                .map(subscription -> {
                    LocalDateTime now = LocalDateTime.now();
                    return subscription.getStartDate().isBefore(now) && subscription.getEndDate().isAfter(now);
                })
                .orElse(false);
    }

    // 用户购买订阅
    public Subscription purchaseSubscription(Long userId, String planName, String paymentMethod, String paymentMethodToken) {
        // 验证用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // 查找订阅计划
        SubscriptionPlan plan = subscriptionPlanRepository.findByPlanName(planName.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid subscription plan: " + planName + ", only support 'MONTHLY' and 'YEARLY'"));

        // 使用计划中的金额和有效期
        double price = plan.getPrice(); // Changed from getAmount() to getPrice()
        int days = plan.getDurationDays();

        // 生成唯一的幂等性键
        String idempotencyKey = UUID.randomUUID().toString();

        // 创建订单并处理支付
        Order order = paymentService.createOrderAndProcessPayment(userId, planName, price, paymentMethod, paymentMethodToken, idempotencyKey);

        // 获取支付记录
         Payment payment = order.getPayment();

        // 创建订阅记录（初始状态为 PENDING，因为支付是异步处理的）
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setSubscriptionPlan(plan);
        subscription.setAmount(price); // Use price here to match the field in SubscriptionPlan
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusDays(days));
        subscription.setStatus("PENDING"); // Set to PENDING until payment is confirmed
        subscription.setPayment(payment);
        subscription.setOrder(order); // Link the subscription to the order
        return subscriptionRepository.save(subscription);
    }

    // 激活订阅（在支付完成后调用）
    public void activateSubscription(Long orderId) {
        // Find the subscription associated with the order
        Subscription subscription = subscriptionRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalStateException("Subscription not found for order ID: " + orderId));

        // Verify the order's payment status
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found: " + orderId));

        if (!"COMPLETED".equals(order.getStatus())) {
            throw new IllegalStateException("Cannot activate subscription: Order payment is not completed for order ID: " + orderId);
        }

        // Activate the subscription
        subscription.setStatus("ACTIVE");
        if (subscription.getStartDate() == null || subscription.getEndDate() == null) {
            int days = subscription.getSubscriptionPlan().getDurationDays();
            subscription.setStartDate(LocalDateTime.now());
            subscription.setEndDate(LocalDateTime.now().plusDays(days));
        }
        subscriptionRepository.save(subscription);
        System.out.println("Activated subscription for Order ID: " + orderId + ", User ID: " + subscription.getUser().getId());
    }
}