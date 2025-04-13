package com.tradingacademy.trading_system.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "profit_loss_history")
@Setter
@Getter
public class ProfitLossHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "trade_id", nullable = false)
  private Trade trade;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private Double profitLoss;

  @Column(nullable = false)
  private LocalDateTime recordedAt;


  @Column(nullable = false)
  private String symbol;
}