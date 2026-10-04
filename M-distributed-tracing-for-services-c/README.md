# Distributed Tracing - Service C

This service is part of the distributed tracing chain.
It receives a request from Service B and calls Service D.

Request flow:

```text
Service A -> Service B -> Service C -> Service D
```

## Purpose

Service C shows how tracing continues through the middle of a microservice call chain.
The Trace ID should remain the same, and Service C should create a new Span ID.

## Configuration

```properties
spring.application.name=distributed-tracing-for-service-c
server.port=8002

management.tracing.sampling.probability=1.0
management.zipkin.tracing.endpoint=http://localhost:9411/api/v2/spans

logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p [traceId: %X{traceId},spanId: %X{spanId}] %-40.40c{1.} : %m%n
```

## API

```text
http://localhost:8002/c/call
```

## How To Run

1. Start Zipkin.
2. Start Service A, B, C, and D.
3. Call `http://localhost:8000/a/call`.
4. Check the console logs and Zipkin dashboard.

## Interview Points

- Service C is a downstream service in the trace flow.
- The Trace ID remains common across all services.
- Each service creates a separate Span ID.
