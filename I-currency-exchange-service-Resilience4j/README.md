# Currency Exchange Service With Resilience4j

This project shows how to use **Resilience4j** with Spring Boot microservices.
Resilience4j helps a service stay stable when another service is slow, down, or returning errors.

## Agenda

- [Problem we will solve](#problem-solved-in-this-chapter)
- [What you will learn](#what-you-will-learn)
- [Main explanation and examples](#dependency)
- [How to run](#how-to-run)
- [Key points or common mistakes](#key-points-or-common-mistakes)
- [Chapter Summary and Next Step](#chapter-summary)
- [Common interview questions](#interview-questions-and-answers)


## Problem Solved In This Chapter

This chapter solves the problem of one failed service breaking another service.
Retry and Circuit Breaker make service communication more stable and predictable.

## Dependency

```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot2</artifactId>
</dependency>
```

## What You Will Learn

- How Retry handles temporary failures.
- How Circuit Breaker blocks repeated failing calls.
- How fallback methods return controlled responses.


## Why We Use Resilience4j

In microservices, one service often calls another service.
If the target service fails, the caller should not hang or crash.

Resilience4j helps with:

- Retry
- Circuit Breaker
- Rate Limiter
- Bulkhead
- Time Limiter
- Fallback response

Example:

```text
Order Service -> Payment Service
```

If Payment Service is down, Order Service can return a friendly fallback message instead of failing badly.

## Retry

Retry automatically calls the failed service again before giving up.
It is useful for temporary network or service issues.

```java
@GetMapping("/currency-conversion-feign-retry/from/{from}/to/{to}/quantity/{quantity}")
@Retry(name = "currencyConversionService", fallbackMethod = "fallbackCurrencyExchangeResponse")
public CurrencyConversion calculateCurrencyConversionFeign(
        @PathVariable String from,
        @PathVariable String to,
        @PathVariable BigDecimal quantity) {

    CurrencyConversion currencyConversion = proxy.retrieveExchangeValue(from, to);

    return new CurrencyConversion(
            currencyConversion.getId(),
            from,
            to,
            quantity,
            currencyConversion.getConversionMultiple(),
            quantity.multiply(currencyConversion.getConversionMultiple()),
            currencyConversion.getEnvironment() + " Using-Feign-Client"
    );
}
```

### Fallback Method Rules

- Method name must match the value in `fallbackMethod`.
- Parameters must match the original method.
- `Throwable` can be added as the last parameter.
- Return type must be the same as the original method.
- The fallback method is usually in the same class.

```java
public CurrencyConversion fallbackCurrencyExchangeResponse(
        String from,
        String to,
        BigDecimal quantity,
        Throwable ex) {

    return new CurrencyConversion(
            1010L,
            from,
            to,
            quantity,
            BigDecimal.valueOf(65),
            BigDecimal.valueOf(65).multiply(quantity),
            "Fallback response. Please try again later. Error: " + ex.getMessage()
    );
}
```

### Retry Configuration

```properties
resilience4j.retry.instances.currencyConversionService.maxAttempts=3
resilience4j.retry.instances.currencyConversionService.waitDuration=10s
resilience4j.retry.instances.currencyConversionService.enableExponentialBackoff=true
```

- `maxAttempts=3`: try up to 3 times.
- `waitDuration=10s`: wait 10 seconds between retries.
- `enableExponentialBackoff=true`: increase wait time gradually.

## Circuit Breaker

Circuit Breaker stops calling a failing service again and again.
It protects the system from cascading failures.

```java
@GetMapping("/currency-conversion-feign-circuit-breaker/from/{from}/to/{to}/quantity/{quantity}")
@CircuitBreaker(
        name = "currencyConversionServiceWithCircuitBreaker",
        fallbackMethod = "fallbackCurrencyExchangeResponse")
public CurrencyConversion calculateCurrencyConversionFeignCircuitBreaker(
        @PathVariable String from,
        @PathVariable String to,
        @PathVariable BigDecimal quantity) {

    CurrencyConversion currencyConversion = proxy.retrieveExchangeValue(from, to);

    return new CurrencyConversion(
            currencyConversion.getId(),
            from,
            to,
            quantity,
            currencyConversion.getConversionMultiple(),
            quantity.multiply(currencyConversion.getConversionMultiple()),
            currencyConversion.getEnvironment() + " Using-Feign-Client"
    );
}
```

### Circuit Breaker Configuration

```properties
resilience4j.circuitbreaker.instances.currencyConversionServiceWithCircuitBreaker.sliding-window-size=5
resilience4j.circuitbreaker.instances.currencyConversionServiceWithCircuitBreaker.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.currencyConversionServiceWithCircuitBreaker.wait-duration-in-open-state=30s
resilience4j.circuitbreaker.instances.currencyConversionServiceWithCircuitBreaker.permitted-number-of-calls-in-half-open-state=2
```

## Circuit Breaker States

| State | Meaning |
| --- | --- |
| Closed | Normal state. Calls are allowed and failures are tracked. |
| Open | Calls are blocked and fallback is returned. |
| Half-Open | A few test calls are allowed to check if the service recovered. |

## Flow

1. Circuit Breaker watches the last 5 calls.
2. If failure rate reaches the threshold, it opens.
3. In open state, calls are blocked.
4. After the wait duration, it moves to half-open.
5. If test calls pass, it closes again.
6. If test calls fail, it opens again.

## URLs

Eureka:

```text
http://localhost:8761/
```

Currency Exchange Service:

```text
http://localhost:8000/currency-exchange/from/USD/to/INR
```

Retry API:

```text
http://localhost:8080/currency-conversion-feign-retry/from/USD/to/INR/quantity/10
```

Circuit Breaker API:

```text
http://localhost:8080/currency-conversion-feign-circuit-breaker/from/USD/to/INR/quantity/10
```

## How To Run

1. Start `A-naming-server`.
2. Start `B-currency-exchange-service`.
3. Start this project.
4. Stop `B-currency-exchange-service` and call the retry API again to test fallback.

## Key Points Or Common Mistakes

- The retry instance name in code is `currencyConversionServiceRetry`.
- Fallback method parameters must match the original method, with optional `Throwable` at the end.
- Circuit Breaker is not the same as Retry; it stops calls after repeated failures.


## Interview Points

- Retry is good for temporary failures.
- Circuit Breaker is good for repeated failures.
- Fallback gives a controlled response to the user.
- Circuit Breaker prevents one failing service from affecting the full system.

## Chapter Summary

In this chapter, Retry and Circuit Breaker handled service failures gracefully.
The next chapter solves the problem of traffic overload, too many parallel calls, and slow responses.

**Next step:** Continue with [J-currency-exchange-service-Rate-limiter-blukhead](../J-currency-exchange-service-Rate-limiter-blukhead/README.md), which solves the next remaining problem in the learning path.


## Interview Questions And Answers

**Q1. What is Resilience4j?**  
Resilience4j is a fault-tolerance library used to make microservices more reliable.

**Q2. When should we use Retry?**  
Use Retry for temporary failures like network glitches or short service downtime.

**Q3. When should we use Circuit Breaker?**  
Use Circuit Breaker when a service keeps failing and repeated calls should be stopped for some time.

**Q4. What is a fallback method?**  
A fallback method returns an alternate response when the main call fails.

**Q5. What is the difference between Retry and Circuit Breaker?**  
Retry repeats a failed call. Circuit Breaker stops calls when failures cross a limit.

**Q6. What does `sliding-window-size` mean?**  
It is the number of recent calls used to calculate the failure rate.

**Q7. What does half-open state mean?**  
The circuit allows a few test calls to check if the service recovered.