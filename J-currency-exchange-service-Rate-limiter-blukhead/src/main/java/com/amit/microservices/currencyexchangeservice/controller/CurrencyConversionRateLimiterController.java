package com.amit.microservices.currencyexchangeservice.controller;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.amit.microservices.currencyexchangeservice.model.CurrencyConversion;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@RestController
public class CurrencyConversionRateLimiterController {
	
	private Logger logger = LoggerFactory.getLogger(CurrencyConversionRateLimiterController.class);
	
	@Autowired
	private Environment environment;
	 
	@GetMapping("/ratelimiter")
	public String getRateLimiterController() {
		logger.info("###### getRateLimiterController call called from Rate Limiter controller #######");
		return "Rate_Limiter_Controller";
	}
	
	@GetMapping("/currency-conversion-rate-limiter/from/{from}/to/{to}/quantity/{quantity}")
	@RateLimiter(name = "conratelimiter", fallbackMethod = "fallbackRateLimiterResponse")
	public CurrencyConversion calculateCurrencyConversionRateLimiter(
			@PathVariable String from, 
			@PathVariable String to,
			@PathVariable BigDecimal quantity) throws InterruptedException {
		
		logger.info("########## called from calculateCurrencyConversionRateLimiter funtion from CurrencyConversionRateLimiterController class ################");
		Thread.sleep(10000);
		logger.info("########## after sleep from calculateCurrencyConversionRateLimiter funtion from CurrencyConversionRateLimiterController class ################");
		
		String port = environment.getProperty("local.server.port") + "_Returning_Hard_Coded_Values_For_Rate_Limiter";
		
		CurrencyConversion conversion1 = new CurrencyConversion(
		        1001L,
		        "USD",
		        "INR",
		        BigDecimal.valueOf(10),
		        BigDecimal.valueOf(82), // conversion rate
		        BigDecimal.valueOf(820), // total = 10 * 82
		        port
		);
		 return conversion1;
	}
	
	// Fallback method (must have same parameters as original + Exception as last arg)
	public CurrencyConversion fallbackRateLimiterResponse(
	        String from,
	        String to,
	        BigDecimal quantity,
	        Throwable ex) {

	    logger.info("####### Fallback method has triggered of class CurrencyConversionRateLimiterController due to: " + ex.getMessage());

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
