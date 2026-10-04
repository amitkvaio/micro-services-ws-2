# Currency Exchange Service

This project is a **Eureka Client**. It registers the Currency Exchange Service with the Eureka Naming Server.

It also uses an H2 in-memory database for sample currency exchange data.

## Agenda

- Register Currency Exchange Service with Eureka.
- Store sample exchange values in H2 database.
- Expose an API to return conversion multiple.
- Prepare this service to be consumed by Currency Conversion Service.

## Problem Solved In This Chapter

This chapter solves the problem of making a backend service **discoverable**.
Other services can now find Currency Exchange Service using its service name instead of a fixed URL.

## Eureka Client Dependency

Add this dependency in `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

## What It Does

- Makes this Spring Boot application a Eureka Client.
- Registers the service with Eureka.
- Allows other services to discover it using the service name.
- Removes the need to hardcode URLs like `http://localhost:8000`.

## Why We Need It

In microservices, each service may run on a different port or server.
Hardcoding service URLs is not scalable.

With Eureka:

- Each service registers itself.
- Other services call it by service name.
- Eureka can help with load balancing when multiple instances are running.

## Example

Services used in this workspace:

- Eureka Naming Server: `http://localhost:8761`
- Currency Exchange Service: registers with Eureka.
- Currency Conversion Service: calls Currency Exchange Service.

Without Eureka:

```text
http://localhost:8000/currency-exchange/from/USD/to/INR
```

With Eureka and Feign:

```java
@FeignClient(name = "currency-exchange")
public interface CurrencyExchangeProxy {
    @GetMapping("/currency-exchange/from/{from}/to/{to}")
    CurrencyConversion retrieveExchangeValue(
        @PathVariable String from,
        @PathVariable String to
    );
}
```

Here, `currency-exchange` is the service name registered in Eureka.

## Controller Example

```java
@RestController
public class CurrencyConversionController {

    @Autowired
    private CurrencyExchangeProxy proxy;

    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversionFeign(
            @PathVariable String from,
            @PathVariable String to,
            @PathVariable BigDecimal quantity) {

        CurrencyConversion currencyConversion = proxy.retrieveExchangeValue(from, to);

        return new CurrencyConversion(
                currencyConversion.getId(),
                from,
                to,
                quantity,
                currencyConversion.getConversionMultiple(),
                quantity.multiply(currencyConversion.getConversionMultiple()),
                currencyConversion.getEnvironment() + " feign"
        );
    }
}
```

## H2 Database

Spring Boot can use H2 as a lightweight in-memory database.
It is useful for learning, testing, and demos.

Example configuration:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=none
spring.jpa.defer-datasource-initialization=true
```

`spring.jpa.defer-datasource-initialization=true` makes sure `data.sql` runs after JPA creates the schema.

## Sample `data.sql`

```sql
CREATE TABLE CURRENCY_EXCHANGE(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    currency_from VARCHAR(10),
    currency_to VARCHAR(10),
    conversion_multiple DECIMAL(19,2)
);

INSERT INTO CURRENCY_EXCHANGE(currency_from, currency_to, conversion_multiple)
VALUES ('USD', 'INR', 82.50);
```

## URLs

Currency Exchange API:

```text
http://localhost:8000/currency-exchange/from/USD/to/INR
```

H2 Console:

```text
http://localhost:8000/h2-console
```

JDBC URL:

```text
jdbc:h2:mem:testdb
```

## Important Notes

- H2 data is stored in memory.
- Data is reset when the application restarts.
- `data.sql` reloads sample data on startup.

## Interview Points

- Feign is used to call another REST service.
- Eureka removes the need for hardcoded service URLs.
- H2 is useful for local development, but MySQL or another real database is preferred for production.

## Chapter Summary

In this chapter, Currency Exchange Service was registered with Eureka and exposed exchange data.
The next chapter solves the problem of calling this service from another microservice.

## Interview Questions And Answers

**Q1. What is a Eureka Client?**  
A Eureka Client is a microservice that registers itself with Eureka and can discover other services.

**Q2. Why use H2 in this project?**  
H2 is lightweight and good for local learning or testing without installing a full database.

**Q3. Why should we avoid hardcoded service URLs?**  
Hardcoded URLs break when service ports or server locations change.
