package com.tradingacademy.trading_system.repository;

import com.tradingacademy.trading_system.model.entity.OptionTrade;
import com.tradingacademy.trading_system.model.entity.StockTrade;
import com.tradingacademy.trading_system.model.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {

  // Get all trades for a user
  List<Trade> findByUserId(Long userId);

  // Get all trades within a time range for a user
  List<Trade> findByUserIdAndTradeTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);

  // Get all trades by symbol
  List<Trade> findBySymbol(String symbol);

  // Get the most recent trade for a given user
  Trade findTopByUserIdOrderByTradeTimeDesc(Long userId);

  // Get all OptionTrades for a user
  @Query("SELECT t FROM OptionTrade t WHERE t.user.id = :userId")
  List<OptionTrade> findOptionTradesByUserId(Long userId);

  // Get all StockTrades for a user
  @Query("SELECT t FROM StockTrade t WHERE t.user.id = :userId")
  List<StockTrade> findStockTradesByUserId(Long userId);

  // Delete all trades for a user
  @Modifying
  @Query("DELETE FROM Trade t WHERE t.user.id = :userId")
  void deleteByUserId(Long userId);
}
