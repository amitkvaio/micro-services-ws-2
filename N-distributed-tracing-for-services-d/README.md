# Distributed Tracing - Service D

This service is the last service in the distributed tracing chain.
It receives a request from Service C and returns the final response.

Request flow:

```text
Service A -> Service B -> Service C -> Service D
```

## Agenda

- [Problem we will solve](#problem-solved-in-this-chapter)
- [What you will learn](#what-you-will-learn)
- [Main explanation and examples](#purpose)
- [How to run](#how-to-run)
- [Key points or common mistakes](#key-points-or-common-mistakes)
- [Chapter Summary and Practice Task](#chapter-summary)
- [Common interview questions](#interview-questions-and-answers)


## Problem Solved In This Chapter

This chapter solves the problem of confirming that the full distributed trace reached the final service.
Service D completes the trace chain.

## Purpose

Service D confirms that the trace reaches the final downstream service.
The same Trace ID should be visible in Service A, B, C, and D logs.

## What You Will Learn

- How the final service completes the trace.
- How to confirm the same Trace ID reached the end.
- How to use Zipkin to view the complete chain.


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

## Key Points Or Common Mistakes

- Service D is the final service and should not call another service in this chapter.
- If Service D is down, the chain from Service A will fail near the end.
- Service D uses port `8003`.


## Interview Points

- Service D is the final service in the trace.
- Same Trace ID means the same request flow.
- Different Span ID means a different operation inside the same trace.
- Zipkin helps visualize the full request path and latency.

## Chapter Summary

In this chapter, Service D completed the distributed tracing flow.
Together, chapters K to N solve the problem of tracking one request across multiple services.

**Practice task:** Add one more downstream service or introduce a controlled failure, then verify the trace in Zipkin.


## Interview Questions And Answers

**Q1. What should Service D show in logs?**  
It should show the same Trace ID as Service A, B, and C, with its own Span ID.

**Q2. How does Zipkin help here?**  
Zipkin shows the full request path, service timing, and latency between services.

**Q3. What is the main benefit of distributed tracing?**  
It makes debugging easier when one request moves through many microservices.

**Q4. What port does Service D use?**  
Service D uses port `8003`.

**Q5. What should happen after Service D responds?**  
The response returns back through C, B, and A.

**Q6. What is a good next practice task?**  
Add one more service or add error handling, then observe the trace in Zipkin.