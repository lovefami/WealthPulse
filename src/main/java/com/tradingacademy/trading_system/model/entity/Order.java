package com.tradingacademy.trading_system.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "order")
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private String subscriptionType;

  @Column(nullable = false)
  private Double amount;

  @Column(nullable = false)
  private LocalDateTime orderDate;

  @Column(nullable = false)
  private String status; // 订单状态：PENDING, COMPLETED, FAILED

  @Column(unique = true, nullable = false)
  private String idempotencyKey; // 绑定到订单的幂等性键

  @OneToOne
  @JoinColumn(name = "payment_id", nullable = false)
  private Payment payment; // 关联到 Payment
}