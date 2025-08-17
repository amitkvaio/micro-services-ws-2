package com.amit.distributed.tracing;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DistributedTracingForService_D {
	public static void main(String[] args) {
		SpringApplication.run(DistributedTracingForService_D.class, args);
		System.out.println("DistributedTracingForService_D has started successfull!!");
	}
}