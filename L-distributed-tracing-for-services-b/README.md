# Distributed Tracing - Service B

This service is part of the distributed tracing chain.
It receives a request from Service A and calls Service C.

Request flow:

```text
Service A -> Service B -> Service C -> Service D
```

## Purpose

Service B helps verify that the same Trace ID continues when one service calls another service.
It should log its own Span ID while keeping the same Trace ID from Service A.

## Configuration

```properties
spring.application.name=distributed-tracing-for-service-b
server.port=8001

management.tracing.sampling.probability=1.0
management.zipkin.tracing.endpoint=http://localhost:9411/api/v2/spans

logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p [traceId: %X{traceId},spanId: %X{spanId}] %-40.40c{1.} : %m%n
```

## API

```text
http://localhost:8001/b/call
```

## How To Run

1. Start Zipkin.
2. Start Service A.
3. Start Service B.
4. Start Service C.
5. Start Service D.
6. Call `http://localhost:8000/a/call`.

## Interview Points

- Service B receives the same Trace ID from Service A.
- Service B creates its own Span ID.
- The trace continues when Service B calls Service C.
