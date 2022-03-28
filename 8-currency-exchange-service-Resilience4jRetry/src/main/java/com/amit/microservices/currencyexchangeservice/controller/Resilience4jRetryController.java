package com.amit.microservices.currencyexchangeservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

@RestController
public class Resilience4jRetryController {
	
	private Logger logger = LoggerFactory.getLogger(Resilience4jRetryController.class);
	
	//http://localhost:8000/sample-api-test : it will return output as Sample-API
	@GetMapping("sample-api-test")
	public String sampleApi_() {
		return "Sample-API";
	}
	
	//http://localhost:8000/sample-api-fail : it will throw the exception.
	@GetMapping("sample-api-fail")
	public String sampleApiFail() {
		logger.info("********** Sample-apiFail call received ************");
		ResponseEntity<String> forEntity = 
					new RestTemplate().getForEntity("http://localhost:8080/some-dummy-url", String.class);
		//Calling dummy API to make it fail.
		return forEntity.getBody();
	}
	
	
	@GetMapping("sample-api-default")
	@Retry(name="default")
	public String sampleApiDefault() {
		logger.info("********** Sample-api-default call received ************");
		ResponseEntity<String> forEntity = 
					new RestTemplate().getForEntity("http://localhost:8080/some-dummy-url", String.class);
		
		//Calling dummy API to make it fail and if it is there is any failure for while execution of this
		//method or API then it will call three times same api if it is not getting the proper response. 
		//it will throw the exception.
		return forEntity.getBody();
	}
	
	@GetMapping("sample-api-maxretry")
	@Retry(name="sample-api-maxretry")
	public String sampleApiMaxRetry() {
		logger.info("********** Sample-api-maxretry call received ************");
		ResponseEntity<String> forEntity = 
					new RestTemplate().getForEntity("http://localhost:8080/some-dummy-url", String.class);
		return forEntity.getBody();
		//resilience4j.retry.instances.sample-api-maxretry.maxAttempts=5 - application.property file
		// This method will get called 5 times if all the attempts it will not get the proper
		//response then it will throw the error/Exception.
	}
	
	@GetMapping("sample-api-fallback")
	@Retry(name="sample-api-fallback", fallbackMethod ="hardcodedResponseWithoutParameter")
	public String sampleApiUsingFallBackMethod() {
		logger.info("********** SampleApiUsingFallBackMethod call received ************");
		ResponseEntity<String> forEntity = 
					new RestTemplate().getForEntity("http://localhost:8080/some-dummy-url", String.class);
		//It will not give proper response because fallback method is not catching the exception
		return forEntity.getBody();
	}
	
	@GetMapping("sample-api-fallback-withexception")
	@Retry(name="sample-api-fallback-withexception", fallbackMethod ="hardcodedResponsewithexception")
	//@CircuitBreaker(name="default", fallbackMethod ="hardcodedResponse")
	public String sampleApiWithExceptionFallback() {
		logger.info("********** SampleApiWithExceptionFallback call received ************");
		ResponseEntity<String> forEntity = 
					new RestTemplate().getForEntity("http://localhost:8080/some-dummy-url", String.class);
		//it will call the fall back method called hardcodedResponsewithexception as configured above
		return forEntity.getBody();
	}
	
	public String hardcodedResponseWithoutParameter() {
		return "fallback-response-WithoutParameter";
	}

	
	public String hardcodedResponsewithexception(Exception exe) {
		return "fallback-response";
	}

}
