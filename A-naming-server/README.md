# Eureka Naming Server

This project is the **Eureka Service Registry** for the microservices examples.
It runs on port `8761` and helps services find each other without hardcoded URLs.

## Problem Before Eureka

Earlier, a Feign client used a fixed URL:

```java
@FeignClient(name = "currency-exchange", url = "localhost:8000")
```

This works for one instance, but it is not practical in microservices.

- If we run more instances on ports `8000`, `8001`, or `8002`, we must change configuration.
- If one service goes down, the client may still call the failed service.
- If a new service instance starts, other services do not automatically know about it.

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

## Interview Points

- Eureka is used for **service discovery**.
- Eureka Server is the registry.
- Eureka Client is any microservice that registers with Eureka.
- In real projects, we usually run more than one service instance for high availability.
