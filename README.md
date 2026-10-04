# Microservices Workspace 2

This repository is a step-by-step Spring Boot microservices learning workspace.
Each folder works like one chapter and solves one specific problem.

## Agenda

| Chapter | Project | Problem Solved |
| --- | --- | --- |
| A | `A-naming-server` | How services discover each other using Eureka. |
| B | `B-currency-exchange-service` | How a service registers with Eureka and serves exchange data. |
| C | `C-currency-conversion-service` | How one service calls another service using discovery. |
| D | `D-spring-api-gateway-enabling-discovery-locator` | How Gateway creates routes automatically from Eureka. |
| E | `E-spring-api-gateway-enabling-discovery-locator-lower-case` | How to expose cleaner lowercase gateway URLs. |
| F | `F-spring-api-gateway-With-Routes` | How to define gateway routes manually. |
| G | `G-spring-cloud-gateway-with-Routes-Filter` | How route filters modify requests. |
| H | `H-spring-cloud-gateway-Filter` | How global filters apply common logic to all routes. |
| I | `I-currency-exchange-service-Resilience4j` | How retry and circuit breaker improve fault tolerance. |
| J | `J-currency-exchange-service-Rate-limiter-blukhead` | How rate limiter, bulkhead, and timeout protect services. |
| K | `K-distributed-tracing-for-services-a` | How distributed tracing starts from Service A. |
| L | `L-distributed-tracing-for-services-b` | How tracing continues through Service B. |
| M | `M-distributed-tracing-for-services-c` | How tracing continues through Service C. |
| N | `N-distributed-tracing-for-services-d` | How tracing ends at Service D and appears in Zipkin. |

## Recommended Learning Order

Start from chapter A and move one folder at a time.
Each chapter solves the next practical problem that appears while building microservices.

## Problem Solved In This Repository

This repository solves the end-to-end learning problem of building a Spring Boot microservices system step by step.
It starts with service discovery, then adds service communication, API Gateway, fault tolerance, and distributed tracing.

## Interview Focus

This workspace covers commonly asked topics:

- Service discovery with Eureka.
- Feign client communication.
- Spring Cloud Gateway routing and filters.
- Resilience4j retry, circuit breaker, rate limiter, bulkhead, and time limiter.
- Distributed tracing using Micrometer Tracing and Zipkin.

## Learning Summary

Chapters A to C solve service discovery and service-to-service communication.
Chapters D to H solve API Gateway routing and filtering.
Chapters I to J solve fault tolerance and service protection.
Chapters K to N solve distributed tracing across multiple services.

## Interview Questions And Answers

**Q1. What is the main purpose of this workspace?**  
It explains microservices concepts using small Spring Boot projects in a practical order.

**Q2. What are the main parts of a microservices system shown here?**  
Service discovery, service communication, API Gateway, resilience, and distributed tracing.

**Q3. Why should we learn these chapters in order?**  
Each chapter solves the next problem that appears while building a real microservices system.
