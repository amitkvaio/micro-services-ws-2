package com.amit.microservices.eventdriven;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class EventDrivenDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventDrivenDemoApplication.class, args);
	}
}

@RestController
class EventDemoController {

	private final List<OrderCreatedEvent> broker = new ArrayList<>();
	private final List<String> consumedMessages = new ArrayList<>();

	@PostMapping("/events/orders")
	OrderCreatedEvent publish(@RequestParam(defaultValue = "ORD-1001") String orderId,
			@RequestParam(defaultValue = "2500") double amount) {
		OrderCreatedEvent event = new OrderCreatedEvent("evt-" + orderId, "OrderCreated",
				"trace-" + orderId.toLowerCase(), orderId, amount, "INR", Instant.now());
		broker.add(event);
		return event;
	}

	@PostMapping("/events/consume")
	List<String> consume() {
		consumedMessages.clear();
		for (OrderCreatedEvent event : broker) {
			consumedMessages.add("Payment Service consumed " + event.orderId() + " using " + event.traceId());
			consumedMessages.add("Inventory Service consumed " + event.orderId() + " using " + event.traceId());
			consumedMessages.add("Notification Service consumed " + event.orderId() + " using " + event.traceId());
		}
		return consumedMessages;
	}

	@GetMapping("/events/demo")
	EventDemoResponse demo() {
		broker.clear();
		publish("ORD-1001", 2500);
		publish("ORD-1002", 900);
		return new EventDemoResponse(broker, consume());
	}

	@GetMapping("/events/orders")
	List<OrderCreatedEvent> events() {
		return broker;
	}
}

record OrderCreatedEvent(String eventId, String eventType, String traceId, String orderId, double amount,
		String currency, Instant createdAt) {
}

record EventDemoResponse(List<OrderCreatedEvent> publishedEvents, List<String> consumerOutput) {
}
