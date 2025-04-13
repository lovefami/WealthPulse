package com.tradingacademy.trading_system.service;

import com.tradingacademy.trading_system.model.entity.Alert;
import com.tradingacademy.trading_system.model.entity.User;
import com.tradingacademy.trading_system.repository.AlertRepository;
import com.tradingacademy.trading_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Autowired
    public AlertService(AlertRepository alertRepository, UserRepository userRepository, JavaMailSender mailSender) {
        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    // create new alert
    public Alert createAlert(Long userId, String symbol, Double targetPrice, String condition) {
        //check input

        if(symbol == null || symbol.trim().isEmpty()){
            throw new IllegalArgumentException("Symbol cannot be null or empty");
        }
        //find user

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("未找到用户，ID: " + userId));

        //Create Alert
        Alert alert = new Alert();
        alert.setUser(user);
        alert.setSymbol(symbol);
        alert.setCreatedAt(LocalDateTime.now());
        alert.setStatus("Active");
        alert.setTriggered(false);
        alert.setSignalPrice(0.0);
        return alertRepository.save(alert);
    }

    //fetch user alert

    public List<Alert>getUserAlerts(Long userId){
        if(!userRepository.existsById(userId)){
            throw new IllegalArgumentException("User not found." + userId);
        }
        return alertRepository.findByUserId(userId);
    }
    //delete user alert

    public void deleteAlert(Long alertId){
        if(!alertRepository.existsById(alertId)){
            throw new IllegalArgumentException("Alert not found." + alertId);
        }
        alertRepository.deleteById(alertId);
    }

    // signal and notification

    public void processBuySignal(String symbol, double signalPrice) {
        // 查找该股票代码的所有活跃警报（单次查询）
        List<Alert> alerts = alertRepository.findBySymbolAndStatus(symbol, "ACTIVE");
        if (alerts.isEmpty()) {
            return; // 提前返回，避免不必要的处理
        }

        // 处理每个警报
        for (Alert alert : alerts) {
            // 更新警报的信号价格
            alert.setSignalPrice(signalPrice);

            // 发送通知给用户
            sendNotification(alert, signalPrice);

            // 标记警报为已触发
            alert.setStatus("TRIGGERED");
            alert.setTriggered(true);
            alertRepository.save(alert);
        }
    }


    //SEND NOTIFICATION
    private void sendNotification(Alert alert, double signalPrice) {
        User user = alert.getUser();
        String email = user.getEmail();
        if(userRepository.existsById(user.getId())) {
            SimpleMailMessage message = new SimpleMailMessage();
        }
    }
}