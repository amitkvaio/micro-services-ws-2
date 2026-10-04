# Spring Cloud Gateway Global Filter

This project shows how to use a `GlobalFilter` in Spring Cloud Gateway.
A global filter runs for every request that passes through the gateway.

## Agenda

- Understand `GlobalFilter`.
- Add common pre-filter and post-filter logic.
- Log request and response information.
- Learn where authentication, tracing, and metrics can be added.

## Problem Solved In This Chapter

This chapter solves the problem of repeating common logic in every route.
A global filter applies shared behavior once at the gateway level.

## What Is `GlobalFilter`?

`GlobalFilter` is an interface in Spring Cloud Gateway.
It is used for common logic that should apply to all routes.

Common examples:

- Authentication and authorization.
- Request and response logging.
- Adding or removing headers.
- Rate limiting.
- Metrics and tracing.
- Tenant or business validations.

## When It Executes

A global filter can run:

- Before the request is sent to the backend service.
- After the response comes back from the backend service.

The order is controlled by the `getOrder()` method.
Lower order value means higher priority.

## Example

```java
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("@LoggingFilter Global PRE Filter: Request Path Received -> {}",
                exchange.getRequest().getPath());

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            System.out.println("@LoggingFilter Global POST Filter: Response status -> "
                    + exchange.getResponse().getStatusCode());
        }));
    }

    @Override
    public int getOrder() {
        logger.info("@LoggingFilter Global PRE Filter: getOrder method!");
        return -1;
    }
}
```

## Notes

- `GlobalFilter` is applied to all routes.
- It is useful for cross-cutting concerns.
- It can perform both pre-filter and post-filter logic.
- It should be kept lightweight because every request passes through it.

## URLs

Currency Exchange Service:

```text
http://localhost:8000/currency-exchange/from/USD/to/INR
```

Currency Conversion Service:

```text
http://localhost:8100/currency-conversion/from/USD/to/INR/quantity/10
http://localhost:8100/currency-conversion-feign/from/USD/to/INR/quantity/10
```

Eureka:

```text
http://localhost:8761/
```

API Gateway:

```text
http://localhost:8765
http://localhost:8765/currency-exchange/from/USD/to/INR
http://localhost:8765/currency-conversion/from/USD/to/INR/quantity/10
http://localhost:8765/currency-conversion-feign/from/USD/to/INR/quantity/10
http://localhost:8765/currency-conversion-new/from/USD/to/INR/quantity/10
```

## Sample Console Logs

```text
Global PRE Filter: JwtAuthGlobalFilter started
Global PRE Filter: JwtAuthGlobalFilter jwt token has validated successfully!
@LoggingFilter Global PRE Filter: Request Path Received -> /get
@LoggingFilter Global POST Filter: Response status -> 200 OK
Global POST Filter: JwtAuthGlobalFilter Response status -> 200 OK
```

## Interview Points

- Route filters apply to selected routes.
- Global filters apply to all gateway requests.
- Global filters are good for security, logging, tracing, and metrics.
- Keep global filters fast because they affect every request.

## Chapter Summary

In this chapter, global filters added common behavior to all gateway requests.
The next chapter solves the problem of service failures using Resilience4j retry and circuit breaker.

## Interview Questions And Answers

**Q1. What is `GlobalFilter`?**  
`GlobalFilter` is a Spring Cloud Gateway filter that runs for all routes.

**Q2. Where can JWT validation be added in Gateway?**  
JWT validation can be added in a global filter so every request is checked.

**Q3. Why should global filters be lightweight?**  
Every gateway request passes through them, so heavy logic can slow the whole system.
