package com.tradingacademy.trading_system.repository;

import com.tradingacademy.trading_system.model.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
  Optional<SubscriptionPlan> findByPlanName(String planName);
}