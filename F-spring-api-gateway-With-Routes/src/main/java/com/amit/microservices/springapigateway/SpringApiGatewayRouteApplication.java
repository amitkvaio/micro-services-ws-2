package com.amit.microservices.springapigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringApiGatewayRouteApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpringApiGatewayRouteApplication.class, args);
		System.out.println("SpringApiGatewayRouteApplication service has been started successfully!!");
	}
}
