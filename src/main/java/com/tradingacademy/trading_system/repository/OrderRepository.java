package com.tradingacademy.trading_system.repository;

import com.tradingacademy.trading_system.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
  Optional<Order> findByIdempotencyKey(String idempotencyKey);
  Optional<Order> findByPaymentId(Long paymentId);
}