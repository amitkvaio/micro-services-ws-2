## 🌍 What is `GlobalFilter`?

* `GlobalFilter` is an **interface** in Spring Cloud Gateway.
* It allows us to apply **logic on every request** that passes through the API Gateway.
* It is like a **cross-cutting filter** which affects all routes, not just one.

👉 Think of it like a **security guard at the main gate**: everyone entering (request) or leaving (response) is checked.

---

## ⏰ When does it execute?

* A `GlobalFilter` executes **for each request** that comes to the API Gateway.
* It runs **before** the request is sent to the backend service (**pre-filter**) and/or
* It can also run **after** the response comes back from the backend (**post-filter**).

👉 Order of execution depends on the `getOrder()` method you define.

---

## ✅ What can I achieve using `GlobalFilter`?

You can implement many **cross-cutting concerns** using `GlobalFilter`, such as:

1. **Authentication & Authorization**

   * Check if request contains a valid JWT token or API key.
   * Example: Block request if token is missing or invalid.

2. **Logging & Monitoring**

   * Log request path, method, headers, or response status.
   * Example: Print `"Request: /users/id=10 at 10:30 AM"` in logs.

3. **Request/Response Modification**

   * Add, remove, or update request headers before forwarding.
   * Modify response headers before sending back to client.

4. **Rate Limiting / Throttling**

   * Restrict how many requests a user/IP can make in a certain time.
   * Example: Allow only 100 requests per minute per user.

5. **Metrics & Tracing**

   * Collect request execution time for performance analysis.
   * Example: Measure how long each backend service took to respond.

6. **Custom Business Logic**

   * Inject tenant-specific data.
   * Enforce compliance checks.

---

## 🔎 Example use case

```java
package com.springcloud.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter, Ordered  {

	private Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		logger.info("@LoggingFilter Global PRE Filter: Request Path Received -> {}", exchange.getRequest().getPath());
		// Continue request execution
		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			// Log response details (after processing)
			System.out.println("@LoggingFilter Global POST Filter: Response status -> " + exchange.getResponse().getStatusCode());
		}));
	}
	
	 // Set filter order (lower value = higher priority)
    @Override
    public int getOrder() {
    	logger.info("@LoggingFilter Global PRE Filter: getOrder method!");
        return -1;  // run before route filters
    }
}

```
---

## **Note:**

* `GlobalFilter` = common filter applied to **all routes**.
* Executes **before and/or after** request handling.
* Helps in Authentication, Logging, Monitoring, Rate Limiting, Request/Response changes, etc.

---

## **URL**

**Currency Exchange Service**
http://localhost:8000/currency-exchange/from/USD/to/INR

**Currency Conversion Service**
http://localhost:8100/currency-conversion/from/USD/to/INR/quantity/10  
http://localhost:8100/currency-conversion-feign/from/USD/to/INR/quantity/10

**Eureka**
http://localhost:8761/

**API GATEWAY**
http://localhost:8765  
http://localhost:8765/currency-exchange/from/USD/to/INR  
http://localhost:8765/currency-conversion/from/USD/to/INR/quantity/10  
http://localhost:8765/currency-conversion-feign/from/USD/to/INR/quantity/10  
http://localhost:8765/currency-conversion-new/from/USD/to/INR/quantity/10

## **Console logs**
```
Pre-filter Response
2025-08-16T15:06:41.397+05:30  INFO 19256 --- [spring-cloud-gateway-global-filter] [ctor-http-nio-3] c.s.gateway.filter.LoggingFilter         : Global PRE Filter: JwtAuthGlobalFilter started
2025-08-16T15:06:41.397+05:30  INFO 19256 --- [spring-cloud-gateway-global-filter] [ctor-http-nio-3] c.s.gateway.filter.LoggingFilter         : Global PRE Filter: JwtAuthGlobalFilter jwt token :eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbWl0Iiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNzU1MzM1NTgzLCJleHAiOjE3NTUzMzkxODN9.OF_5R7MJA47QeEtJubbwdICKwco9DXjBiGYJOeH5tG0
2025-08-16T15:06:41.449+05:30  INFO 19256 --- [spring-cloud-gateway-global-filter] [ctor-http-nio-3] c.s.gateway.filter.LoggingFilter         : Global PRE Filter: JwtAuthGlobalFilter jwt token has validated successfully!
2025-08-16T15:06:41.449+05:30  INFO 19256 --- [spring-cloud-gateway-global-filter] [ctor-http-nio-3] c.s.gateway.filter.LoggingFilter         : @LoggingFilter Global PRE Filter: Request Path Received -> /get

Post-Filter Response
@LoggingFilter Global POST Filter: Response status -> 200 OK
Global POST Filter: JwtAuthGlobalFilter Response status -> 200 OK
```