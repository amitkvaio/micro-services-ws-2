package com.amit.distributed.tracing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/c")
public class DistributedTracingForServiceCController {
	
	private static final Logger log = LoggerFactory.getLogger(DistributedTracingForServiceCController.class);
	
	@Autowired
	private RestTemplate restTemplate;

	@GetMapping("/call")
	public String callServiceC() {
		
		log.info("Inside Service C");
		String response = 
				restTemplate.getForObject("http://localhost:8003/d/call", 
						 String.class);
		return "Response from Service C → " + response;
	}
}
