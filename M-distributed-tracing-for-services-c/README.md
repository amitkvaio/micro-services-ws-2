# Distributed Tracing - Service C

This service is part of the distributed tracing chain.
It receives a request from Service B and calls Service D.

Request flow:

```text
Service A -> Service B -> Service C -> Service D
```

## Agenda

- Receive the traced request from Service B.
- Log trace details in the middle service.
- Call Service D with the same trace context.
- Verify the full chain is still connected.

## Problem Solved In This Chapter

This chapter solves the problem of losing trace context in the middle of a service chain.
Service C keeps the same Trace ID while creating its own Span ID.

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

## Chapter Summary

In this chapter, Service C continued the trace and called Service D.
The next chapter solves the problem of confirming the final service received the same request trace.

## Interview Questions And Answers

**Q1. Why is Service C called a middle service here?**  
Because it receives a request from Service B and forwards the flow to Service D.

**Q2. What should we check in Service C logs?**  
Check that the Trace ID is the same and the Span ID is different.

**Q3. Why is this useful in production?**  
It helps find where latency or failure happens in a long service chain.
