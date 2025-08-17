
## Dependency

```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot2</artifactId>
</dependency>
```

This adds **Resilience4j** support in your Spring Boot app.
Resilience4j is a **fault-tolerance library**.  
 – it helps your app stay stable and reliable when calling other services or APIs.

---

## Use Case (Why & When to Use)

We use it in **microservices or distributed systems** where one service depends on another.

* If the dependent service is **slow**, **down**, or **returns errors**, instead of your whole system failing, Resilience4j helps:

  * Control failures
  * Retry automatically
  * Prevent overload
  * Provide fallbacks

**Scenario Example**:

* Your **Order Service** calls a **Payment Service**.
* If the payment service is down or too slow:

  * Without Resilience4j → Order Service also hangs or crashes.
  * With Resilience4j → Order Service can return a proper response (like “Payment service temporarily unavailable, try again later”) instead of failing badly.

---

## ⚙️ Widely Used Components & Annotations

### 1. **Retry**

* Automatically retries the call a few times before giving up.
* Useful when failures are **temporary** (like network glitch).

**Example**:

```java
package com.amit.microservices.currencyexchangeservice.controller;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.amit.microservices.currencyexchangeservice.model.CurrencyConversion;

import io.github.resilience4j.retry.annotation.Retry;

@RestController
public class Resilience4jRetryController {
	
	private Logger logger = LoggerFactory.getLogger(Resilience4jRetryController.class);
	
	@Autowired
	private com.amit.microservices.currencyexchangeservice.proxy.CurrencyExchangeProxy proxy;
	
	@GetMapping("/currency-conversion-feign-retry/from/{from}/to/{to}/quantity/{quantity}")
        @Retry(name = "currencyConversionService", fallbackMethod = "fallbackCurrencyExchangeResponse")
	public CurrencyConversion calculateCurrencyConversionFeign(
			@PathVariable String from, 
			@PathVariable String to,
			@PathVariable BigDecimal quantity) {
		
		logger.info("########## called from calculateCurrencyConversionFeign funtion from Resilience4jRetryController class ################");
		
		// This might fail (for example, service down or network issue)
       	CurrencyConversion currencyConversion = proxy.retrieveExchangeValue(from, to);

		return new CurrencyConversion(currencyConversion.getId(), from, to, quantity,
				currencyConversion.getConversionMultiple(),
				quantity.multiply(currencyConversion.getConversionMultiple()),
				currencyConversion.getEnvironment() + " " + "Using-Feign-Client");
	}
	
	// Fallback method (must have same parameters as original + Exception as last arg)
	public CurrencyConversion fallbackCurrencyExchangeResponse(
	        String from,
	        String to,
	        BigDecimal quantity,
	        Throwable ex) {

	    logger.info("####### Fallback triggered due to: " + ex.getMessage());

	    return new CurrencyConversion(
	            1010L,  // dummy id
	            from,
	            to,
	            quantity,
	            BigDecimal.valueOf(65),  // default conversion rate
	            BigDecimal.valueOf(65).multiply(quantity), // total amount
	            "Fallback response: ohh! Sorry!! Looks like there are some technical problems. Please try again later. Error: " + ex.getMessage()
	    );
	}
}

```

### **Rules to Remember while writing the fallback method**

1. **Method name** must match the one you gave in `fallbackMethod`.
2. **Parameters** must be identical to the original method + optionally `Throwable`.
3. **Return type** must be the same as the original method .
4. Place the fallback method in the **same class** (unless you configure otherwise).

```properties
resilience4j.retry.instances.currencyConversionService.maxAttempts=3
resilience4j.retry.instances.currencyConversionService.waitDuration=10s
resilience4j.retry.instances.currencyConversionService.enableExponentialBackoff=true
```

* `maxAttempts=3` → Retry the request up to **3 times** before failing.
* `waitDuration=10s` → Wait **10 seconds** between retry attempts.
* `enableExponentialBackoff=true` → Each retry will wait **longer than the previous one** (progressively increasing delay).


## **URL/ Steps to run**

```
First Run : A-naming-server application
Second Run : B-currency-exchange-service application
Third Run : I-currency-exchange-service-Resilience4jRetry

Check the respective services whether it is running or not.

Eureka
http://localhost:8761/

Currency Exchange Service
http://localhost:8000/currency-exchange/from/USD/to/INR

8-currency-exchange-service-Resilience4jRetry
http://localhost:8080/currency-conversion-feign-retry/from/USD/to/INR/quantity/10

It will give the response as below.
2025-08-17T00:28:41.905+05:30  INFO 13340 --- [currency-exchange-service-Resilience4jRetry] [io-8080-exec-10] c.a.m.c.c.Resilience4jRetryController    : ########## called from calculateCurrencyConversionFeign funtion from Resilience4jRetryController class ################

Stop this service : B-currency-exchange-service application and try the same url
http://localhost:8000/currency-conversion-feign-retry/from/USD/to/INR/quantity/10

Response log:
2025-08-17T00:30:45.064+05:30  INFO 13340 --- [currency-exchange-service-Resilience4jRetry] [nio-8080-exec-4] c.a.m.c.c.Resilience4jRetryController    : ########## called from calculateCurrencyConversionFeign funtion from Resilience4jRetryController class ################
2025-08-17T00:30:57.131+05:30  INFO 13340 --- [currency-exchange-service-Resilience4jRetry] [nio-8080-exec-4] c.a.m.c.c.Resilience4jRetryController    : ########## called from calculateCurrencyConversionFeign funtion from Resilience4jRetryController class ################
2025-08-17T00:31:14.159+05:30  INFO 13340 --- [currency-exchange-service-Resilience4jRetry] [nio-8080-exec-4] c.a.m.c.c.Resilience4jRetryController    : ########## called from calculateCurrencyConversionFeign funtion from Resilience4jRetryController class ################
2025-08-17T00:31:16.193+05:30  INFO 13340 --- [currency-exchange-service-Resilience4jRetry] [nio-8080-exec-4] c.a.m.c.c.Resilience4jRetryController    : ####### Fallback triggered due to: Connection refused: no further information executing GET http://currency-exchange/currency-exchange/from/USD/to/INR

```
### 2. **CircuitBreaker**

* Stops calling a failing service for some time after repeated failures.
* Helps avoid **cascading failures**.

**Example**:

```java
package com.amit.microservices.currencyexchangeservice.controller;
import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.amit.microservices.currencyexchangeservice.model.CurrencyConversion;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@RestController
public class Resilience4jCircuitBreakerController {
	
	private Logger logger = LoggerFactory.getLogger(Resilience4jRetryController.class);
	
	@Autowired
	private com.amit.microservices.currencyexchangeservice.proxy.CurrencyExchangeProxy proxy;
	
	@GetMapping("/currency-conversion-feign-circuit-breaker/from/{from}/to/{to}/quantity/{quantity}")
        @CircuitBreaker(name = "currencyConversionServiceWithCircuitBreaker", fallbackMethod = "fallbackCurrencyExchangeResponse")
	public CurrencyConversion calculateCurrencyConversionFeign_CircuitBreaker(
			@PathVariable String from, 
			@PathVariable String to,
			@PathVariable BigDecimal quantity) {
		
		logger.info("########## called from calculateCurrencyConversionFeign_CircuitBreaker funtion from Resilience4jCircuitBreakerController class ################");
		
		// This might fail (for example, service down or network issue)
       	CurrencyConversion currencyConversion = proxy.retrieveExchangeValue(from, to);

		return new CurrencyConversion(currencyConversion.getId(), from, to, quantity,
				currencyConversion.getConversionMultiple(),
				quantity.multiply(currencyConversion.getConversionMultiple()),
				currencyConversion.getEnvironment() + " " + "Using-Feign-Client");
	}
	
	// Fallback method (must have same parameters as original + Exception as last arg)
	public CurrencyConversion fallbackCurrencyExchangeResponse(
	        String from,
	        String to,
	        BigDecimal quantity,
	        Throwable ex) {

	    logger.info("####### Fallback method fallbackCurrencyExchangeResponse triggered due to: " + ex.getMessage());

	    return new CurrencyConversion(
	            1010L,  // dummy id
	            from,
	            to,
	            quantity,
	            BigDecimal.valueOf(65),  // default conversion rate
	            BigDecimal.valueOf(65).multiply(quantity), // total amount
	            "Fallback response: ohh! Sorry!! Looks like there are some technical problems. Please try again later. Error: " + ex.getMessage()
	    );
	}
}

```
---

## What is Circuit Breaker?

* A **Circuit Breaker** is like a safety switch in an electrical circuit.
* In software, it is used to **stop calling a failing service** again and again.
* If a service keeps failing (like a payment API is down), the circuit breaker “opens” and **blocks further calls** for a period of time.
* This helps to:

  * Save system resources (no useless retries).
  * Prevent cascading failures in microservices.
  * Quickly return a fallback response to the user.
---

## Main Purpose of Circuit Breaker

1. Protects your application from repeatedly calling a **failing service**.
2. Improves **resilience and stability**.
3. Allows services to **recover gracefully**.

---

## Configuration in `application.properties`

```properties
resilience4j.circuitbreaker.instances.currencyConversionServiceWithCircuitBreaker.sliding-window-size=5
resilience4j.circuitbreaker.instances.currencyConversionServiceWithCircuitBreaker.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.currencyConversionServiceWithCircuitBreaker.wait-duration-in-open-state=30s
resilience4j.circuitbreaker.instances.currencyConversionServiceWithCircuitBreaker.permitted-number-of-calls-in-half-open-state=2
```
---

### 🔹`sliding-window-size=5`

* The circuit breaker **monitors the last 5 calls** (success or failure).
* This is like keeping a small window of recent history.
* Failures are counted within this window.

>Example: If 3 out of the last 5 calls failed → failure rate = 60%.

---

### 🔹`failure-rate-threshold=50`

* If more than **50% of the calls in the sliding window fail**, the circuit breaker **opens**.
* Open means → block further calls and return fallback immediately.

  **Example:**


* Window size = 5
* Failures = 3 (60%) → greater than 50% → Circuit Breaker **opens**.

---

### 🔹`wait-duration-in-open-state=10s`

* After the breaker is **open**, it stays open (blocks all calls) for **10 seconds**.
* After 10s, it moves to **Half-Open** and allows a few test calls.

>Example: Payment service failed → breaker opened → for 10s, no call is made → after 10s, it allows test calls.

---

### 🔹`permitted-number-of-calls-in-half-open-state=2`

* In **Half-Open** state, the breaker allows **2 test calls**.
* If both calls succeed → breaker closes again (normal).
* If any fails → breaker goes back to Open.

>Example: After 10s wait → allow 2 calls:

* If success → back to Closed.
* If failure → back to Open.

---

#### **Full Flow with Your Config**

1. Circuit Breaker watches last **5 calls**.
2. If **3 out of 5 fail (≥50%)**, it **opens**.
3. In Open state → no calls for **10s** (fallback returned immediately).
4. After 10s → breaker enters **Half-Open** and allows **2 test calls**.
5. If those **2 calls succeed → Close** (normal state again).
6. If they fail → breaker goes **back to Open** for another 10s.

---

* **sliding-window-size=5** → Keep last 5 exam results.
* **failure-rate-threshold=50** → If more than half are failed → Stop giving exams.
* **wait-duration-in-open-state=10s** → Take a break for 10 seconds.
* **permitted-number-of-calls-in-half-open-state=2** → Try 2 sample exams to check recovery.

---

## 🔌 Circuit Breaker States
The **Circuit Breaker** in Resilience4j has **3 main states.**
### 1. **Closed** (Normal State)

* All calls are allowed.
* Circuit Breaker is “closed,” like electricity is flowing normally.
* Failures are counted.
* If **failure rate** crosses threshold (e.g., 50% of last 10 calls failed), it goes to **Open** state.

> Example: Order service is healthy, so we call it normally.

---

### 2. **Open** (Tripped State)

* Calls are **blocked immediately**.
* No request is sent to the failing service.
* Instead, fallback response is returned.
* After a set **wait duration** (e.g., 10 seconds), it moves to **Half-Open**.

> Example: Order service is down → breaker “opens” → stop calling for 10 seconds.

---

### 3. **Half-Open** (Testing State)

* Only a **limited number of calls** are allowed.
* If those calls succeed → breaker goes back to **Closed**.
* If those calls fail → breaker goes back to **Open**.

> Example: After 10 seconds, breaker allows 2 test calls.

* If success → service is back → return to Closed.
* If failure → back to Open again.

---

### 4. (Optional) **Disabled / Forced-Open**

* Sometimes you can manually disable the circuit breaker or force it to stay open.
* Rarely used in real cases.

---

## 🖼️ Circuit Breaker State Transition Diagram

Here’s a **text diagram** (ASCII) since I can’t draw in Word here:

```
         ┌─────────────┐
         │   CLOSED    │
         │ (All calls) │
         └─────┬───────┘
               │
     Failure rate > Threshold
               │
               ▼
         ┌─────────────┐
         │    OPEN     │
         │ (Block calls)│
         └─────┬───────┘
   After waitDuration
               │
               ▼
         ┌─────────────┐
         │  HALF-OPEN  │
         │ (Test calls)│
         └─────┬───────┘
     Success   │   Failure
       │       │
       ▼       ▼
   ┌────────┐  ┌────────┐
   │ CLOSED │  │  OPEN  │
   └────────┘  └────────┘
```

---

## ✅ Summary (Easy to Remember)

* **Closed** → All good (monitor failures).
* **Open** → Service failing → stop calls, fallback immediately.
* **Half-Open** → Test few calls to check recovery.

👉 Real life analogy:

* Closed = Normal traffic light is green.
* Open = Light turns red (stop all traffic).
* Half-Open = Yellow light (allow a few cars to test road condition).

---

## ✅ Why Industry Uses Resilience4j

* Prevents **system-wide outages** due to one bad service.
* Improves **user experience** by giving fallback responses.
* Makes services **robust and self-healing**.
* Widely used in **microservices with Spring Boot + Cloud** (e.g., Netflix-like architectures).
---
