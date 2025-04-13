package com.tradingacademy.trading_system.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private Long paymentId;
    private Long userId;
    private String paymentMethod;
    private String paymentMethodToken;
    private Double amount;

    public PaymentRequest() {
    }

    public PaymentRequest(Long paymentId, Long userId, String paymentMethod, String paymentMethodToken, Double amount) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.paymentMethodToken = paymentMethodToken;
        this.amount = amount;
    }
}