package com.amit.microservices.kubernetes;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class KubernetesBasicsApplication {

	public static void main(String[] args) {
		SpringApplication.run(KubernetesBasicsApplication.class, args);
	}
}

@RestController
class KubernetesDemoController {

	@Value("${spring.application.name}")
	private String serviceName;

	@GetMapping("/kubernetes/status")
	Map<String, String> status() throws UnknownHostException {
		return Map.of(
				"service", serviceName,
				"status", "UP",
				"podHostName", InetAddress.getLocalHost().getHostName(),
				"message", "This Spring Boot microservice is running in Kubernetes style");
	}
}
