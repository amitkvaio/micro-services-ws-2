# **How to Implement API Gateway in Spring Boot Microservices**

## **Introduction**

* In **Microservices Architecture**, we can deploy multiple services on different servers or hosts in a private network.
* When a client sends a request, **authentication** should be done before processing.
* Without a Gateway: If there are **100 services**, the client must pass authentication **100 times** — slow and repetitive.
* **Solution**: Use an **API Gateway** to handle authentication once and forward the request to the correct service.
* This makes responses **faster** and interactions **simpler**.
* **Spring Cloud Gateway** is a service that acts as a **mediator** between the client and services.
* It also supports **security, monitoring, metrics, and resiliency**.
---



## **Why Do We Need an API Gateway?**

* In microservices, services are on different servers/ports.
* Without a Gateway:

  * Client must remember **host & port** of each service.
  * Higher **security risks**.
* With a Gateway:

  * Validates **authentication** once.
  * **Routes** requests to correct service.
  * Adds **monitoring, metrics, resiliency**.
---
## **Advantages of API Gateway**

1. **One-time authentication** for all services.

   * If authentication fails at the Gateway → request stops.
   * Improves **security**.
2. **No direct access** to microservices endpoints.
3. **Hides internal architecture** from clients.
4. **Centralized cross-cutting features**:

   * Authentication
   * Monitoring
   * Resiliency
5. **Easy client interaction** with a single entry point.

---

## **Disadvantages of API Gateway**

1. **Performance impact** — every request goes through Gateway.
2. **Single point of failure** — if the Gateway is down, requests fail.

   * Solution: Use multiple Gateways with a **Load Balancer**.
3. **Client-specific Gateways may be needed**:

   * Android
   * iOS
   * Web
   * This pattern is called **Back-end for Front-end (BFF)**.

---

## **Key Terms in API Gateway**

### **Route**

* The **path** or **URL** to forward incoming requests.
* Defined by:

  * ID
  * Destination URI
  * Predicates (conditions)
  * Filters
* A Route is used when all conditions are **true**.

### **Predicate**

* A **condition** (Java 8 `Predicate` function) that checks request details.
* Input type: `ServerWebExchange` (Spring Framework).
* Example: Check headers, query params, etc.

### **Filter**

* Modify requests or responses **before** or **after** sending to the service.
* Example:

  * Add/remove headers
  * Change response body

---

## **Routing in API Gateway**

### **Static Routing**

* Microservice has **one instance**.
* Gateway forwards request **directly** to it.

### **Dynamic Routing**

* Microservice has **multiple instances**.
* Gateway uses **Eureka** to find a less-loaded instance.
* Uses **Feign Client** for load balancing.

---

## **How to Include Spring Cloud Gateway in a Project**

* Add the dependency:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```

---

## **How to Disable Spring Cloud Gateway**

> spring.cloud.gateway.enabled=false
---

## **spring.cloud.gateway.discovery.locator.enabled = true/false**

### **When `true`**

* Gateway **automatically discovers services** from the service registry (Eureka, Consul, etc.).
* We **do not** need to manually define routes in `application.properties` or Java config.
* It creates a **route for every registered service** using the service name.
* The route pattern is usually:

  ```
  http://<gateway-host>/<SERVICE-NAME>/<path>
  ```
* Example:

  * Eureka has a service named `currency-exchange`.
  * Gateway auto-creates route:

    ```
    GET /CURRENCY-EXCHANGE/** → forwards to lb://CURRENCY-EXCHANGE
    ```
  * You can directly call:

    ```
    http://localhost:8765/CURRENCY-EXCHANGE/currency-exchange/from/USD/to/INR
    ```

---

### **When `false`** ❌

* Gateway **will not** auto-create routes from service discovery.
* You must **manually define** every route in:

  * `application.properties` using `spring.cloud.gateway.routes`
  * or in Java config using `RouteLocatorBuilder`.
* Example:

  ```properties
  spring.cloud.gateway.routes[0].id=currency-exchange
  spring.cloud.gateway.routes[0].uri=lb://CURRENCY-EXCHANGE
  spring.cloud.gateway.routes[0].predicates[0]=Path=/currency-exchange/**
  ```
---

### **Summary Table**

| Setting Value | Behavior                                               | Use Case                                                                      |
| ------------- | ------------------------------------------------------ | ----------------------------------------------------------------------------- |
| `true`        | Auto-discovers services and builds routes dynamically. | When you want minimal manual config and dynamic service routing.              |
| `false`       | No auto-discovery, routes must be manually added.      | When you want **full control** over routes and prevent exposing all services. |

---

**Note:**
Even if `true`, you can customize route naming and filtering using:

```properties
spring.cloud.gateway.discovery.locator.lower-case-service-id=true
```

so service names in URLs are lowercase.
## **How to run the application.**
###### start A-naming-server application for eureka server.
###### start B-currency-exchange-service application==> run two/three instance of it
>  -Dserver.port=8001 ==> By changing the port.
###### Start C-currency-conversion-service application.
###### Last D-spring-api-gateway-enabling-discovery-locator start this application.
> Then run the below to verify the output.

---
## **URL**
#### Currency Exchange Service

http://localhost:8000/currency-exchange/from/USD/to/INR

#### Currency Conversion Service

http://localhost:8100/currency-conversion/from/USD/to/INR/quantity/10  
http://localhost:8100/currency-conversion-feign/from/USD/to/INR/quantity/10

#### Eureka

http://localhost:8761/

#### API GATEWAY
###### For Currency Exchange

http://localhost:8765/CURRENCY-EXCHANGE/currency-exchange/from/USD/to/INR

###### For Currency-conversion 
 
http://localhost:8765/CURRENCY-CONVERSION/currency-conversion-feign/from/USD/to/INR/quantity/10  
http://localhost:8765/CURRENCY-CONVERSION/currency-conversion/from/from/USD/to/INR/quantity/10