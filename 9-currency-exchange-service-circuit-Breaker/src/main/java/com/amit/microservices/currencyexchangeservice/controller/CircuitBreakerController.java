package com.amit.microservices.currencyexchangeservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.slf4j.*;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

@RestController
public class CircuitBreakerController {
	
	private Logger logger = LoggerFactory.getLogger(CircuitBreakerController.class);
	
	@GetMapping("sample-api")
	//@Retry(name="default")
	//@Retry(name="sample-api", fallbackMethod ="hardcodedResponse")
	@CircuitBreaker(name="default", fallbackMethod ="hardcodedResponse")
	public String sampleApi() {
		logger.info("********** Sample-api call received ************");
		ResponseEntity<String> forEntity = 
					new RestTemplate().getForEntity("http://localhost:8080/some-dummy-rul", String.class);
		
		return forEntity.getBody();
	}
	
	public String hardcodedResponse(Exception exe) {
		return "fallback-response";
	}
	
	@GetMapping("sample-ratelimiter")
	@RateLimiter(name="default")
	public String rateLimiter() {
		logger.info("********** rateLimiter call received ************");	
		return "****** Sample Rate limiter *****";
	}
	
	@GetMapping("sample-bulkhead-default")
	@Bulkhead(name="default")
	public String bulkHeadDefault() {
		logger.info("********** bulkHead call received ************");	
		return "****** Bulk Head *****";
	}
	
	@GetMapping("sample-bulkhead")
	@Bulkhead(name="sample-bulkhead")
	public String bulkHead() {
		logger.info("********** bulkHead call received ************");	
		return "****** Bulk Head *****";
	}
}
