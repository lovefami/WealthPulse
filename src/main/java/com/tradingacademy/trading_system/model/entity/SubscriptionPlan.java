package com.tradingacademy.trading_system.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "subscription_plan")
public class SubscriptionPlan {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String planName;//套餐名称

  @Column(nullable = false)
private Double price;//订阅价格

  @Column(nullable = false)
  private Integer durationDays;//订阅时间
}