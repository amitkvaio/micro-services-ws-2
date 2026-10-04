package com.amit.microservices.compose;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class DockerComposeSetupApplication {

	public static void main(String[] args) {
		SpringApplication.run(DockerComposeSetupApplication.class, args);
	}
}

@RestController
class ComposeDemoController {

	@Value("${spring.application.name}")
	private String applicationName;

	@GetMapping("/compose/status")
	Map<String, Object> status() {
		return Map.of(
				"service", applicationName,
				"status", "UP",
				"message", "This Spring Boot service can be started from Docker Compose",
				"composeServices", List.of("mysql", "eureka", "api-gateway", "currency-exchange-service",
						"currency-conversion-service"));
	}
}
