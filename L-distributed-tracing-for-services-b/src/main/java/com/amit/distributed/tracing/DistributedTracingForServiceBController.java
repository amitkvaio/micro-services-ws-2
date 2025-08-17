package com.amit.distributed.tracing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/b")
public class DistributedTracingForServiceBController {
	
	private static final Logger log = LoggerFactory.getLogger(DistributedTracingForServiceBController.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	@GetMapping("/call")
	public String callServiceB() {
		log.info("Inside Service B");
		String response = restTemplate.getForObject("http://localhost:8002/c/call", String.class);
		return "Response from Service B → " + response;
	}
}
