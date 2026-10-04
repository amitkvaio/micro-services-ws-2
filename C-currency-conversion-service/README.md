# Currency Conversion Service

This project registers the **Currency Conversion Service** with the Eureka Naming Server.
It calls the Currency Exchange Service to calculate the final converted amount.

## Agenda

- Register Currency Conversion Service with Eureka.
- Call Currency Exchange Service using service discovery.
- Calculate the final converted amount.
- Compare normal REST call flow with Feign-based communication.

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

## How To Run

1. Start `A-naming-server`.
2. Start `B-currency-exchange-service`.
3. Start `C-currency-conversion-service`.
4. Open Eureka dashboard and verify both services are registered.

Eureka dashboard:

```text
http://localhost:8761/
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

## Interview Points

- Currency Conversion Service is a Eureka Client.
- It uses Feign to call Currency Exchange Service.
- The exchange service URL is discovered from Eureka.
- Multiple exchange service instances can be used for load balancing.

## Chapter Summary

In this chapter, one microservice called another microservice through Eureka discovery.
The next chapter solves the problem of exposing microservices through a single API Gateway.

## Interview Questions And Answers

**Q1. What is Feign Client?**  
Feign is a declarative REST client. It helps call another REST service using an interface.

**Q2. Why use Eureka with Feign?**  
Feign can use Eureka to find the target service by name and avoid fixed URLs.

**Q3. What is service-to-service communication?**  
It means one microservice calls another microservice to complete a business operation.
