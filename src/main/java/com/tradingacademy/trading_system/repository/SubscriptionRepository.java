package com.tradingacademy.trading_system.repository;

import com.tradingacademy.trading_system.model.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
  Optional<Subscription> findByUserIdAndStatus(Long userId, String status);

  Optional<Subscription> findByPaymentId(Long paymentId);
  Optional<Subscription> findByOrderId(Long orderId);

}