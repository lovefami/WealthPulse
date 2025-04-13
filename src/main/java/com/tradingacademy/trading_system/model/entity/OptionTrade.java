package com.tradingacademy.trading_system.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("OPTION")
@Setter
@Getter
public class OptionTrade extends Trade {

  @Column(nullable = false)
  private String optionType;

  @Column(nullable = false)
  private Double strikePrice;

  @Column(nullable = false)
  private LocalDate expiryDate;
}