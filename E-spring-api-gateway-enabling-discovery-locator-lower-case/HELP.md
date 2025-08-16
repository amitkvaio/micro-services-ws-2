## **spring.cloud.gateway.discovery.locator.lowerCaseServiceId=true**
---

### **Default Behavior (false)**

* By default, service IDs are **UPPERCASE** because Eureka service IDs are stored in uppercase.
* That means our route URLs will contain uppercase names.
* Example:

  * Eureka service name: `CURRENCY-EXCHANGE`
  * Auto route:

    ```
    http://localhost:8765/CURRENCY-EXCHANGE/**
    ```

---

### **When set to true ✅**

* The service IDs in routes are converted to **lowercase**.
* This makes URLs **cleaner** and easier to use (since URLs are usually lowercase).
* Example:

  * Eureka service name: `CURRENCY-EXCHANGE`
  * Auto route:

    ```
    http://localhost:8765/currency-exchange/**
    ```

---

### **Summary Table**

| Setting           | URL Example             | When to Use                                                               |
| ----------------- | ----------------------- | ------------------------------------------------------------------------- |
| `false` (default) | `/CURRENCY-EXCHANGE/**` | If we want routes to match service IDs exactly as in Eureka (uppercase). |
| `true`            | `/currency-exchange/**` | If we prefer lowercase, cleaner, user-friendly URLs.                     |

---

#### **Best Practice:**
Usually we keep it **true** because:

* URLs look better in lowercase.
* Easier to type and avoid confusion.
* Consistent with REST API conventions (lowercase, hyphen-separated paths).

---

## **How to run the application.**
###### start A-naming-server application for eureka server.
###### start B-currency-exchange-service application==> run two/three instance of it
>  -Dserver.port=8001 ==> By changing the port.
###### Start C-currency-conversion-service application.
###### Last E-spring-api-gateway-enabling-discovery-locator-lower-case start this application.
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

http://localhost:8765/currency-exchange/currency-exchange/from/USD/to/INR

###### For Currency-conversion 
 
http://localhost:8765/currency-conversion/currency-conversion-feign/from/USD/to/INR/quantity/10
http://localhost:8765/currency-conversion/currency-conversion/from/USD/to/INR/quantity/10
