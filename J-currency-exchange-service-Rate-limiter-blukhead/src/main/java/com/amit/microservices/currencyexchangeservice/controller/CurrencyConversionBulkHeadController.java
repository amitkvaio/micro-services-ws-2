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

import io.github.resilience4j.bulkhead.annotation.Bulkhead;

@RestController
public class CurrencyConversionBulkHeadController {
	
	private Logger logger = LoggerFactory.getLogger(CurrencyConversionBulkHeadController.class);
	
	@Autowired
	private Environment environment;
	
	@GetMapping("/bulkhead")
	public String getBulkHeadController() {
		logger.info("###### getBulkHeadController call called from Bulk Head controller #######");
		return "Bulk_Head_Controller";
	}
	
	@GetMapping("/currency-conversion-bulk-head/from/{from}/to/{to}/quantity/{quantity}")
	@Bulkhead(name = "conconbulkhead", type = Bulkhead.Type.SEMAPHORE, fallbackMethod = "fallbackBulkHeadResponse")
	public CurrencyConversion calculateCurrencyConversionBulkHead(
			@PathVariable String from, 
			@PathVariable String to,
			@PathVariable BigDecimal quantity) throws InterruptedException {
		
		logger.info("##########Befor sleep called from calculateCurrencyConversionBulkHead funtion from CurrencyConversionBulkHeadController class ################");
		Thread.sleep(5000);
		logger.info("##########After sleep called from calculateCurrencyConversionBulkHead funtion from CurrencyConversionBulkHeadController class ################");
		String port = environment.getProperty("local.server.port") + "_Returning_Hard_Coded_Values_For_BulkHead";
		
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
	public CurrencyConversion fallbackBulkHeadResponse(
	        String from,
	        String to,
	        BigDecimal quantity,
	        Throwable ex) {

	    logger.info("####### Fallback mehtod has triggered of class CurrencyConversionBulkHeadController due to: " + ex.getMessage());

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
