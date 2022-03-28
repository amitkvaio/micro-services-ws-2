package com.amit.microservices.currencyexchangeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CurrencyExchangeResilience4jRetry {
	public static void main(String[] args) {
		SpringApplication.run(CurrencyExchangeResilience4jRetry.class, args);
		System.out.println("CurrencyExchangeCircuitBreaker application has started successfully!!");
	}

}
