package com.amit.distributed.tracing;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DistributedTracingForService_C {
	public static void main(String[] args) {
		SpringApplication.run(DistributedTracingForService_C.class, args);
		System.out.println("DistributedTracingForService_C has started successfull!!");
	}
}