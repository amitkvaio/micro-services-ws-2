# API Gateway With Discovery Locator

This project shows how to use **Spring Cloud Gateway** with Eureka service discovery.
The gateway becomes the single entry point for calling backend microservices.

## Agenda

- [Problem we will solve](#problem-solved-in-this-chapter)
- [What you will learn](#what-you-will-learn)
- [Main explanation and examples](#why-api-gateway-is-needed)
- [How to run](#how-to-run)
- [Key points or common mistakes](#key-points-or-common-mistakes)
- [Chapter Summary and Next Step](#chapter-summary)
- [Common interview questions](#interview-questions-and-answers)


## Problem Solved In This Chapter

This chapter solves the problem of clients calling many different microservice URLs.
The gateway provides one common entry point and forwards requests to the correct service.

## Why API Gateway Is Needed

In a microservices system, services usually run on different ports or servers.
Without a gateway, the client must know every service URL.

With API Gateway:

- The client calls one common gateway URL.
- Authentication and common checks can happen in one place.
- The gateway routes the request to the correct service.
- Internal service URLs are hidden from the client.

## What You Will Learn

- How Gateway uses Eureka service discovery.
- How discovery locator creates routes automatically.
- Why a gateway gives clients one entry point.


## Advantages

- One entry point for clients.
- Central place for authentication, monitoring, logging, and resiliency.
- No direct access to internal microservice endpoints.
- Better control over routing.

## Disadvantages

- Every request passes through the gateway, so it adds one extra hop.
- If only one gateway instance is running, it can become a single point of failure.
- In real systems, we should run multiple gateway instances behind a load balancer.

## Important Terms

### Route

A route decides where a request should go.
It contains route id, destination URI, predicates, and filters.

### Predicate

A predicate is a condition.
The route is selected only when the condition is true.

### Filter

A filter can change the request or response.
For example, it can add headers, remove headers, log requests, or rewrite paths.

## Dependency

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```

## Discovery Locator

Enable automatic route creation from Eureka:

```properties
spring.cloud.gateway.discovery.locator.enabled=true
```

When this is enabled, Gateway creates routes automatically for services registered in Eureka.

Example:

```text
http://localhost:8765/CURRENCY-EXCHANGE/currency-exchange/from/USD/to/INR
```

Here, `CURRENCY-EXCHANGE` is the service name from Eureka.

## Manual Route Example

If discovery locator is disabled, define routes manually:

```properties
spring.cloud.gateway.routes[0].id=currency-exchange
spring.cloud.gateway.routes[0].uri=lb://CURRENCY-EXCHANGE
spring.cloud.gateway.routes[0].predicates[0]=Path=/currency-exchange/**
```

## Useful Property

To disable Spring Cloud Gateway:

```properties
spring.cloud.gateway.enabled=false
```

To make service ids lowercase in the URL:

```properties
spring.cloud.gateway.discovery.locator.lower-case-service-id=true
```

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
http://localhost:8765/CURRENCY-EXCHANGE/currency-exchange/from/USD/to/INR
http://localhost:8765/CURRENCY-CONVERSION/currency-conversion-feign/from/USD/to/INR/quantity/10
http://localhost:8765/CURRENCY-CONVERSION/currency-conversion/from/USD/to/INR/quantity/10
```

## How To Run

1. Start `A-naming-server`.
2. Start `B-currency-exchange-service`.
3. Start more exchange service instances by changing the port, for example:

```text
-Dserver.port=8001
```

4. Start `C-currency-conversion-service`.
5. Start this gateway project.

## Key Points Or Common Mistakes

- Discovery locator exposes routes based on registered service ids.
- Default Eureka service ids may appear uppercase in gateway URLs.
- Do not run another gateway on port `8765` at the same time.


## Interview Points

- API Gateway is a single entry point for microservices.
- `lb://SERVICE-NAME` means route through load balancer using Eureka.
- Discovery locator can auto-create routes from Eureka services.
- In production, gateway should be highly available.

## Chapter Summary

In this chapter, Spring Cloud Gateway automatically created routes from Eureka services.
The next chapter solves the problem of making gateway URLs cleaner by using lowercase service ids.

**Next step:** Continue with [E-spring-api-gateway-enabling-discovery-locator-lower-case](../E-spring-api-gateway-enabling-discovery-locator-lower-case/README.md), which solves the next remaining problem in the learning path.


## Interview Questions And Answers

**Q1. What is API Gateway?**  
API Gateway is a single entry point that receives client requests and routes them to backend services.

**Q2. What does discovery locator do?**  
It automatically creates gateway routes for services registered in Eureka.

**Q3. What does `lb://` mean?**  
It means the gateway should use load balancing and service discovery to find the target service.

**Q4. Which property enables discovery locator?**  
`spring.cloud.gateway.discovery.locator.enabled=true` enables it.

**Q5. Why can automatic routes be risky?**  
They can expose services that you may not want clients to call directly.

**Q6. What is the gateway port here?**  
The gateway runs on port `8765`.