package com.tradingacademy.trading_system.repository;

import com.tradingacademy.trading_system.model.entity.VirtualAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VirtualAccountRepository extends JpaRepository<VirtualAccount, Long> {
  VirtualAccount findByUserId(Long userId);
}