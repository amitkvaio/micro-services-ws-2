package com.amit.microservices.currencyexchangeservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.slf4j.*;

import io.github.resilience4j.retry.annotation.Retry;

@RestController
public class CircuitBreakerController {
	
	private Logger logger = LoggerFactory.getLogger(CircuitBreakerController.class);
	
	@GetMapping("sample-api")
	//@Retry(name="default")
	@Retry(name="sample-api", fallbackMethod ="hardcodedResponse")
	public String sampleApi() {
		logger.info("********** Sample-api call received ************");
		ResponseEntity<String> forEntity = 
					new RestTemplate().getForEntity("http://localhost:8080/some-dummy-rul", String.class);
		
		return forEntity.getBody();
	}
	
	public String hardcodedResponse(Exception exe) {
		return "fallback-response";
	}
}
