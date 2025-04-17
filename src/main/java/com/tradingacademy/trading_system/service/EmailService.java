package com.tradingacademy.trading_system.service;

import com.tradingacademy.trading_system.model.entity.User;
import com.tradingacademy.trading_system.repository.UserRepository;
import com.tradingacademy.trading_system.repository.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;

    @Autowired
    public EmailService(JavaMailSender mailSender, UserRepository userRepository, UserSessionRepository userSessionRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.userSessionRepository = userSessionRepository;
    }

    // 支付确认邮件（已实现）
    public void sendPaymentConfirmation(Long userId, Long orderId, Double amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("user not found with ID"+userId));

        String userEmail = user.getEmail();

        String subject = "Payment Confirmation - Order #" + orderId;
        String body = String.format(
                "Dear User (ID: %d),\n\n" +
                        "Thank you for your payment!\n" +
                        "Order ID: %d\n" +
                        "Amount: $%.2f\n\n" +
                        "Best regards,\n" +
                        "Trading Academy Team",
                userId, orderId, amount
        );

        sendEmail(userEmail, subject, body);
    }

    // 新增：发送注册确认邮件
    public void sendRegistrationEmail(Long userId, String username, String email) {
        String subject = "Welcome to Trading Academy!";
        String body = String.format(
                "Dear %s (ID: %d),\n\n" +
                        "Welcome to Wealthpuls!\n" +
                        "Your account has been successfully created.\n" +
                        "Username: %s\n" +
                        "Email: %s\n\n" +
                        "Start exploring our platform and enhance your trading skills!\n\n" +
                        "Best regards,\n" +
                        "Trading Academy Team",
                username, userId, username, email
        );

        sendEmail(email, subject, body);
    }
    public void sendPasswordResetEmail(Long userId, String username,String email, String resetToken){
        String resetLink = "http://wealthpuls.com/reset-password?token=" + resetToken;
        String subject = "Password Reset Request - WealthPuls";
        String body = String.format(
                "Dear %s (ID: %d),\n\n" +
                        "We received a request to reset your password.\n" +
                        "Please click the link below to reset your password:\n" +
                        "%s\n\n" +
                        "This link will expire in 24 hours. For security reasons, do not share this link with anyone.\n" +
                        "If you did not request a password reset, please ignore this email or contact our support team.\n\n" +
                        "Best regards,\n" +
                        "Trading Academy Team",
                username, userId, resetLink
        );

        sendEmail(email, subject, body);
    }

    // 抽取通用发送邮件方法
    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("tmi.investment2015@gmail.com");

        try {
            mailSender.send(message);
            System.out.println("Email sent successfully to " + to);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email to " + to + ": " + e.getMessage(), e);
        }
    }
}