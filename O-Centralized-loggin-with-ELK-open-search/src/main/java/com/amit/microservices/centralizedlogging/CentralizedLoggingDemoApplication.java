package com.amit.microservices.centralizedlogging;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class CentralizedLoggingDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CentralizedLoggingDemoApplication.class, args);
	}
}

@RestController
class LogSearchController {

	private static final Logger log = LoggerFactory.getLogger(LogSearchController.class);

	private final List<LogEvent> logEvents = List.of(
			new LogEvent(Instant.parse("2026-10-04T10:15:01.100Z"), "api-gateway", "INFO",
					"trace-2026-elk-demo-001", "/currency-conversion/from/USD/to/INR/quantity/10",
					"Gateway routed request to currency conversion service"),
			new LogEvent(Instant.parse("2026-10-04T10:15:01.220Z"), "currency-conversion-service", "INFO",
					"trace-2026-elk-demo-001", "/currency-conversion/from/USD/to/INR/quantity/10",
					"Conversion service requested exchange value"),
			new LogEvent(Instant.parse("2026-10-04T10:15:01.390Z"), "currency-exchange-service", "ERROR",
					"trace-2026-elk-demo-001", "/currency-exchange/from/USD/to/INR",
					"Database timeout while reading exchange value"),
			new LogEvent(Instant.parse("2026-10-04T10:15:01.560Z"), "currency-conversion-service", "WARN",
					"trace-2026-elk-demo-001", "/currency-conversion/from/USD/to/INR/quantity/10",
					"Fallback response returned because exchange service failed"));

	@GetMapping("/logs/demo")
	List<LogEvent> demoLogs() {
		writeDemoLogs();
		return logEvents;
	}

	@GetMapping("/logs/search")
	List<LogEvent> search(@RequestParam(required = false) String traceId,
			@RequestParam(required = false) String serviceName,
			@RequestParam(required = false) String level) {
		writeDemoLogs();
		return logEvents.stream()
				.filter(event -> traceId == null || event.traceId().equalsIgnoreCase(traceId))
				.filter(event -> serviceName == null || event.serviceName().equalsIgnoreCase(serviceName))
				.filter(event -> level == null || event.level().equalsIgnoreCase(level))
				.toList();
	}

	private void writeDemoLogs() {
		for (LogEvent event : logEvents) {
			MDC.put("traceId", event.traceId());
			MDC.put("spanId", event.serviceName().toLowerCase(Locale.ROOT).replace("-", "_"));
			log.atLevel(org.slf4j.event.Level.valueOf(event.level()))
					.log("{} | {} | {}", event.serviceName(), event.endpoint(), event.message());
			MDC.clear();
		}
	}
}

record LogEvent(Instant timestamp, String serviceName, String level, String traceId, String endpoint, String message) {
}
