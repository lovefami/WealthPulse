package com.tradingacademy.trading_system.service;

import com.tradingacademy.trading_system.model.entity.Order;
import com.tradingacademy.trading_system.model.entity.Subscription;
import com.tradingacademy.trading_system.model.entity.User;
import com.tradingacademy.trading_system.model.entity.Payment;
import com.tradingacademy.trading_system.repository.OrderRepository;
import com.tradingacademy.trading_system.repository.SubscriptionRepository;
import com.tradingacademy.trading_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository, UserRepository userRepository,
                               PaymentService paymentService, OrderRepository orderRepository) {
        this.subscriptionRepository = subscriptionRepository;
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
    public Subscription purchaseSubscription(Long userId, String subscriptionType, String paymentMethod, String paymentMethodToken) {
        // 验证用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // 确定订阅金额和有效期
        double amount;
        int days;
        if ("MONTHLY".equalsIgnoreCase(subscriptionType)) {
            amount = 29.99;
            days = 30;
        } else if ("YEARLY".equalsIgnoreCase(subscriptionType)) {
            amount = 99.99;
            days = 365;
        } else {
            throw new IllegalArgumentException("Invalid subscription type, only support 'MONTHLY' and 'YEARLY'");
        }

        // 生成唯一的幂等性键
        String idempotencyKey = UUID.randomUUID().toString();

        // 创建订单并处理支付
        Order order = paymentService.createOrderAndProcessPayment(userId, subscriptionType, (Double) amount, paymentMethod, paymentMethodToken, idempotencyKey);

        // 获取支付记录
        Payment payment = order.getPayment();

        // 创建订阅记录
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setSubscriptionType(subscriptionType.toUpperCase());
        subscription.setAmount((Double) amount);
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusDays(days));
        subscription.setStatus("ACTIVE");
        subscription.setPayment(payment); // 关联 Payment
        return subscriptionRepository.save(subscription);
    }
}