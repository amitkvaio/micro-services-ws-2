# API Gateway With Manual Routes

This project shows how to define Spring Cloud Gateway routes in Java code.
Instead of depending only on discovery locator, we manually decide which paths should go to which service.

## Agenda

- [Problem we will solve](#problem-solved-in-this-chapter)
- [What you will learn](#what-you-will-learn)
- [Main explanation and examples](#route-configuration)
- [How to run](#how-to-run)
- [Key points or common mistakes](#key-points-or-common-mistakes)
- [Chapter Summary and Next Step](#chapter-summary)
- [Common interview questions](#interview-questions-and-answers)


## Problem Solved In This Chapter

This chapter solves the problem of exposing every Eureka service automatically.
Manual routes give better control over which APIs are available through the gateway.

## Route Configuration

```java
@Configuration
public class SpringCloudGatewayRouting {

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/currency-exchange/**")
                        .uri("lb://CURRENCY-EXCHANGE"))
                .route(p -> p
                        .path("/currency-conversion/**")
                        .uri("lb://CURRENCY-CONVERSION"))
                .route(p -> p
                        .path("/currency-conversion-feign/**")
                        .uri("lb://CURRENCY-CONVERSION"))
                .build();
    }
}
```

## What You Will Learn

- How to define selected routes in Java.
- How `RouteLocatorBuilder` maps paths to services.
- Why manual routes give better API control.


## Explanation

- `@Configuration` marks the class as a Spring configuration class.
- `@Bean` creates a Spring bean.
- `RouteLocator` defines the gateway routing rules.
- `.path("/currency-exchange/**")` matches incoming requests.
- `lb://CURRENCY-EXCHANGE` tells Gateway to use load balancing and Eureka service discovery.

## How It Works

1. Client calls Gateway:

```text
http://localhost:8765/currency-exchange/from/USD/to/INR
```

2. Gateway matches the path `/currency-exchange/**`.
3. Gateway asks Eureka for an available `CURRENCY-EXCHANGE` instance.
4. Gateway forwards the request to the selected instance.

Final backend call:

```text
http://localhost:8000/currency-exchange/from/USD/to/INR
```

## Why Manual Routes Are Useful

- We can expose only selected APIs.
- We get better control over route names.
- We can apply filters to specific routes.
- We avoid exposing every Eureka service automatically.

## URL Examples

Uppercase discovery-locator style:

```text
http://localhost:8765/CURRENCY-EXCHANGE/currency-exchange/from/USD/to/INR
http://localhost:8765/CURRENCY-CONVERSION/currency-conversion-feign/from/USD/to/INR/quantity/10
http://localhost:8765/CURRENCY-CONVERSION/currency-conversion/from/USD/to/INR/quantity/10
```

Lowercase discovery-locator style:

```text
http://localhost:8765/currency-exchange/currency-exchange/from/USD/to/INR
http://localhost:8765/currency-conversion/currency-conversion-feign/from/USD/to/INR/quantity/10
http://localhost:8765/currency-conversion/currency-conversion/from/USD/to/INR/quantity/10
```

Manual route style:

```text
http://localhost:8765/currency-exchange/from/USD/to/INR
http://localhost:8765/currency-conversion-feign/from/USD/to/INR/quantity/10
http://localhost:8765/currency-conversion/from/USD/to/INR/quantity/10
```

## How To Run

- Start Eureka, Currency Exchange, and Currency Conversion first.
- From this project, run `./mvnw.cmd spring-boot:run` on Windows or `mvn spring-boot:run`.
- Open `http://localhost:8765/currency-exchange/from/USD/to/INR`.

## Key Points Or Common Mistakes

- Manual routes and discovery locator can both affect routing; know which URL style you are testing.
- Use `lb://SERVICE-NAME` when routing through Eureka.
- Path predicates must match the incoming gateway path.


## Interview Points

- Manual routes give more control than discovery locator.
- `lb://` means load-balanced service lookup.
- Gateway integrates with Eureka to find the actual service instance.

## Chapter Summary

In this chapter, we manually controlled gateway routes.
The next chapter solves the problem of modifying requests before they reach backend services.

**Next step:** Continue with [G-spring-cloud-gateway-with-Routes-Filter](../G-spring-cloud-gateway-with-Routes-Filter/README.md), which solves the next remaining problem in the learning path.


## Interview Questions And Answers

**Q1. What is `RouteLocator`?**  
`RouteLocator` stores the routing rules used by Spring Cloud Gateway.

**Q2. Why define routes manually?**  
Manual routes give more security and control over exposed APIs.

**Q3. What is the advantage of `lb://SERVICE-NAME`?**  
It allows the gateway to discover and load balance service instances through Eureka.


**Q4. What is a path predicate?**  
It is a route condition that checks the incoming request path.

**Q5. Why use manual routes instead of automatic routes?**  
Manual routes expose only the APIs we choose.

**Q6. Can filters be added to manual routes?**  
Yes. Route filters are added in the route definition.