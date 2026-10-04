# Distributed Tracing - Service B

This service is part of the distributed tracing chain.
It receives a request from Service A and calls Service C.

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

This chapter solves the problem of keeping trace context alive after the first service call.
Service B proves that the same request can be tracked across the next service.

## Purpose

Service B helps verify that the same Trace ID continues when one service calls another service.
It should log its own Span ID while keeping the same Trace ID from Service A.

## What You Will Learn

- How Service B receives trace context.
- How Service B logs its own span.
- How Service B forwards the request to Service C.


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

## Key Points Or Common Mistakes

- Do not call only Service B if you want to see the full A-to-D chain.
- The Trace ID should stay the same when B calls C.
- Service B uses port `8001`.


## Interview Points

- Service B receives the same Trace ID from Service A.
- Service B creates its own Span ID.
- The trace continues when Service B calls Service C.

## Chapter Summary

In this chapter, Service B continued the trace from Service A and called Service C.
The next chapter solves the problem of carrying the same trace through the middle layer.

**Next step:** Continue with [M-distributed-tracing-for-services-c](../M-distributed-tracing-for-services-c/README.md), which solves the next remaining problem in the learning path.


## Interview Questions And Answers

**Q1. Should Trace ID change in Service B?**  
No. Trace ID should remain the same for the same request flow.

**Q2. Should Span ID change in Service B?**  
Yes. Each service operation gets its own Span ID.

**Q3. Why is Service B important in tracing?**  
It proves that trace context can move from one service to the next.

**Q4. What port does Service B use?**  
Service B uses port `8001`.

**Q5. What service does B call next?**  
Service B calls Service C at `/c/call`.

**Q6. Why should Span ID change in B?**  
Because Service B performs a separate operation inside the same trace.