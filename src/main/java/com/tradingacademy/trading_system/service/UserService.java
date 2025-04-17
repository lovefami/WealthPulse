package com.tradingacademy.trading_system.service;

import com.tradingacademy.trading_system.exception.PasswordResetException;
import com.tradingacademy.trading_system.model.entity.PasswordResetToken;
import com.tradingacademy.trading_system.model.entity.User;
import com.tradingacademy.trading_system.repository.PasswordResetTokenRepository;
import com.tradingacademy.trading_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // Register a new user
    public User registerUser(String email, String password, String username) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setUsername(username);
        User savedUser = userRepository.save(user);

        sendRegistrationEmailAsync(savedUser);

        return savedUser;
    }

    @Async
    public void sendRegistrationEmailAsync(User user) {
        try {
            emailService.sendRegistrationEmail(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail()
            );
        } catch (Exception e) {
            System.err.println("Failed to send registration email for User ID " + user.getId() + ": " + e.getMessage());
        }
    }

    // Authenticate a user (login)
    public User loginUser(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user = userOptional.orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        return user;
    }

    // Update user profile
    public User updateUserProfile(Long userId, String username, String email) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("Email already exists: " + email);
            }
            user.setEmail(email);
        }

        if (username != null && !username.equals(user.getUsername())) {
            if (userRepository.existsByUsername(username)) {
                throw new IllegalArgumentException("Username already exists: " + username);
            }
            user.setUsername(username);
        }

        return userRepository.save(user);
    }

    // Get user details
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
    }

    // Request password reset
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setCreatedAt(LocalDateTime.now());
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        resetToken.setUsed(false);

        passwordResetTokenRepository.save(resetToken);

        sendPasswordResetEmailAsync(user, token);
    }

    @Async
    public void sendPasswordResetEmailAsync(User user, String token) {
        try {
            emailService.sendPasswordResetEmail(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    token
            );
        } catch (Exception e) {
            System.err.println("Failed to send password reset email for User ID " + user.getId() + ": " + e.getMessage());
        }
    }

    // Reset password
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new PasswordResetException(
                        "The password reset link is invalid or has expired. Please request a new one.",
                        "Invalid or expired reset token"
                ));

        // Validate token usage and expiration
        if (resetToken.isUsed()) {
            throw new PasswordResetException(
                    "This password reset link has already been used. Please request a new one.",
                    "Reset token has already been used"
            );
        }
        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new PasswordResetException(
                    "The password reset link has expired. Please request a new one.",
                    "Reset token has expired"
            );
        }

        // Update user password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }
}