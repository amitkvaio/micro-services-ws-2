package com.amit.microservices.currencyexchangeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CurrencyExchangeRateLimitBulkHeadApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyExchangeRateLimitBulkHeadApplication.class, args);
		System.out.println("CurrencyExchangeRateLimitBulkHeadApplication has started successfully!!");
	}

}
