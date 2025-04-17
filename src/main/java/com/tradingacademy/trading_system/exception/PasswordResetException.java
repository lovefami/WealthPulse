package com.tradingacademy.trading_system.exception;

import com.tradingacademy.trading_system.repository.PasswordResetTokenRepository;

public class PasswordResetException extends RuntimeException {
    private final String userMessage;

    public PasswordResetException(String userMessage,String detailedMessage) {
            super(detailedMessage);
            this.userMessage  = userMessage;
    }

    public PasswordResetException(String userMessage,String detailedMessage, Throwable cause) {
        super(detailedMessage, cause);
        this.userMessage  = userMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }

}
