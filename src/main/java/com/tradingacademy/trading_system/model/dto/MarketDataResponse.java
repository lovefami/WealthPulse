package com.tradingacademy.trading_system.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarketDataResponse {

    private String symbol;
    private Double price;
}
