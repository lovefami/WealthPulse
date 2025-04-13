package com.tradingacademy.trading_system.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
public class Subscription {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private String subscriptionType;

  @Column(nullable = false)
  private Double amount; // 订阅金额

  @Column(nullable = false)
  private LocalDateTime startDate;

  @Column(nullable = false)
  private LocalDateTime endDate;

  @Column(nullable = false)
  private String status; // "ACTIVE" 或 "EXPIRED"

  @OneToOne
  @JoinColumn(name = "payment_id", nullable = false)
  private Payment payment;
}