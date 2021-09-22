package com.amit.microservices.springapigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringApiEnablingDiscoveryLocatorGatewayApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpringApiEnablingDiscoveryLocatorGatewayApplication.class, args);
		System.out.println("SpringApiEnablingDiscoveryLocatorGatewayApplication service has been started successfully!!");
	}
}
