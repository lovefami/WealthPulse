package com.tradingacademy.trading_system.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.tradingacademy.trading_system.model.entity.User}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDTO implements Serializable {
    private String username;
    private String password;
    private String email;
}