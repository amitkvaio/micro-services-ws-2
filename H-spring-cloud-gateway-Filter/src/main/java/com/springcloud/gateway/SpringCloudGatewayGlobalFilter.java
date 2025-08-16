package com.springcloud.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringCloudGatewayGlobalFilter {
	public static void main(String[] args) {
		SpringApplication.run(SpringCloudGatewayGlobalFilter.class, args);
		System.out.println("SpringCloudGatewayGlobalFilter service has been started successfully!!");
	}
}
