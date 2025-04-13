package com.tradingacademy.trading_system.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("STOCK")
@Getter
@Setter
public class StockTrade extends Trade {
}