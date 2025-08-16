package com.amit.microservices.currencyexchangeservice.controller;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.amit.microservices.currencyexchangeservice.model.CurrencyConversion;

import io.github.resilience4j.retry.annotation.Retry;

@RestController
public class Resilience4jRetryController {
	
	private Logger logger = LoggerFactory.getLogger(Resilience4jRetryController.class);
	
	@Autowired
	private com.amit.microservices.currencyexchangeservice.proxy.CurrencyExchangeProxy proxy;
	
	@GetMapping("/currency-conversion-feign-retry/from/{from}/to/{to}/quantity/{quantity}")
	// Retry mechanism applied here
    @Retry(name = "currencyConversionServiceRetry", fallbackMethod = "fallbackCurrencyExchangeResponse")
	public CurrencyConversion calculateCurrencyConversionFeign(
			@PathVariable String from, 
			@PathVariable String to,
			@PathVariable BigDecimal quantity) {
		
		logger.info("########## called from calculateCurrencyConversionFeign funtion from Resilience4jRetryController class ################");
		
		// This might fail (for example, service down or network issue)
       	CurrencyConversion currencyConversion = proxy.retrieveExchangeValue(from, to);

		return new CurrencyConversion(currencyConversion.getId(), from, to, quantity,
				currencyConversion.getConversionMultiple(),
				quantity.multiply(currencyConversion.getConversionMultiple()),
				currencyConversion.getEnvironment() + " " + "Using-Feign-Client-Retry");
	}
	
	// Fallback method (must have same parameters as original + Exception as last arg)
	public CurrencyConversion fallbackCurrencyExchangeResponse(
	        String from,
	        String to,
	        BigDecimal quantity,
	        Throwable ex) {

	    logger.info("####### Fallback triggered of class Resilience4jRetryController due to: " + ex.getMessage());

	    return new CurrencyConversion(
	            1010L,  // dummy id
	            from,
	            to,
	            quantity,
	            BigDecimal.valueOf(65),  // default conversion rate
	            BigDecimal.valueOf(65).multiply(quantity), // total amount
	            "Fallback response: ohh! Sorry!! Looks like there are some technical problems. Please try again later. Error: Retry :" + ex.getMessage()
	    );
	}
}

/*
First Run : A-naming-server application
Second Run : B-currency-exchange-service application
Third Run : I-currency-exchange-service-Resilience4jRetry

Check the respective services whether it is running or not.

Eureka
http://localhost:8761/

Currency Exchange Service
http://localhost:8000/currency-exchange/from/USD/to/INR

8-currency-exchange-service-Resilience4jRetry
http://localhost:8080/currency-conversion-feign-retry/from/USD/to/INR/quantity/10

Stop : B-currency-exchange-service application
http://localhost:8000/currency-conversion-feign-retry/from/USD/to/INR/quantity/10

Check the log whether Retry logic is working or not by looking into the logs.

*/