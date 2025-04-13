package com.tradingacademy.trading_system.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "payment")
public class Payment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private Double amount;

  @Column(nullable = false)
  private String paymentMethod;

  @Column(nullable = false)
  private LocalDateTime paymentDate;

  @Column(nullable = false)
  private String status;

  // 移除 idempotencyKey，移动到 Order 实体
}