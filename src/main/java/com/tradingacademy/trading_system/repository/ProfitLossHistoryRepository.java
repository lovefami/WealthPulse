package com.tradingacademy.trading_system.repository;

import com.tradingacademy.trading_system.model.entity.ProfitLossHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProfitLossHistoryRepository extends JpaRepository<ProfitLossHistory, Long> {
  List<ProfitLossHistory> findByUserId(Long userId);
  List<ProfitLossHistory> findByUserIdAndSymbolAndRecordedAtBetween(Long userId, String symbol, LocalDateTime start, LocalDateTime end);
  List<ProfitLossHistory> findByUserIdAndSymbol(Long userId, String symbol);

}