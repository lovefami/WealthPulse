package com.tradingacademy.trading_system.repository;

import com.tradingacademy.trading_system.model.entity.KlineData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KlineDataRepository extends JpaRepository<KlineData, Long> {
  List<KlineData> findBySymbol(String symbol);
}