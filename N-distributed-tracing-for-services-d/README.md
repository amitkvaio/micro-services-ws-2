# Distributed Tracing - Service D

This service is the last service in the distributed tracing chain.
It receives a request from Service C and returns the final response.

Request flow:

```text
Service A -> Service B -> Service C -> Service D
```

## Purpose

Service D confirms that the trace reaches the final downstream service.
The same Trace ID should be visible in Service A, B, C, and D logs.

## Configuration

```properties
spring.application.name=distributed-tracing-for-service-d
server.port=8003

management.tracing.sampling.probability=1.0
management.zipkin.tracing.endpoint=http://localhost:9411/api/v2/spans

logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p [traceId: %X{traceId},spanId: %X{spanId}] %-40.40c{1.} : %m%n
```

## API

```text
http://localhost:8003/d/call
```

## How To Run

1. Start Zipkin.
2. Start Service A, B, C, and D.
3. Call `http://localhost:8000/a/call`.
4. Verify the same Trace ID in all service logs.

## Interview Points

- Service D is the final service in the trace.
- Same Trace ID means the same request flow.
- Different Span ID means a different operation inside the same trace.
- Zipkin helps visualize the full request path and latency.
