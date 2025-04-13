package com.tradingacademy.trading_system.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Getter
@Setter
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private Double SignalPrice;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private Boolean triggered;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String condition;



}