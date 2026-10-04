# Microservices Workspace 2

This repository is a step-by-step Spring Boot microservices learning workspace.
Each folder works like one chapter and solves one specific problem.

## README Style Used

This workspace uses the **Balanced** README style:

- Clear and concise.
- Simple enough for students.
- Detailed enough for interview preparation.
- Focused on the problem solved in each chapter.
- No unnecessary long theory.

## Agenda

| Chapter | Project | Problem Solved |
| --- | --- | --- |
| A | [`A-naming-server`](A-naming-server/README.md) | How services discover each other using Eureka. |
| B | [`B-currency-exchange-service`](B-currency-exchange-service/README.md) | How a service registers with Eureka and serves exchange data. |
| C | [`C-currency-conversion-service`](C-currency-conversion-service/README.md) | How one service calls another service using discovery. |
| D | [`D-spring-api-gateway-enabling-discovery-locator`](D-spring-api-gateway-enabling-discovery-locator/README.md) | How Gateway creates routes automatically from Eureka. |
| E | [`E-spring-api-gateway-enabling-discovery-locator-lower-case`](E-spring-api-gateway-enabling-discovery-locator-lower-case/README.md) | How to expose cleaner lowercase gateway URLs. |
| F | [`F-spring-api-gateway-With-Routes`](F-spring-api-gateway-With-Routes/README.md) | How to define gateway routes manually. |
| G | [`G-spring-cloud-gateway-with-Routes-Filter`](G-spring-cloud-gateway-with-Routes-Filter/README.md) | How route filters modify requests. |
| H | [`H-spring-cloud-gateway-Filter`](H-spring-cloud-gateway-Filter/README.md) | How global filters apply common logic to all routes. |
| I | [`I-currency-exchange-service-Resilience4j`](I-currency-exchange-service-Resilience4j/README.md) | How retry and circuit breaker improve fault tolerance. |
| J | [`J-currency-exchange-service-Rate-limiter-blukhead`](J-currency-exchange-service-Rate-limiter-blukhead/README.md) | How rate limiter, bulkhead, and timeout protect services. |
| K | [`K-distributed-tracing-for-services-a`](K-distributed-tracing-for-services-a/README.md) | How distributed tracing starts from Service A. |
| L | [`L-distributed-tracing-for-services-b`](L-distributed-tracing-for-services-b/README.md) | How tracing continues through Service B. |
| M | [`M-distributed-tracing-for-services-c`](M-distributed-tracing-for-services-c/README.md) | How tracing continues through Service C. |
| N | [`N-distributed-tracing-for-services-d`](N-distributed-tracing-for-services-d/README.md) | How tracing ends at Service D and appears in Zipkin. |
| O | [`O-Centralized-loggin-with-ELK-open-search`](O-Centralized-loggin-with-ELK-open-search/README.md) | How to search logs from all services in one OpenSearch Dashboards UI. |
| P | [`P-event-driven-microservices-with-kafka-rabbitmq`](P-event-driven-microservices-with-kafka-rabbitmq/README.md) | How services communicate asynchronously using Kafka or RabbitMQ. |
| Q | [`Q-saga-pattern-example`](Q-saga-pattern-example/README.md) | How to handle distributed transactions safely using Saga. |
| R | [`R-dockerize-each-service`](R-dockerize-each-service/README.md) | How to package each microservice as a Docker container. |
| S | [`S-docker-compose-setup`](S-docker-compose-setup/README.md) | How to start Eureka, Gateway, DB, and services with one command. |
| T | [`T-kubernetes-basics`](T-kubernetes-basics/README.md) | How to deploy microservices locally using Minikube or kind. |

## Recommended Learning Order

Start from chapter A and move one folder at a time.
Each chapter solves the next practical problem that appears while building microservices.

## How To Use This Workspace

1. Read the root agenda first.
2. Open chapter `A` and understand the problem solved there.
3. Run the project when required.
4. Read the chapter summary and interview questions.
5. Move to the next chapter.

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
- Centralized logging using OpenSearch and OpenSearch Dashboards.
- Event-driven communication using Kafka or RabbitMQ.
- Saga pattern for distributed transactions.
- Docker, Docker Compose, and Kubernetes basics.

## Key Concepts

| Concept | Simple Meaning |
| --- | --- |
| **Eureka Server** | A registry where services register themselves. |
| **Eureka Client** | A microservice that registers with Eureka. |
| **Feign Client** | A simple way to call another REST service. |
| **API Gateway** | A single entry point for client requests. |
| **Gateway Filter** | Logic that changes or checks requests and responses. |
| **Resilience4j** | A library for fault tolerance in microservices. |
| **Circuit Breaker** | Stops calling a failing service for some time. |
| **Rate Limiter** | Controls how many requests are allowed. |
| **Bulkhead** | Limits parallel calls to protect resources. |
| **Trace ID** | One id used to track a request across services. |
| **Span ID** | One id used to track a single operation inside a trace. |
| **Centralized Logging** | Collecting logs from many services into one searchable place. |
| **OpenSearch** | Stores and indexes logs for fast search. |
| **OpenSearch Dashboards** | UI used to search and visualize logs. |
| **Event-Driven Architecture** | Services communicate by publishing and consuming events. |
| **Kafka** | Distributed event streaming platform. |
| **RabbitMQ** | Message broker commonly used for queues and routing. |
| **Saga Pattern** | Manages distributed transactions using local transactions and compensation. |
| **Docker** | Packages each service as a runnable container. |
| **Docker Compose** | Starts multiple containers together from one YAML file. |
| **Kubernetes** | Orchestrates containers with scaling, service discovery, and self-healing. |

## Learning Summary

Chapters A to C solve service discovery and service-to-service communication.
Chapters D to H solve API Gateway routing and filtering.
Chapters I to J solve fault tolerance and service protection.
Chapters K to N solve distributed tracing across multiple services.
Chapter O solves centralized log search across services using OpenSearch.
Chapter P solves asynchronous service communication using Kafka or RabbitMQ.
Chapter Q solves distributed transaction handling using Saga.
Chapters R to S solve container packaging and one-command local startup.
Chapter T solves local Kubernetes deployment practice.

## Interview Questions And Answers

**Q1. What is the main purpose of this workspace?**  
It explains microservices concepts using small Spring Boot projects in a practical order.

**Q2. What are the main parts of a microservices system shown here?**  
Service discovery, service communication, API Gateway, resilience, and distributed tracing.

**Q3. Why should we learn these chapters in order?**  
Each chapter solves the next problem that appears while building a real microservices system.

**Q4. Why is API Gateway important in microservices?**  
It gives clients one entry point and hides internal service details.

**Q5. Why is Resilience4j used?**  
It helps services handle failures, overload, and slow responses gracefully.

**Q6. Why is distributed tracing needed?**  
It helps track one request across many services and makes debugging easier.

**Q7. Why is centralized logging needed?**  
It lets us search logs from all services in one place instead of checking each service console.

**Q8. Why use event-driven communication?**  
It reduces tight coupling because services can publish events and continue without waiting for every downstream service.

**Q9. What problem does Saga solve?**  
Saga handles a business transaction that spans multiple services without one shared database transaction.

## Interview-Ready Short Answers

- **Eureka** solves service discovery.
- **Feign** simplifies REST communication between services.
- **API Gateway** centralizes routing and common checks.
- **Resilience4j** improves fault tolerance.
- **Zipkin** shows request flow and latency across services.
- **OpenSearch Dashboards** helps search logs from all services in one UI.
- **Kafka/RabbitMQ** enable asynchronous communication between services.
- **Saga** coordinates distributed business transactions safely.
- **Docker Compose** starts the local microservices stack with one command.
- **Kubernetes** runs and manages containers in a cluster.

## Reusable Balanced README Template

Use this structure for future project README files:

```markdown
# Project Name

Short description of what the project does.

## Agenda

- What this project explains.
- What problem it solves.
- What the reader will learn.

## Problem Solved

Explain the problem in simple words.

## Overview

Explain the solution in clear and concise language.

## How To Run

Add required commands, URLs, and setup steps.

## Key Concepts

- **Concept 1**: short meaning.
- **Concept 2**: short meaning.

## Chapter Summary

Summarize what was solved and what the next chapter solves.

## Interview Questions And Answers

**Q1. Question?**  
Short and simple answer.
```

## Reusable Prompt

```text
Polish this README in a balanced style.
Keep the original technical meaning.
Use simple student-friendly language.
Add an agenda, problem solved section, key concepts, chapter summary, and interview Q&A.
Keep it concise and readable.
Do not remove important commands, URLs, code snippets, or project-specific details.
```
