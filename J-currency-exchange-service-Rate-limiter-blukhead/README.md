# Rate Limiter, Bulkhead, And Time Limiter

This project shows more Resilience4j patterns:

- Rate Limiter
- Bulkhead
- Time Limiter
- Multiple Resilience4j annotations on one method

These patterns help protect a service from overload and slow calls.

## Agenda

- [Problem we will solve](#problem-solved-in-this-chapter)
- [What you will learn](#what-you-will-learn)
- [Main explanation and examples](#rate-limiter)
- [How to run](#how-to-run)
- [Key points or common mistakes](#key-points-or-common-mistakes)
- [Chapter Summary and Next Step](#chapter-summary)
- [Common interview questions](#interview-questions-and-answers)


## Problem Solved In This Chapter

This chapter solves the problem of service overload.
It protects the application from too many requests, too many concurrent calls, and slow methods.

## Rate Limiter

`RateLimiter` controls how many calls are allowed in a fixed time period.
It protects the service when too many requests come at once.

### Configuration

```properties
resilience4j.ratelimiter.instances.conratelimiter.limitForPeriod=5
resilience4j.ratelimiter.instances.conratelimiter.limitRefreshPeriod=10s
resilience4j.ratelimiter.instances.conratelimiter.timeoutDuration=2s
```

Meaning:

- `limitForPeriod=5`: allow only 5 calls.
- `limitRefreshPeriod=10s`: reset the counter every 10 seconds.
- `timeoutDuration=2s`: wait up to 2 seconds for permission.

### Controller Example

```java
@GetMapping("/currency-conversion-rate-limiter/from/{from}/to/{to}/quantity/{quantity}")
@RateLimiter(name = "conratelimiter", fallbackMethod = "fallbackRateLimiterResponse")
public CurrencyConversion calculateCurrencyConversionRateLimiter(
        @PathVariable String from,
        @PathVariable String to,
        @PathVariable BigDecimal quantity) throws InterruptedException {

    Thread.sleep(10000);

    return new CurrencyConversion(
            1001L,
            "USD",
            "INR",
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(82),
            BigDecimal.valueOf(820),
            "Returning_Hard_Coded_Values_For_Rate_Limiter"
    );
}
```

### Test URLs

```text
http://localhost:8000/ratelimiter
http://localhost:8000/currency-conversion-rate-limiter/from/USD/to/INR/quantity/10
```

### Windows CMD Test

This sends requests one by one:

```cmd
for /l %g in (1,1,100) do @(curl http://localhost:8000/ratelimiter & timeout /t 1)
```

This sends many parallel requests:

```cmd
for /l %g in (1,1,100) do start "" curl http://localhost:8000/currency-conversion-rate-limiter/from/USD/to/INR/quantity/10
```

## What You Will Learn

- How Rate Limiter controls request volume.
- How Bulkhead controls concurrent execution.
- How Time Limiter handles slow async calls.
- How multiple Resilience4j aspects are ordered.


## Bulkhead

`Bulkhead` limits how many concurrent calls can enter a method or service.
It isolates failures so one overloaded feature does not affect the whole system.

### Types

| Type | Meaning |
| --- | --- |
| Semaphore Bulkhead | Limits concurrent calls. Simple and fast. |
| ThreadPool Bulkhead | Runs calls in a separate thread pool. Useful for expensive work. |

### Configuration

```properties
resilience4j.bulkhead.instances.conconbulkhead.maxConcurrentCalls=5
resilience4j.bulkhead.instances.conconbulkhead.maxWaitDuration=2s
```

Meaning:

- Only 5 parallel calls are allowed.
- If all slots are busy, a request waits up to 2 seconds.
- If no slot is free, fallback is called.

### Controller Example

```java
@GetMapping("/currency-conversion-bulk-head/from/{from}/to/{to}/quantity/{quantity}")
@Bulkhead(
        name = "conconbulkhead",
        type = Bulkhead.Type.SEMAPHORE,
        fallbackMethod = "fallbackBulkHeadResponse")
public CurrencyConversion calculateCurrencyConversionBulkHead(
        @PathVariable String from,
        @PathVariable String to,
        @PathVariable BigDecimal quantity) throws InterruptedException {

    Thread.sleep(5000);

    return new CurrencyConversion(
            1001L,
            "USD",
            "INR",
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(82),
            BigDecimal.valueOf(820),
            "Returning_Hard_Coded_Values_For_BulkHead"
    );
}
```

### Test URLs

```text
http://localhost:8000/bulkhead
http://localhost:8000/currency-conversion-bulk-head/from/USD/to/INR/quantity/10
```

### Windows CMD Test

```cmd
for /l %g in (1,1,100) do start "" curl http://localhost:8000/currency-conversion-bulk-head/from/USD/to/INR/quantity/10
```

### PowerShell Test

```powershell
1..10 | ForEach-Object {
    Start-Job {
        curl http://localhost:8000/currency-conversion-bulk-head/from/USD/to/INR/quantity/10
    }
}
```

## Time Limiter

`TimeLimiter` sets a maximum time for a method call.
If the method takes too long, fallback is called.

Important: `@TimeLimiter` works with async return types like `Future`, `CompletionStage`, or `CompletableFuture`.
It does not directly work with normal synchronous return types.

### Configuration

```properties
resilience4j.timelimiter.instances.myTimeLimiter.timeout-duration=2s
resilience4j.timelimiter.instances.myTimeLimiter.cancel-running-future=true
```

### Controller Example

```java
@GetMapping("/currency-conversion-time-limiter/from/{from}/to/{to}/quantity/{quantity}")
@TimeLimiter(name = "concontimelimiter", fallbackMethod = "fallbackTimeLimiterResponse")
public CompletableFuture<CurrencyConversion> calculateCurrencyConversionTimeLimiter(
        @PathVariable String from,
        @PathVariable String to,
        @PathVariable BigDecimal quantity) {

    return CompletableFuture.supplyAsync(() -> {
        try {
            Thread.sleep(5000);
            return new CurrencyConversion(
                    1001L,
                    "USD",
                    "INR",
                    BigDecimal.valueOf(10),
                    BigDecimal.valueOf(82),
                    BigDecimal.valueOf(820),
                    "Returning_Hard_Coded_Values_For_Time_Limiter"
            );
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    });
}
```

### Test URLs

```text
http://localhost:8000/timelimiter
http://localhost:8000/currency-conversion-time-limiter/from/USD/to/INR/quantity/10
```

## Multiple Resilience4j Aspects

Resilience4j allows multiple annotations on one method.
The order is important.

Recommended order:

```properties
resilience4j.bulkhead.bulkheadAspectOrder=1
resilience4j.timelimiter.timeLimiterAspectOrder=2
resilience4j.ratelimiter.rateLimiterAspectOrder=3
resilience4j.circuitbreaker.circuitBreakerAspectOrder=4
resilience4j.retry.retryAspectOrder=5
```

## How To Run

- From this project, run `mvn spring-boot:run`.
- Open `http://localhost:8000/ratelimiter`.
- Open `http://localhost:8000/bulkhead`.
- Open `http://localhost:8000/timelimiter`.
- Use the Windows CMD or PowerShell loops in this README to create load.

## Key Points Or Common Mistakes

- Bulkhead limits concurrent calls, while Rate Limiter limits calls over time.
- Time Limiter requires async return types such as `CompletableFuture`.
- Do not use aggressive local load tests against shared or production services.


## Interview Points

- Rate Limiter controls request count.
- Bulkhead controls concurrent calls.
- Time Limiter controls maximum execution time.
- Bulkhead protects resources from overload.
- Time Limiter needs async return types because it must cancel or timeout the running task.

## Chapter Summary

In this chapter, Rate Limiter, Bulkhead, and Time Limiter protected the service from overload and delays.
The next chapter solves the problem of tracking one request across multiple microservices.

**Next step:** Continue with [K-distributed-tracing-for-services-a](../K-distributed-tracing-for-services-a/README.md), which solves the next remaining problem in the learning path.


## Interview Questions And Answers

**Q1. What is Rate Limiter?**  
Rate Limiter controls how many calls are allowed in a time period.

**Q2. What is Bulkhead?**  
Bulkhead limits concurrent calls so one busy feature does not consume all resources.

**Q3. What is Time Limiter?**  
Time Limiter fails a call if it takes more than the configured time.

**Q4. Why does Time Limiter need `CompletableFuture`?**  
Because timeout and cancellation work better with asynchronous return types.


**Q5. What is the difference between Rate Limiter and Bulkhead?**  
Rate Limiter controls call rate. Bulkhead controls parallel calls.

**Q6. What happens when Bulkhead is full?**  
The request waits if configured, or fallback is called.

**Q7. Why use Time Limiter with remote calls?**  
It prevents slow calls from blocking resources for too long.