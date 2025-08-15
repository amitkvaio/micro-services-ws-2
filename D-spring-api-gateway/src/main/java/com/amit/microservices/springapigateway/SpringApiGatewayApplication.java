package com.amit.microservices.springapigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringApiGatewayApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpringApiGatewayApplication.class, args);
		System.out.println("SpringApiGatewayApplication service has been started successfully!!");
	}
}
