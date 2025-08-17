package com.amit.distributed.tracing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
@RestController
@RequestMapping("/d")
public class DistributedTracingForServiceDController {

	private static final Logger log = LoggerFactory.getLogger(DistributedTracingForServiceDController.class);
	
	@GetMapping("/call")
	public String callServiceD() {
		log.info("Inside Service D");
		return "Response from Service D";
	}
}
