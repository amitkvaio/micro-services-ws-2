# Spring Cloud Gateway Routes And Filters

This project shows how to add route-specific filters in Spring Cloud Gateway.
Filters can modify the request before it reaches the backend service.

## Agenda

- [Problem we will solve](#problem-solved-in-this-chapter)
- [What you will learn](#what-you-will-learn)
- [Main explanation and examples](#simple-route-example)
- [How to run](#how-to-run)
- [Key points or common mistakes](#key-points-or-common-mistakes)
- [Chapter Summary and Next Step](#chapter-summary)
- [Common interview questions](#interview-questions-and-answers)


## Problem Solved In This Chapter

This chapter solves the problem of sending requests exactly as received.
Gateway filters let us enrich, clean, or rewrite requests before forwarding them.

## Simple Route Example

```java
@Bean
public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
    return builder.routes()
            .route(p -> p.path("/get")
                    .uri("http://httpbin.org:80"))
            .build();
}
```

When we call:

```text
http://localhost:8765/get
```

Gateway forwards the request to:

```text
http://httpbin.org:80/get
```

## What You Will Learn

- How route filters add headers and query parameters.
- How `rewritePath` hides internal backend paths.
- How to test route behavior with `httpbin.org`.


## What Is `httpbin.org`?

`httpbin.org` is a public test API.
It returns details about the HTTP request, such as headers, query parameters, method, and origin.

It is useful for testing gateway routing and filters.

## Add Header And Query Parameter

```java
@Bean
public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
    return builder.routes()
            .route(p -> p.path("/get")
                    .filters(f -> f
                            .addRequestHeader("MyHeader", "MyURI")
                            .addRequestParameter("Param", "MyValue"))
                    .uri("http://httpbin.org:80"))
            .build();
}
```

## What This Does

When we call:

```text
http://localhost:8765/get
```

Gateway adds:

```text
Header: MyHeader=MyURI
Query parameter: Param=MyValue
```

Then it forwards the request to:

```text
http://httpbin.org:80/get?Param=MyValue
```

## Rewrite Path

```java
.route(p -> p.path("/currency-conversion-new/**")
        .filters(f -> f.rewritePath(
                "/currency-conversion-new/(?<segment>.*)",
                "/currency-conversion-feign/${segment}"))
        .uri("lb://currency-conversion"))
```

## How Rewrite Path Works

Client-friendly URL:

```text
http://localhost:8765/currency-conversion-new/from/USD/to/INR/quantity/10
```

Gateway rewrites it to:

```text
/currency-conversion-feign/from/USD/to/INR/quantity/10
```

Then Gateway forwards it to the Currency Conversion Service.

## Why Use `rewritePath`

- Keep public URLs simple.
- Hide internal service paths.
- Support old client URLs without changing backend code.
- Handle API versioning at the gateway level.

## URLs

```text
http://localhost:8765/get
http://localhost:8765/currency-exchange/from/USD/to/INR
http://localhost:8765/currency-conversion-feign/from/USD/to/INR/quantity/10
http://localhost:8765/currency-conversion/from/USD/to/INR/quantity/10
http://localhost:8765/currency-conversion-new/from/USD/to/INR/quantity/10
```

## How To Run

- Start supporting services when testing currency routes.
- From this project, run `./mvnw.cmd spring-boot:run` on Windows or `mvn spring-boot:run`.
- Open `http://localhost:8765/get` to test the `httpbin.org` route.
- Open `http://localhost:8765/currency-conversion-new/from/USD/to/INR/quantity/10` to test path rewrite.

## Key Points Or Common Mistakes

- Route filters apply only to matching routes, not every request.
- Regex groups in `rewritePath` must match the incoming path.
- `httpbin.org` is only a test service, not part of this microservice system.


## Interview Points

- Gateway filters can modify requests and responses.
- Route filters apply only to selected routes.
- `rewritePath` is useful when public API paths and backend paths are different.
- `httpbin.org` is commonly used to test HTTP behavior.

## Chapter Summary

In this chapter, route filters modified specific gateway requests.
The next chapter solves the problem of applying common logic to every gateway request.

**Next step:** Continue with [H-spring-cloud-gateway-Filter](../H-spring-cloud-gateway-Filter/README.md), which solves the next remaining problem in the learning path.


## Interview Questions And Answers

**Q1. What is a Gateway filter?**  
A Gateway filter modifies the request or response during routing.

**Q2. What is `rewritePath` used for?**  
It changes the incoming path before forwarding the request to the backend service.

**Q3. What is the difference between a route filter and a global filter?**  
A route filter applies to selected routes. A global filter applies to all routes.

**Q4. What does `addRequestHeader` do?**  
It adds a header before forwarding the request.

**Q5. What does `addRequestParameter` do?**  
It adds a query parameter before forwarding the request.

**Q6. Why use `rewritePath`?**  
It lets clients use a clean URL while the backend receives the path it expects.