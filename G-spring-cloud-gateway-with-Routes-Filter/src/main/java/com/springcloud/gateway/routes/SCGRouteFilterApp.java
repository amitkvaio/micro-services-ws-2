package com.springcloud.gateway.routes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SCGRouteFilterApp {
	public static void main(String[] args) {
		SpringApplication.run(SCGRouteFilterApp.class, args);
		System.out.println("Spring Cloud Gateway RouteFilterApp service has been started successfully!!");
	}
}
