package com.tradingacademy.trading_system.model.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentCompletedEvent extends ApplicationEvent {

    private final Long paymentId;
    private final Long userId;
    private final Long orderId;
    private final Double amount;

    public PaymentCompletedEvent(Object source, Long paymentId, Long userId, Long orderId, Double amount) {
        super(source);
        this.paymentId = paymentId;
        this.userId = userId;
        this.orderId = orderId;
        this.amount = amount;
    }
}