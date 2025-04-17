package com.tradingacademy.trading_system;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TradingacademyApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradingacademyApplication.class, args);

	}

}
