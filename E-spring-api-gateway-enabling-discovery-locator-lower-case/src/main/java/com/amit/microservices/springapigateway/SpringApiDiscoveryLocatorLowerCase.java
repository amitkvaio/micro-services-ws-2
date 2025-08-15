package com.amit.microservices.springapigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringApiDiscoveryLocatorLowerCase {
	public static void main(String[] args) {
		SpringApplication.run(SpringApiDiscoveryLocatorLowerCase.class, args);
		System.out.println("SpringApiDiscoveryLocatorLowerCase service has been started successfully!!");
	}
}
