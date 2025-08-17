package com.amit.microservices.currencyexchangeservice.controller;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.amit.microservices.currencyexchangeservice.model.CurrencyConversion;

import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

@RestController
public class CurrencyConversionTimeLimiterController {
	
	private Logger logger = LoggerFactory.getLogger(CurrencyConversionTimeLimiterController.class);
	
	@Autowired
	private Environment environment;
	
	@GetMapping("/timelimiter")
	public String getTimeLimiterController() {
		logger.info("###### getTimeLimiterController call called from CurrencyConversionTimeLimiterController controller #######");
		return "Time_Limiter_Controller";
	}
	
	@GetMapping("/currency-conversion-time-limiter/from/{from}/to/{to}/quantity/{quantity}")
	@TimeLimiter(name = "concontimelimiter", fallbackMethod = "fallbackTimeLimiterResponse")
	public CompletableFuture<CurrencyConversion> calculateCurrencyConversionTimeLimiter(
	        @PathVariable String from,
	        @PathVariable String to,
	        @PathVariable BigDecimal quantity) {
	    
	    return CompletableFuture.supplyAsync(() -> {
	        try {
	            logger.info("########## Before sleep in calculateCurrencyConversionTimeLimiter method ##########");
	            // simulate delay
	            Thread.sleep(5000);
	            logger.info("########## After sleep in calculateCurrencyConversionTimeLimiter method ##########");
	            String port = environment.getProperty("local.server.port")
	                    + "_Returning_Hard_Coded_Values_For_Time_Limiter";

	            // create object
	            CurrencyConversion conversion1 = new CurrencyConversion(
	                    1001L,
	                    "USD",
	                    "INR",
	                    BigDecimal.valueOf(10),
	                    BigDecimal.valueOf(82),   // conversion rate
	                    BigDecimal.valueOf(820),  // total = 10 * 82
	                    port
	            );
	            return conversion1;
	        } catch (InterruptedException e) {
	            throw new RuntimeException(e);
	        }
	    });
	}

	
	// Fallback method (must have same parameters as original + Exception as last arg)
	public CompletableFuture<CurrencyConversion> fallbackTimeLimiterResponse(
	        String from,
	        String to,
	        BigDecimal quantity,
	        Throwable ex) {

	    logger.info("####### Fallback mehtod has triggered of class CurrencyConversionTimeLimiterController due to: " + ex.getMessage());

	    CurrencyConversion fallbackResponse = new CurrencyConversion(
	            1010L,  // dummy id
	            from,
	            to,
	            quantity,
	            BigDecimal.valueOf(65),  // default conversion rate
	            BigDecimal.valueOf(65).multiply(quantity), // total amount
	            "Fallback response: ohh! Sorry!! Looks like there are some technical problems. Please try again later. Error: Retry :" + ex.getMessage()
	    );
	    // wrap inside CompletableFuture
	    return CompletableFuture.completedFuture(fallbackResponse);
	}
}
