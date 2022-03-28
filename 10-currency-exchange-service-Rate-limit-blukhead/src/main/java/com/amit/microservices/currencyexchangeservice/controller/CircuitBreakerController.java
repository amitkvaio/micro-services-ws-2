package com.amit.microservices.currencyexchangeservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@RestController
public class CircuitBreakerController {
	
	private Logger logger = LoggerFactory.getLogger(CircuitBreakerController.class);
	
	@GetMapping("sample-api")
	@RateLimiter(name="default")
	public String sampleApi() {
		logger.info("********** Sample-api call received ************");
		return "Sample API!!";
	}
	
	
	@GetMapping("sample-api-test")
	@RateLimiter(name="sample-api-test")
	public String sampleApiTest() {
		logger.info("********** Sample-api call received ************");
		return "Sample API Test!!";
	}
	
	@GetMapping("sample-api-bulk")
	@Bulkhead(name="sample-api-bulk")
	public String sampleApiBulkHead() {
		logger.info("********** Sample-api bulk head call received ************");
		return "Sample API for bulk head!!";
	}
	

}
