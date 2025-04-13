package com.tradingacademy.trading_system.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TradeRequest {
    private String tradeType;  // "STOCK" or "OPTION"
    private String action;     // "BUY" or "SELL"
    private int quantity;      // Positive number of shares/contracts
    private String symbol;
    private Double buyPrice;
    // Fields for OptionTrade
    private String optionType;  // e.g., "CALL" or "PUT"
    private Double strikePrice;
    private LocalDate expiryDate;
}