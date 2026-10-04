# Distributed Tracing - Service C

This service is part of the distributed tracing chain.
It receives a request from Service B and calls Service D.

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
- [Chapter Summary and Next Step](#chapter-summary)
- [Common interview questions](#interview-questions-and-answers)


## Problem Solved In This Chapter

This chapter solves the problem of losing trace context in the middle of a service chain.
Service C keeps the same Trace ID while creating its own Span ID.

## Purpose

Service C shows how tracing continues through the middle of a microservice call chain.
The Trace ID should remain the same, and Service C should create a new Span ID.

## What You Will Learn

- How Service C continues the trace.
- How Service C calls Service D.
- How middle services help identify latency.


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

## Key Points Or Common Mistakes

- Service C must run on port `8002` for the current hardcoded call chain.
- Keep the same Zipkin endpoint across all tracing services.
- Trace ID should not change between B and C.


## Interview Points

- Service C is a downstream service in the trace flow.
- The Trace ID remains common across all services.
- Each service creates a separate Span ID.

## Chapter Summary

In this chapter, Service C continued the trace and called Service D.
The next chapter solves the problem of confirming the final service received the same request trace.

**Next step:** Continue with [N-distributed-tracing-for-services-d](../N-distributed-tracing-for-services-d/README.md), which solves the next remaining problem in the learning path.


## Interview Questions And Answers

**Q1. Why is Service C called a middle service here?**  
Because it receives a request from Service B and forwards the flow to Service D.

**Q2. What should we check in Service C logs?**  
Check that the Trace ID is the same and the Span ID is different.

**Q3. Why is this useful in production?**  
It helps find where latency or failure happens in a long service chain.

**Q4. What port does Service C use?**  
Service C uses port `8002`.

**Q5. What service does C call next?**  
Service C calls Service D at `/d/call`.

**Q6. How does tracing help with middle services?**  
It shows whether the delay or failure happened before, inside, or after that service.