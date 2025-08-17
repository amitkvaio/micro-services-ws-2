package com.amit.distributed.tracing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/a")
public class DistributedTracingForServiceAController {
	
	private static final Logger log = LoggerFactory.getLogger(DistributedTracingForServiceAController.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	@GetMapping("/call")
	public String callServiceA() {
		log.info("Inside Service A");
		String response = restTemplate.getForObject("http://localhost:8001/b/call", String.class);
		return "Response from Service A → " + response;
	}
}
