package com.tradingacademy.trading_system.model.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "virtual_accounts")
public class VirtualAccount {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private Double balance;

  @Setter
  @Transient//Not persisted ; calculated at runtime
  private Double marketValue;

  public Double getMarketValue(List<Trade> trades) {
    return marketValue;
  }

}