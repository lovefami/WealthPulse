package com.tradingacademy.trading_system.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "trades")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "trade_type", discriminatorType = DiscriminatorType.STRING)
@Setter
@Getter
public abstract class Trade {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String symbol;

  @Column(nullable = false)
  private Double buyPrice;

  @Column
  private Double sellPrice;

  @Column(nullable = false)
  private Integer quantity;

  @Column(nullable = false)
  private LocalDateTime tradeTime;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name ="trade_type", insertable = false, updatable = false)
  private String tradeType;
}