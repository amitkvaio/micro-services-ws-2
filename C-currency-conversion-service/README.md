# Currency Conversion Service

This project registers the **Currency Conversion Service** with the Eureka Naming Server.
It calls the Currency Exchange Service to calculate the final converted amount.

## Agenda

- [Problem we will solve](#problem-solved-in-this-chapter)
- [What you will learn](#what-you-will-learn)
- [Main explanation and examples](#dependency)
- [How to run](#how-to-run)
- [Key points or common mistakes](#key-points-or-common-mistakes)
- [Chapter Summary and Next Step](#chapter-summary)
- [Common interview questions](#interview-questions-and-answers)


## Problem Solved In This Chapter

This chapter solves the problem of **service-to-service communication**.
Currency Conversion Service can call Currency Exchange Service without hardcoding the exchange service location.

## Dependency

Add this dependency in `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

## What You Will Learn

- How Currency Conversion calls Currency Exchange.
- How Feign removes manual REST client code.
- How Eureka helps Feign find service instances.


## Enable Discovery

Add `@EnableDiscoveryClient` to the main application class if required by the Spring Cloud version.

```java
@EnableDiscoveryClient
@SpringBootApplication
public class CurrencyConversionServicesApplicationUsingFeign {
    public static void main(String[] args) {
        SpringApplication.run(CurrencyConversionServicesApplicationUsingFeign.class, args);
    }
}
```

## Eureka Configuration

`application.properties`

```properties
eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka
```

## URLs

Currency Exchange Service:

```text
http://localhost:8000/currency-exchange/from/USD/to/INR
```

Currency Conversion Service:

```text
http://localhost:8100/currency-conversion-feign/from/USD/to/INR/quantity/10
http://localhost:8100/currency-conversion/from/USD/to/INR/quantity/10
```

## Sample Response

```json
{
  "id": 10001,
  "from": "USD",
  "to": "INR",
  "quantity": 10,
  "conversionMultiple": 70.00,
  "totalCalucatedAmout": 700.00,
  "environment": "8000 feign"
}
```

## How To Run

1. Start `A-naming-server`.
2. Start `B-currency-exchange-service`.
3. Start `C-currency-conversion-service`.
4. Open Eureka dashboard and verify both services are registered.

Eureka dashboard:

```text
http://localhost:8761/
```

## Key Points Or Common Mistakes

- Do not start this service before the exchange service if you want a successful Feign call.
- Use the service name `currency-exchange`, not a hardcoded URL, for Eureka-based Feign.
- Port `8100` is used by this conversion service.


## Interview Points

- Currency Conversion Service is a Eureka Client.
- It uses Feign to call Currency Exchange Service.
- The exchange service URL is discovered from Eureka.
- Multiple exchange service instances can be used for load balancing.

## Chapter Summary

In this chapter, one microservice called another microservice through Eureka discovery.
The next chapter solves the problem of exposing microservices through a single API Gateway.

**Next step:** Continue with [D-spring-api-gateway-enabling-discovery-locator](../D-spring-api-gateway-enabling-discovery-locator/README.md), which solves the next remaining problem in the learning path.


## Interview Questions And Answers

**Q1. What is Feign Client?**  
Feign is a declarative REST client. It helps call another REST service using an interface.

**Q2. Why use Eureka with Feign?**  
Feign can use Eureka to find the target service by name and avoid fixed URLs.

**Q3. What is service-to-service communication?**  
It means one microservice calls another microservice to complete a business operation.

**Q4. Which annotation enables Feign clients?**  
`@EnableFeignClients` enables Feign client scanning.

**Q5. What is the main benefit of Feign?**  
It lets us call REST APIs using a Java interface.

**Q6. What happens if Currency Exchange Service is down?**  
The conversion call fails unless resilience logic is added in later chapters.