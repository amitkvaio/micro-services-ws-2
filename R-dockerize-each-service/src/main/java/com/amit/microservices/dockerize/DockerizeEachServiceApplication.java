package com.amit.microservices.dockerize;

import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class DockerizeEachServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DockerizeEachServiceApplication.class, args);
	}
}

@RestController
class DockerStatusController {

	@GetMapping("/docker/status")
	Map<String, String> status() {
		return Map.of(
				"service", "dockerized-spring-boot-service",
				"status", "UP",
				"message", "This Spring Boot microservice is ready to run inside Docker");
	}
}
