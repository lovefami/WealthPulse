package com.tradingacademy.trading_system.service;

import com.tradingacademy.trading_system.model.dto.MarketDataErrorResponse;
import com.tradingacademy.trading_system.model.dto.MarketDataResponse;
import com.tradingacademy.trading_system.model.entity.Trade;
import com.tradingacademy.trading_system.model.entity.User;
import com.tradingacademy.trading_system.model.entity.VirtualAccount;
import com.tradingacademy.trading_system.repository.TradeRepository;
import com.tradingacademy.trading_system.repository.UserRepository;
import com.tradingacademy.trading_system.repository.VirtualAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class VirtualAccountService {

    private static final double DEFAULT_BALANCE = 100000.0;

    @Autowired
    private VirtualAccountRepository virtualAccountRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private UserRepository userRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public VirtualAccount getVirtualAccount(Long userId) {
        VirtualAccount account = getOrCreateVirtualAccount(userId);

        List<Trade> trades = tradeRepository.findByUserId(userId);
        Double marketValue = calculateMarketValue(trades);
        account.setMarketValue(marketValue);

        return account;
    }

    public VirtualAccount getOrCreateVirtualAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        VirtualAccount account = virtualAccountRepository.findByUserId(userId);
        if (account == null) {
            account = new VirtualAccount();
            account.setUser(user);
            account.setBalance(DEFAULT_BALANCE);
            account = virtualAccountRepository.save(account);
        }
        return account;
    }

    public void resetVirtualAccount(Long userId) {
        VirtualAccount account = getOrCreateVirtualAccount(userId);

        // Reset the account
        account.setBalance(DEFAULT_BALANCE);
        account.setMarketValue(0.0);
        virtualAccountRepository.save(account);

        // Delete all trades for the user
        tradeRepository.deleteByUserId(userId);
    }

    public double getNetWorth(Long userId) {
        VirtualAccount account = getVirtualAccount(userId);
        return account.getBalance() + account.getMarketValue();
    }

    private Double calculateMarketValue(List<Trade> trades) {
        if (trades == null || trades.isEmpty()) {
            return 0.0;
        }

        double totalMarketValue = 0.0;
        for (Trade trade : trades) {
            String symbol = trade.getSymbol();
            int quantity = trade.getQuantity();
            Double price = fetchMarketPrice(symbol, trade);
            totalMarketValue += price * quantity;
        }

        return totalMarketValue;
    }

    private Double fetchMarketPrice(String symbol, Trade trade) {
        String url = "http://localhost:5000/api/market-data/" + symbol;
        try {
            ResponseEntity<MarketDataResponse> responseEntity = restTemplate.getForEntity(url, MarketDataResponse.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                MarketDataResponse response = responseEntity.getBody();
                if (response != null && response.getPrice() != null) {
                    System.out.println("Fetched price for symbol: " + response.getSymbol() + ", price: " + response.getPrice());
                    return response.getPrice();
                } else {
                    return handlePriceFetchFailure(symbol, "Invalid market data response", trade);
                }
            } else {
                ResponseEntity<MarketDataErrorResponse> errorResponseEntity =
                        restTemplate.getForEntity(url, MarketDataErrorResponse.class);
                MarketDataErrorResponse errorResponse = errorResponseEntity.getBody();
                String errorMessage = errorResponse != null ? errorResponse.getError() : "Unknown error";
                return handlePriceFetchFailure(symbol, "Failed to fetch market data. Error: " + errorMessage, trade);
            }
        } catch (HttpClientErrorException e) {
            MarketDataErrorResponse errorResponse = restTemplate.getForObject(url, MarketDataErrorResponse.class);
            String errorMessage = errorResponse != null ? errorResponse.getError() : e.getMessage();
            return handlePriceFetchFailure(symbol, "Failed to fetch market data. Error: " + errorMessage, trade);
        } catch (Exception e) {
            return handlePriceFetchFailure(symbol, "Error fetching market data: " + e.getMessage(), trade);
        }
    }

    private Double handlePriceFetchFailure(String symbol, String errorMessage, Trade trade) {
        System.err.println("Warning: " + errorMessage + " for symbol: " + symbol + ". Using buyPrice as fallback.");
        return trade.getBuyPrice();
    }
}