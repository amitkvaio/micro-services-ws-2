# API Gateway With Lowercase Service IDs

This project shows how to make Spring Cloud Gateway discovery routes use lowercase service names.

## Agenda

- Understand default uppercase Eureka service ids.
- Enable lowercase service ids in gateway routes.
- Make API Gateway URLs cleaner and easier to use.
- Prepare for manually controlled routes in the next chapter.

## Problem Solved In This Chapter

This chapter solves the problem of gateway URLs looking less readable because of uppercase service names.
Lowercase URLs are simpler and follow common REST API style.

## Property

```properties
spring.cloud.gateway.discovery.locator.lower-case-service-id=true
```

## Default Behavior

By default, Eureka service ids are usually uppercase.

Example service name:

```text
CURRENCY-EXCHANGE
```

Default gateway URL:

```text
http://localhost:8765/CURRENCY-EXCHANGE/**
```

## Behavior When Enabled

When `lower-case-service-id=true`, Gateway converts service ids to lowercase in the route URL.

Example:

```text
http://localhost:8765/currency-exchange/**
```

This looks cleaner and is easier to type.

## Summary

| Setting | URL Example | Use Case |
| --- | --- | --- |
| `false` | `/CURRENCY-EXCHANGE/**` | Match Eureka service id exactly. |
| `true` | `/currency-exchange/**` | Use cleaner REST-style URLs. |

## Best Practice

Usually, keep this property as `true`.

- URLs look clean.
- URLs are easier to remember.
- Lowercase paths follow common REST API style.

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
http://localhost:8765/currency-exchange/currency-exchange/from/USD/to/INR
http://localhost:8765/currency-conversion/currency-conversion-feign/from/USD/to/INR/quantity/10
http://localhost:8765/currency-conversion/currency-conversion/from/USD/to/INR/quantity/10
```

## Interview Points

- Eureka stores service names in uppercase by default.
- Gateway can expose cleaner lowercase routes.
- Lowercase URLs are preferred for public APIs.

## Chapter Summary

In this chapter, gateway URLs became cleaner with lowercase service ids.
The next chapter solves the problem of controlling gateway routes manually instead of exposing auto-created routes.

## Interview Questions And Answers

**Q1. Why are Eureka service names often uppercase?**  
Eureka commonly stores registered service ids in uppercase.

**Q2. Why use lowercase URLs?**  
Lowercase URLs are easier to read, type, and maintain.

**Q3. When should we avoid automatic discovery routes?**  
When we want full control and do not want every service exposed through the gateway.
