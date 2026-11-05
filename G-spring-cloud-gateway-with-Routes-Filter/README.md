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



# Additional Explaination
```java
package com.springcloud.gateway.routes.configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGateWayCOnfiguration {
	@Bean
	public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
		return builder.routes()
				
				.route(p -> p.path("/get")  // A
						
					// Pre and Post filter provided by spring cloud gateway
					.filters(f -> f.addRequestHeader("MyHeader", "MyURI")
							.addRequestParameter("Param", "MyValue")
							.addResponseHeader("MyHeaderResponse", "MyHeaderResponse")) // B line number 17,18
			
						
				.uri("http://httpbin.org:80"))  // C
						
				 
				.route(p -> p.path("/currency-exchange/**")
							.uri("lb://currency-exchange"))
				
				.route(p -> p.path("/currency-conversion/**")
							.uri("lb://currency-conversion"))
				
				.route(p -> p.path("/currency-conversion-feign/**")
							.uri("lb://currency-conversion"))
				
				.route(p-> p.path("/currency-conversion-new/**")
								.filters(f -> f.rewritePath("/currency-conversion-new/(?<segment>.*)",
															"/currency-conversion-feign/${segment}"))
								.uri("lb://currency-conversion"))
				//D From line number 25 to 36
				.build();//E
	}
}

```

`gatewayRouter` is where you list the gateway's rules. Each rule says "if the URL looks like this, optionally change the request, then send it there." This method defines 5 such rules.

## The building blocks

| `@Bean` | Spring calls this method once at startup and keeps the routes it returns. |  
| `RouteLocatorBuilder builder` | A helper Spring gives you for building routes. |  
| `builder.routes()` | Start a new list of routes. |  
| `.route(p -> p ...)` | Add one rule. `p` is that rule, which you fill in. |  
| `.path("...")` | **When** this rule applies: which URL paths it matches. |  
| `.filters(f -> f ...)` | **What to change** before sending (optional). `f` is the list of changes. |  
| `.uri("...")` | **Where** to send the request. |  
| `.build()` | Finish the list and hand it to Spring. |  

So every route follows the same pattern:

```text
.route( WHEN path matches  →  CHANGE something (optional)  →  SEND to uri )
```

## Route 1: `/get` with filters (lines 14–22)

```java
.route(p -> p.path("/get")
        .filters(f -> f.addRequestHeader("MyHeader", "MyURI")
                .addRequestParameter("Param", "MyValue")
                .addResponseHeader("MyHeaderResponse", "MyHeaderResponse"))
        .uri("http://httpbin.org:80"))
```

**Step by step for `http://localhost:8765/get`:**

1. **Path check:** the path is exactly `/get`, so this route is used.
2. **Filters** modify the request on the way in and the response on the way out:
   - `addRequestHeader("MyHeader", "MyURI")`: add header `MyHeader: MyURI` to the request.
   - `addRequestParameter("Param", "MyValue")`: add `?Param=MyValue` to the URL.
   - `addResponseHeader(...)`: when the answer comes back, add header `MyHeaderResponse: MyHeaderResponse` before you receive it.
3. **URI:** send it to `http://httpbin.org:80`. This is a real external website, not Eureka, which is why there's no `lb://`.

```text
You:      GET http://localhost:8765/get
Gateway:  GET http://httpbin.org/get?Param=MyValue   + header MyHeader: MyURI
httpbin:  replies with JSON that echoes back what it received
Gateway:  adds response header MyHeaderResponse → gives it to you
```

httpbin echoes the request back, so the response shows `"MyHeader": "MyURI"` and `"Param": "MyValue"`. That's how you can see the filters working. You need internet access for this route.

## Routes 2, 3, 4: plain forwarding (lines 25–32)

```java
.route(p -> p.path("/currency-exchange/**")         .uri("lb://currency-exchange"))
.route(p -> p.path("/currency-conversion/**")       .uri("lb://currency-conversion"))
.route(p -> p.path("/currency-conversion-feign/**") .uri("lb://currency-conversion"))
```

These are the same as in F. They have no filters, so the path is forwarded unchanged.

| You type | Sent to |
|---|---|
| `localhost:8765/currency-exchange/from/USD/to/INR` | B → `/currency-exchange/from/USD/to/INR` |
| `localhost:8765/currency-conversion/from/USD/to/INR/quantity/10` | C → `/currency-conversion/from/USD/to/INR/quantity/10` |
| `localhost:8765/currency-conversion-feign/from/USD/to/INR/quantity/10` | C → `/currency-conversion-feign/from/USD/to/INR/quantity/10` |

`lb://currency-exchange` means: ask Eureka where `currency-exchange` is running, then send the request there.

## Route 5: rename a URL with `rewritePath` (lines 34–37)

```java
.route(p -> p.path("/currency-conversion-new/**")
        .filters(f -> f.rewritePath("/currency-conversion-new/(?<segment>.*)",
                                    "/currency-conversion-feign/${segment}"))
        .uri("lb://currency-conversion"))
```

This gives the user a new public URL (`/currency-conversion-new/...`) while C keeps its old one (`/currency-conversion-feign/...`). C doesn't have to change at all.

**Step by step for `http://localhost:8765/currency-conversion-new/from/USD/to/INR/quantity/10`:**

1. **Path check:** it starts with `/currency-conversion-new/`, so this route is used.
2. **rewritePath** does a find-and-replace:
   ```text
   Find:     /currency-conversion-new/(?<segment>.*)
                                      └─ save everything after it as "segment"
                                         segment = from/USD/to/INR/quantity/10

   Replace:  /currency-conversion-feign/${segment}
          =  /currency-conversion-feign/from/USD/to/INR/quantity/10
   ```
3. **URI:** Eureka finds C on port 8100 and the gateway sends:
   ```text
   http://localhost:8100/currency-conversion-feign/from/USD/to/INR/quantity/10
   ```
4. C runs the Feign method as usual, and you get the normal response.

## `.build()` (line 39)

This finishes the list. All 5 rules are now active in the gateway.

## How the gateway uses this list for each request

```text
Request comes in
   ↓
Check the routes for one whose path matches
   ↓  (no match → 404 from the gateway)
Apply that route's filters (if any)
   ↓
Send to that route's uri (Eureka lookup if it starts with lb://)
   ↓
Response comes back → response filters run → returned to you
```

## The comment at the bottom (Case 1/2/3)

The comment describes an exercise: comment parts out to see each feature on its own.
- **Case 1:** Comment out all the routes and keep only `.build()`. Only the auto-generated discovery-locator routes remain.
- **Case 2:** Keep only the `/get` route, without filters, and call http://localhost:8765/get. You see httpbin's plain response.
- **Case 3:** Add the filters back and call `/get` again. The extra header and parameter now appear in the response.
