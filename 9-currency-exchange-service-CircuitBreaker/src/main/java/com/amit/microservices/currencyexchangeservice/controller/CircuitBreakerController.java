package com.amit.microservices.currencyexchangeservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@RestController
public class CircuitBreakerController {
	
	private Logger logger = LoggerFactory.getLogger(CircuitBreakerController.class);
	
	@GetMapping("sample-api-fallback")
	@CircuitBreaker(name="default", fallbackMethod ="hardcodedResponsewithexception")
	public String sampleApiWithExceptionFallback() {
		logger.info("********** SampleApiWithExceptionFallback call received ************");
		ResponseEntity<String> forEntity = 
					new RestTemplate().getForEntity("http://localhost:8080/some-dummy-url", String.class);
		return forEntity.getBody();
	}
	
	public String hardcodedResponsewithexception(Exception exe) {
		return "fallback-response-with-CircuitBreaker";
	}
}
