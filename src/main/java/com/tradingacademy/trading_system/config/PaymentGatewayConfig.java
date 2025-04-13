package com.tradingacademy.trading_system.config;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class PaymentGatewayConfig {

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${paypal.client-id}")
    private String paypalClientId;

    @Value("${paypal.client-secret}")
    private String paypalClientSecret;

    @Value("${paypal.mode}")
    private String paypalMode;

    @PostConstruct
    public void initStripe() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Bean
    public PayPalHttpClient payPalHttpClient() {
        PayPalEnvironment environment = "sandbox".equalsIgnoreCase(paypalMode)
                ? new PayPalEnvironment.Sandbox(paypalClientId, paypalClientSecret)
                : new PayPalEnvironment.Live(paypalClientId, paypalClientSecret);
        return new PayPalHttpClient(environment);
    }




}
