package com.tradingacademy.trading_system.service;

import com.tradingacademy.trading_system.model.entity.User;
import com.tradingacademy.trading_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Register a new user
    public User registerUser(String email, String password, String username) {
        // Validate email and username uniqueness
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Hash the password
        user.setUsername(username);
        return userRepository.save(user);
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

        // Check email uniqueness if email is being updated
        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("Email already exists: " + email);
            }
            user.setEmail(email);
        }

        // Check username uniqueness if username is being updated
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
}