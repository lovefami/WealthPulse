package com.tradingacademy.trading_system.model.entity;

import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "kline_data")
public class KlineData {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String symbol;

  @Column(nullable = false)
  private LocalDateTime timeStamp;

  @Column(nullable = false)
  private Double highPrice;

  @Column(nullable= false)
  private Double openPrice;

  @Column(nullable = false)
  private Double closePrice;

  @Column(nullable = false)
  private Double lowPrice;

  @Column(nullable = false)
  private Double Volume;



}