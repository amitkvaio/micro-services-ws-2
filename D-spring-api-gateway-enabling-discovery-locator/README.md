# API Gateway With Discovery Locator

This project shows how to use **Spring Cloud Gateway** with Eureka service discovery.
The gateway becomes the single entry point for calling backend microservices.

## Why API Gateway Is Needed

In a microservices system, services usually run on different ports or servers.
Without a gateway, the client must know every service URL.

With API Gateway:

- The client calls one common gateway URL.
- Authentication and common checks can happen in one place.
- The gateway routes the request to the correct service.
- Internal service URLs are hidden from the client.

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

## How To Run

1. Start `A-naming-server`.
2. Start `B-currency-exchange-service`.
3. Start more exchange service instances by changing the port, for example:

```text
-Dserver.port=8001
```

4. Start `C-currency-conversion-service`.
5. Start this gateway project.

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

## Interview Points

- API Gateway is a single entry point for microservices.
- `lb://SERVICE-NAME` means route through load balancer using Eureka.
- Discovery locator can auto-create routes from Eureka services.
- In production, gateway should be highly available.
