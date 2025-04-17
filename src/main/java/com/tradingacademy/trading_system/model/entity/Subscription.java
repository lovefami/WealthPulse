
package com.tradingacademy.trading_system.model.entity;

import com.tradingacademy.trading_system.model.entity.Order;
import com.tradingacademy.trading_system.model.entity.Payment;
import com.tradingacademy.trading_system.model.entity.SubscriptionPlan;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscription")
@Getter
@Setter
public class Subscription {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "plan_id")
  private SubscriptionPlan subscriptionPlan; // Reference to SubscriptionPlan

  @Column(name = "amount")
  private Double amount;

  @Column(name = "start_date")
  private LocalDateTime startDate;

  @Column(name = "end_date")
  private LocalDateTime endDate;

  @Column(name = "status")
  private String status;

  @OneToOne
  @JoinColumn(name = "payment_id")
  private Payment payment;

  @OneToOne
  @JoinColumn(name = "order_id")
  private Order order;
}