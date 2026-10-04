package com.amit.microservices.saga;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SagaPatternDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SagaPatternDemoApplication.class, args);
	}
}

@RestController
class SagaDemoController {

	@GetMapping("/saga/success")
	List<SagaStep> success() {
		return List.of(
				new SagaStep("saga-order-1001", "Order Service", "Create order", "PENDING"),
				new SagaStep("saga-order-1001", "Payment Service", "Take payment", "PAID"),
				new SagaStep("saga-order-1001", "Inventory Service", "Reserve stock", "RESERVED"),
				new SagaStep("saga-order-1001", "Order Service", "Confirm order", "CONFIRMED"));
	}

	@GetMapping("/saga/failure")
	List<SagaStep> failureWithCompensation() {
		return List.of(
				new SagaStep("saga-order-1002", "Order Service", "Create order", "PENDING"),
				new SagaStep("saga-order-1002", "Payment Service", "Take payment", "PAID"),
				new SagaStep("saga-order-1002", "Inventory Service", "Reserve stock", "FAILED"),
				new SagaStep("saga-order-1002", "Payment Service", "Refund payment", "REFUNDED"),
				new SagaStep("saga-order-1002", "Order Service", "Cancel order", "CANCELLED"));
	}

	@GetMapping("/saga/demo")
	SagaDemoResponse demo() {
		return new SagaDemoResponse(success(), failureWithCompensation(),
				"Saga uses local transactions. If a later step fails, compensation fixes earlier completed steps.");
	}
}

record SagaStep(String sagaId, String serviceName, String action, String status) {
}

record SagaDemoResponse(List<SagaStep> successFlow, List<SagaStep> failureFlow, String learning) {
}
