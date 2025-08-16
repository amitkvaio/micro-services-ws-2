package com.amit.microservices.currencyexchangeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CurrencyExchangeResilience4j {
	public static void main(String[] args) {
		SpringApplication.run(CurrencyExchangeResilience4j.class, args);
		System.out.println("CurrencyExchangeResilience4j application has started successfully!!");
	}
}
