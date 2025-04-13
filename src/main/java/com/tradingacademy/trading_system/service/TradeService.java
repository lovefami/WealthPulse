package com.tradingacademy.trading_system.service;

import com.tradingacademy.trading_system.model.dto.TradeRequest;
import com.tradingacademy.trading_system.model.entity.StockTrade;
import com.tradingacademy.trading_system.model.entity.OptionTrade;
import com.tradingacademy.trading_system.model.entity.Trade;
import com.tradingacademy.trading_system.model.entity.User;
import com.tradingacademy.trading_system.model.entity.VirtualAccount;
import com.tradingacademy.trading_system.repository.TradeRepository;
import com.tradingacademy.trading_system.repository.UserRepository;
import com.tradingacademy.trading_system.repository.VirtualAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TradeService {

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VirtualAccountRepository virtualAccountRepository;

    @Autowired
    private VirtualAccountService virtualAccountService;

    public Trade createTrade(Long userId, TradeRequest tradeRequest) {
        // Get or create virtual account
        VirtualAccount account = virtualAccountService.getOrCreateVirtualAccount(userId);

        // Validate trade request
        if (tradeRequest.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        if (tradeRequest.getBuyPrice() <= 0) {
            throw new IllegalArgumentException("Buy price must be greater than zero.");
        }
        if (tradeRequest.getTradeType() == null || (!tradeRequest.getTradeType().equals("STOCK") && !tradeRequest.getTradeType().equals("OPTION"))) {
            throw new IllegalArgumentException("Trade type must be 'STOCK' or 'OPTION'.");
        }
        if (tradeRequest.getAction() == null || (!tradeRequest.getAction().equals("BUY") && !tradeRequest.getAction().equals("SELL"))) {
            throw new IllegalArgumentException("Action must be 'BUY' or 'SELL'.");
        }

        // Calculate trade cost
        double tradeCost = calculateTradeCost(tradeRequest);

        // Validate based on action
        if (tradeRequest.getAction().equals("BUY")) {
            // Check balance for buying
            if (account.getBalance() < tradeCost) {
                throw new IllegalArgumentException("Insufficient balance to buy. Required: " + tradeCost + ", Available: " + account.getBalance());
            }
        } else {
            // Since action can only be "BUY" or "SELL", this block handles "SELL"
            // Check position for selling
            int netPosition = calculateNetPosition(userId, tradeRequest);
            if (netPosition < tradeRequest.getQuantity()) {
                throw new IllegalArgumentException("Insufficient position to sell. Available: " + netPosition + ", Attempting to sell: " + tradeRequest.getQuantity());
            }
        }

        // Create the trade
        Trade trade;
        if (tradeRequest.getTradeType().equals("STOCK")) {
            trade = new StockTrade();
        } else {
            trade = new OptionTrade();
            ((OptionTrade) trade).setOptionType(tradeRequest.getOptionType());
            ((OptionTrade) trade).setStrikePrice(tradeRequest.getStrikePrice());
            ((OptionTrade) trade).setExpiryDate(tradeRequest.getExpiryDate());
        }

        // Set common fields
        trade.setUser(account.getUser());
        trade.setSymbol(tradeRequest.getSymbol());
        // Set quantity based on action
        trade.setQuantity(tradeRequest.getAction().equals("BUY") ? tradeRequest.getQuantity() : -tradeRequest.getQuantity());
        trade.setBuyPrice(tradeRequest.getBuyPrice());
        trade.setTradeTime(LocalDateTime.now());
        trade = tradeRepository.save(trade);

        // Update account balance based on action
        if (tradeRequest.getAction().equals("BUY")) {
            // Buying: deduct the cost
            account.setBalance(account.getBalance() - tradeCost);
        } else {
            // Selling: add the proceeds
            account.setBalance(account.getBalance() + tradeCost);
        }

        // Update virtual account market value
        VirtualAccount updatedAccount = virtualAccountService.getVirtualAccount(userId);
        account.setMarketValue(updatedAccount.getMarketValue());
        virtualAccountRepository.save(account);

        // Removed automatic reset logic; net worth can now be negative
        return trade;
    }

    public void resetAccount(Long userId) {
        virtualAccountService.resetVirtualAccount(userId);
    }

    private double calculateTradeCost(TradeRequest tradeRequest) {
        double tradeCost;
        if (tradeRequest.getTradeType().equals("STOCK")) {
            tradeCost = tradeRequest.getQuantity() * tradeRequest.getBuyPrice();
        } else {
            // Option trade: each contract covers 100 shares
            if (tradeRequest.getOptionType() == null || (!tradeRequest.getOptionType().equals("CALL") && !tradeRequest.getOptionType().equals("PUT"))) {
                throw new IllegalArgumentException("Option type must be 'CALL' or 'PUT'.");
            }
            if (tradeRequest.getStrikePrice() == null || tradeRequest.getStrikePrice() <= 0) {
                throw new IllegalArgumentException("Strike price must be greater than zero.");
            }
            if (tradeRequest.getExpiryDate() == null) {
                throw new IllegalArgumentException("Expiry date is required for option trades.");
            }
            tradeCost = tradeRequest.getQuantity() * tradeRequest.getBuyPrice() * 100;
        }
        return tradeCost;
    }

    private int calculateNetPosition(Long userId, TradeRequest tradeRequest) {
        List<Trade> userTrades = tradeRepository.findByUserId(userId);
        int netPosition = 0;
        for (Trade trade : userTrades) {
            if (trade.getSymbol().equals(tradeRequest.getSymbol()) &&
                    trade.getTradeType().equals(tradeRequest.getTradeType())) {
                // For options, match optionType, strikePrice, and expiryDate
                if (tradeRequest.getTradeType().equals("OPTION")) {
                    OptionTrade optionTrade = (OptionTrade) trade;
                    if (!optionTrade.getOptionType().equals(tradeRequest.getOptionType()) ||
                            !optionTrade.getStrikePrice().equals(tradeRequest.getStrikePrice()) ||
                            !optionTrade.getExpiryDate().equals(tradeRequest.getExpiryDate())) {
                        continue;
                    }
                }
                netPosition += trade.getQuantity();
            }
        }
        return netPosition;
    }
}