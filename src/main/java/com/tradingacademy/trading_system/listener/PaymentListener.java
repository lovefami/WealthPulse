package com.tradingacademy.trading_system.listener;

import com.tradingacademy.trading_system.model.event.PaymentCompletedEvent;
import com.tradingacademy.trading_system.service.EmailService;
import com.tradingacademy.trading_system.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentListener {

    private final EmailService emailService ;
    private final SubscriptionService subscriptionService;

    @Autowired
    public PaymentListener(EmailService emailService, SubscriptionService subscriptionService) {
        this.emailService = emailService;
        this.subscriptionService = subscriptionService;
    }

    @EventListener
    public void handlePaymentCompletedEvent(PaymentCompletedEvent event) {
        System.out.println("Received PaymentCompletedEvent:");
        System.out.println("PaymentId: " + event.getPaymentId());
        System.out.println("UserId: " + event.getUserId());
        System.out.println("OrderId: " + event.getOrderId());
        System.out.println("Amount: " + event.getAmount());
        notifyOtherServices(event); // (e.g., email, update subscription)
    }

    private void notifyOtherServices(PaymentCompletedEvent event) {
        // Activate the subscription
        try {
            subscriptionService.activateSubscription(event.getOrderId());
        } catch (Exception e) {
            System.err.println("Failed to activate subscription for Order ID " + event.getOrderId() + ": " + e.getMessage());
            // Optionally, you can rethrow the exception or handle it differently
        }

        // Send a payment confirmation email
        try {
            emailService.sendPaymentConfirmation(
                    event.getUserId(),
                    event.getOrderId(),
                    event.getAmount()
            );
        } catch (Exception e) {
            System.err.println("Failed to send payment confirmation email for Order ID " + event.getOrderId() + ": " + e.getMessage());
            // Optionally, you can rethrow the exception or handle it differently
        }
    }
}