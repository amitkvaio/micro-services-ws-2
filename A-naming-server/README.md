# Eureka Naming Server

This project is the **Eureka Service Registry** for the microservices examples.
It runs on port `8761` and helps services find each other without hardcoded URLs.

## Agenda

- [Problem we will solve](#problem-solved-in-this-chapter)
- [What you will learn](#what-you-will-learn)
- [Main explanation and examples](#problem-before-eureka)
- [How to run](#how-to-run)
- [Key points or common mistakes](#key-points-or-common-mistakes)
- [Chapter Summary and Next Step](#chapter-summary)
- [Common interview questions](#interview-questions-and-answers)


## Problem Solved In This Chapter

This chapter solves the problem of **service location management**.
Instead of remembering host names and ports manually, services can use Eureka as a common registry.

## Problem Before Eureka

Earlier, a Feign client used a fixed URL:

```java
@FeignClient(name = "currency-exchange", url = "localhost:8000")
```

This works for one instance, but it is not practical in microservices.

- If we run more instances on ports `8000`, `8001`, or `8002`, we must change configuration.
- If one service goes down, the client may still call the failed service.
- If a new service instance starts, other services do not automatically know about it.

## What You Will Learn

- Why service discovery is needed.
- How Eureka Server works.
- Why the Eureka server does not register with itself.


## Solution

Eureka works as a **Service Registry**.

- Each microservice registers itself with Eureka.
- Other services ask Eureka for the service location.
- The client uses the service name instead of a hardcoded host and port.

Example:

```text
currency-conversion -> asks Eureka for currency-exchange -> Eureka returns an available instance
```

## Dependency

Add this dependency for a Eureka server:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

## Configuration

`application.properties`

```properties
server.port=8761
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

`register-with-eureka=false` and `fetch-registry=false` are used because this application is the server itself.
It does not need to register with another Eureka server.

## Service Discovery Flow

1. Start the Eureka server on port `8761`.
2. Microservices register with Eureka.
3. Services send heartbeat signals to show they are alive.
4. A service calls another service by name, for example `http://CURRENCY-EXCHANGE`.
5. Eureka returns an available instance.
6. If a service stops, Eureka removes it after some time.

## Advantages

- No hardcoded IP address or port.
- New service instances can register automatically.
- Failed instances are removed from the registry.
- Works well with Feign and load balancing.
- Helps build scalable microservices.

## How To Run

- Prerequisites: Java 17 and Maven.
- From `A-naming-server`, run `mvn spring-boot:run`.
- Open `http://localhost:8761/` to verify the Eureka dashboard.

## Key Points Or Common Mistakes

- Do not set Eureka Server as a normal client unless you are building a clustered Eureka setup.
- Port `8761` is the expected Eureka dashboard port in this workspace.
- Start this chapter before running Eureka client services.


## Interview Points

- Eureka is used for **service discovery**.
- Eureka Server is the registry.
- Eureka Client is any microservice that registers with Eureka.
- In real projects, we usually run more than one service instance for high availability.

## Chapter Summary

In this chapter, we created the Eureka Naming Server.
The next chapter solves the problem of registering a real microservice with Eureka.

**Next step:** Continue with [B-currency-exchange-service](../B-currency-exchange-service/README.md), which solves the next remaining problem in the learning path.


## Interview Questions And Answers

**Q1. What is Eureka Server?**  
Eureka Server is a service registry where microservices register themselves and discover other services.

**Q2. Why do we need service discovery?**  
Because service instances can run on different ports or machines. Service discovery removes hardcoded URLs.

**Q3. Why is `register-with-eureka=false` used in Eureka Server?**  
Because the server itself does not need to register as a client.


**Q4. What port does this Eureka Server use?**  
It uses port `8761`.

**Q5. What happens if Eureka is down?**  
New discovery lookups may fail, but clients can sometimes use cached registry data for a short time.

**Q6. Is Eureka a database?**  
No. It is a runtime service registry, not a persistent business database.