package com.springcloud.gateway.logging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringCloudGatewayLoggingFilter {
	public static void main(String[] args) {
		SpringApplication.run(SpringCloudGatewayLoggingFilter.class, args);
		System.out.println("SpringCloudGatewayWithRoutesApplication service has been started successfully!!");
	}
}
