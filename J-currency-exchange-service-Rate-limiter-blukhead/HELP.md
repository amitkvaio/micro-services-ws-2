 
### 3. **RateLimiter**

* Controls the number of calls per second to avoid overloading a service.

---

## 🚦 What is RateLimiter in Resilience4j?

* **RateLimiter** is used to **limit the number of calls** to a service within a given time.
* It helps **protect a service from overload** when too many requests come at once.
* Works like a **traffic signal** → only a certain number of requests are allowed, others must wait or fail.

---

## ⚙️ How it Works

* You set:

  1. **limitForPeriod** → How many requests allowed per refresh period.
  2. **limitRefreshPeriod** → How often the rate limit resets.
  3. **timeoutDuration** → How long a request should wait for permission if limit is reached.

---

## Configuration (application.properties)

```properties
resilience4j.ratelimiter.instances.conratelimiter.limitForPeriod=5
# Allow max 5 calls per refresh period

resilience4j.ratelimiter.instances.conratelimiter.limitRefreshPeriod=10s
# Every 10 seconds, the counter resets

resilience4j.ratelimiter.instances.conratelimiter.timeoutDuration=2s
# If limit is exceeded, a call can wait for 2 seconds to get a permit
```

```
limitForPeriod=5 ==> Within 10 seconds, only 5 API calls are allowed.
limitRefreshPeriod=10s ==> If a 6th call comes, it will wait for up to 2 seconds.
timeoutDuration=2s ==> If no slot frees up in 2 seconds, the request is rejected with an error or fallback
```
---

## ✅ Example Usage in Controller

```java
 package com.amit.microservices.currencyexchangeservice.controller;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.amit.microservices.currencyexchangeservice.model.CurrencyConversion;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@RestController
public class CurrencyConversionRateLimiterController {
	
	private Logger logger = LoggerFactory.getLogger(CurrencyConversionRateLimiterController.class);
	
	@Autowired
	private Environment environment;
	 
	@GetMapping("/ratelimiter")
	public String getRateLimiterController() {
		logger.info("###### getRateLimiterController call called from Rate Limiter controller #######");
		return "Rate_Limiter_Controller";
	}
	
	@GetMapping("/currency-conversion-rate-limiter/from/{from}/to/{to}/quantity/{quantity}")
	@RateLimiter(name = "conratelimiter", fallbackMethod = "fallbackRateLimiterResponse")
	public CurrencyConversion calculateCurrencyConversionRateLimiter(
			@PathVariable String from, 
			@PathVariable String to,
			@PathVariable BigDecimal quantity) throws InterruptedException {
		
		logger.info("########## called from calculateCurrencyConversionRateLimiter funtion from CurrencyConversionRateLimiterController class ################");
		Thread.sleep(10000);
		logger.info("########## after sleep from calculateCurrencyConversionRateLimiter funtion from CurrencyConversionRateLimiterController class ################");
		
		String port = environment.getProperty("local.server.port") + "_Returning_Hard_Coded_Values_For_Rate_Limiter";
		
		CurrencyConversion conversion1 = new CurrencyConversion(
		        1001L,
		        "USD",
		        "INR",
		        BigDecimal.valueOf(10),
		        BigDecimal.valueOf(82), // conversion rate
		        BigDecimal.valueOf(820), // total = 10 * 82
		        port
		);
		 return conversion1;
	}
	
	// Fallback method (must have same parameters as original + Exception as last arg)
	public CurrencyConversion fallbackRateLimiterResponse(
	        String from,
	        String to,
	        BigDecimal quantity,
	        Throwable ex) {

	    logger.info("####### Fallback method has triggered of class CurrencyConversionRateLimiterController due to: " + ex.getMessage());

	    return new CurrencyConversion(
	            1010L,  // dummy id
	            from,
	            to,
	            quantity,
	            BigDecimal.valueOf(65),  // default conversion rate
	            BigDecimal.valueOf(65).multiply(quantity), // total amount
	            "Fallback response: ohh! Sorry!! Looks like there are some technical problems. Please try again later. Error: Retry :" + ex.getMessage()
	    );
	}
}

```

---

## Real-World Example

Imagine you have a **currency conversion API** that can only handle **5 requests every 10 seconds**.

* If **6th request** comes within 10 seconds → it will be **blocked** and fallback response is returned.
* This prevents **server overload** and ensures **fair usage**.

---

## **URL**
http://localhost:8000/ratelimiter  
http://localhost:8000/currency-conversion-rate-limiter/from/USD/to/INR/quantity/10  

---
##### **for /l %g in (1,1,100) do @(curl http://localhost:8000/ratelimiter & timeout /t 1)**

>(1,1,100) 
First 1 ==> starting number.    
Second 1 ==> is increment one by each iteration.  
Third 1 ==> 100--> is the ending number

>timeout /t 1 ==>Waits 1 second before sending the next request.

**Note :** This will wait for response then it will make a next request.

---

##### **for /l %g in (1,1,100) do start "" curl http://localhost:8000/currency-conversion-rate-limiter/from/USD/to/INR/quantity/10**

> This will not wait, it will make 100 parallel request.

## **Response/Logs**
```
2025-08-17T12:50:00.653+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2025-08-17T12:50:00.654+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2025-08-17T12:50:00.655+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 1 ms
2025-08-17T12:50:00.661+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-1] .CurrencyConversionRateLimiterController : ########## called from calculateCurrencyConversionRateLimiter funtion from CurrencyConversionRateLimiterController class ################
2025-08-17T12:50:01.406+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-3] .CurrencyConversionRateLimiterController : ########## called from calculateCurrencyConversionRateLimiter funtion from CurrencyConversionRateLimiterController class ################
2025-08-17T12:50:02.155+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-5] .CurrencyConversionRateLimiterController : ########## called from calculateCurrencyConversionRateLimiter funtion from CurrencyConversionRateLimiterController class ################
2025-08-17T12:50:02.894+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-7] .CurrencyConversionRateLimiterController : ########## called from calculateCurrencyConversionRateLimiter funtion from CurrencyConversionRateLimiterController class ################
2025-08-17T12:50:03.601+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-8] .CurrencyConversionRateLimiterController : ########## called from calculateCurrencyConversionRateLimiter funtion from CurrencyConversionRateLimiterController class ################
2025-08-17T12:50:04.347+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [io-8000-exec-10] .CurrencyConversionRateLimiterController : ########## called from calculateCurrencyConversionRateLimiter funtion from CurrencyConversionRateLimiterController class ################
2025-08-17T12:50:07.033+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-2] .CurrencyConversionRateLimiterController : ####### Fallback method has triggered of class CurrencyConversionRateLimiterController due to: RateLimiter 'conratelimiter' does not permit further calls
2025-08-17T12:50:07.681+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-3] .CurrencyConversionRateLimiterController : ####### Fallback method has triggered of class CurrencyConversionRateLimiterController due to: RateLimiter 'conratelimiter' does not permit further calls
2025-08-17T12:50:08.306+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-4] .CurrencyConversionRateLimiterController : ####### Fallback method has triggered of class CurrencyConversionRateLimiterController due to: RateLimiter 'conratelimiter' does not permit further calls
2025-08-17T12:50:08.898+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-5] .CurrencyConversionRateLimiterController : ####### Fallback method has triggered of class CurrencyConversionRateLimiterController due to: RateLimiter 'conratelimiter' does not permit further calls
```

>Within 10 second making more then 5 api call (Here making 10 api call).  
6th, 7th.. call it has wait for approx 2 seconds then it has called fallback method as shown from the above log.

### 4. **Bulkhead**

* Limits the number of concurrent calls to a service.
---

## What is @Bulkhead?

* **Bulkhead** is a design pattern (borrowed from ship compartments 🛳️).
* It **limits the number of concurrent calls** to a method/service.
* Helps **isolate failures** so one overloaded service doesn’t sink the whole system.
* In Resilience4j, Bulkhead works in **two modes**:

  1. **Semaphore Bulkhead** → Limits concurrent threads (fast, simple).
  2. **ThreadPool Bulkhead** → Runs calls in separate thread pool (for expensive tasks).
---

### Two types of Bulkhead

1. **Semaphore Bulkhead (default)**

   * Limits the number of concurrent calls.
   * Main property:

     ```properties
     resilience4j.bulkhead.instances.conconbulkhead.maxConcurrentCalls=5
	 # Maximum 5 parallel calls allowed
     
	 resilience4j.bulkhead.instances.conconbulkhead.maxWaitDuration=0
	 # If all slots busy, wait up to 2 seconds for a free slot
     ```

     * `maxConcurrentCalls` → maximum allowed parallel calls.
     * `maxWaitDuration` → how long a thread should wait to acquire a permit before failing. (`0` means *don’t wait, fail immediately*).

2. **ThreadPoolBulkhead** (when using thread pool isolation)

   * Uses a separate thread pool for handling calls.
   * Main properties:

     ```properties
     resilience4j.thread-pool-bulkhead.instances.conconbulkhead.maxThreadPoolSize=10
     resilience4j.thread-pool-bulkhead.instances.conconbulkhead.coreThreadPoolSize=5
     resilience4j.thread-pool-bulkhead.instances.conconbulkhead.queueCapacity=20
     ```
---

## Example Usage in Controller

```java
package com.amit.microservices.currencyexchangeservice.controller;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.amit.microservices.currencyexchangeservice.model.CurrencyConversion;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;

@RestController
public class CurrencyConversionBulkHeadController {
	
	private Logger logger = LoggerFactory.getLogger(CurrencyConversionBulkHeadController.class);
	
	@Autowired
	private Environment environment;
	
	@GetMapping("/bulkhead")
	public String getBulkHeadController() {
		logger.info("###### getBulkHeadController call called from Bulk Head controller #######");
		return "Bulk_Head_Controller";
	}
	
	@GetMapping("/currency-conversion-bulk-head/from/{from}/to/{to}/quantity/{quantity}")
	@Bulkhead(name = "conconbulkhead", type = Bulkhead.Type.SEMAPHORE, fallbackMethod = "fallbackBulkHeadResponse")
	public CurrencyConversion calculateCurrencyConversionBulkHead(
			@PathVariable String from, 
			@PathVariable String to,
			@PathVariable BigDecimal quantity) throws InterruptedException {
		
		logger.info("##########Befor sleep called from calculateCurrencyConversionBulkHead funtion from CurrencyConversionBulkHeadController class ################");
		Thread.sleep(5000);
		logger.info("##########After sleep called from calculateCurrencyConversionBulkHead funtion from CurrencyConversionBulkHeadController class ################");
		String port = environment.getProperty("local.server.port") + "_Returning_Hard_Coded_Values_For_BulkHead";
		
		CurrencyConversion conversion1 = new CurrencyConversion(
		        1001L,
		        "USD",
		        "INR",
		        BigDecimal.valueOf(10),
		        BigDecimal.valueOf(82), // conversion rate
		        BigDecimal.valueOf(820), // total = 10 * 82
		        port
		);
		 return conversion1;
	}
	
	// Fallback method (must have same parameters as original + Exception as last arg)
	public CurrencyConversion fallbackBulkHeadResponse(
	        String from,
	        String to,
	        BigDecimal quantity,
	        Throwable ex) {

	    logger.info("####### Fallback mehtod has triggered of class CurrencyConversionBulkHeadController due to: " + ex.getMessage());

	    return new CurrencyConversion(
	            1010L,  // dummy id
	            from,
	            to,
	            quantity,
	            BigDecimal.valueOf(65),  // default conversion rate
	            BigDecimal.valueOf(65).multiply(quantity), // total amount
	            "Fallback response: ohh! Sorry!! Looks like there are some technical problems. Please try again later. Error: Retry :" + ex.getMessage()
	    );
	}
}
```

---

## Real-Life Analogy

Think of a **restaurant with 5 tables** 🍽️

* Only **5 customers** can sit (concurrent calls).
* If a **6th customer arrives**, they must **wait** (maxWaitDuration) or leave (fallback).
* This prevents the kitchen (your service) from being overloaded.

---

## **URL**
http://localhost:8000/currency-conversion-bulk-head/from/USD/to/INR/quantity/10  
http://localhost:8000/bulkhead  

## **Windows CMD**
```
for /l %g in (1,1,100) do start "" curl http://localhost:8000/currency-conversion-bulk-head/from/USD/to/INR/quantity/10
```
## **Power cell**

```
1..10 | ForEach-Object { Start-Job { curl http://localhost:8000/currency-conversion-bulk-head/from/USD/to/INR/quantity/10 } }

PS C:\Users\ajitp> 1..10 | ForEach-Object { Start-Job { curl http://localhost:8000/currency-conversion-bulk-head/from/USD/to/INR/quantity/10 } }

Id     Name            PSJobTypeName   State         HasMoreData     Location             Command
--     ----            -------------   -----         -----------     --------             -------
241    Job241          BackgroundJob   Running       True            localhost             curl http://localhost...
243    Job243          BackgroundJob   Running       True            localhost             curl http://localhost...
245    Job245          BackgroundJob   Running       True            localhost             curl http://localhost...
247    Job247          BackgroundJob   Running       True            localhost             curl http://localhost...
249    Job249          BackgroundJob   Running       True            localhost             curl http://localhost...
251    Job251          BackgroundJob   Running       True            localhost             curl http://localhost...
253    Job253          BackgroundJob   Running       True            localhost             curl http://localhost...
255    Job255          BackgroundJob   Running       True            localhost             curl http://localhost...
257    Job257          BackgroundJob   Running       True            localhost             curl http://localhost...
259    Job259          BackgroundJob   Running       True            localhost             curl http://localhost...
```

## **Response**

```
2025-08-17T13:52:00.839+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-1] c.c.CurrencyConversionBulkHeadController : ##########Befor sleep called from calculateCurrencyConversionBulkHead funtion from CurrencyConversionBulkHeadController class ################
2025-08-17T13:52:00.915+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-2] c.c.CurrencyConversionBulkHeadController : ##########Befor sleep called from calculateCurrencyConversionBulkHead funtion from CurrencyConversionBulkHeadController class ################
2025-08-17T13:52:00.993+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-3] c.c.CurrencyConversionBulkHeadController : ##########Befor sleep called from calculateCurrencyConversionBulkHead funtion from CurrencyConversionBulkHeadController class ################
2025-08-17T13:52:01.073+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-4] c.c.CurrencyConversionBulkHeadController : ##########Befor sleep called from calculateCurrencyConversionBulkHead funtion from CurrencyConversionBulkHeadController class ################
2025-08-17T13:52:01.153+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-5] c.c.CurrencyConversionBulkHeadController : ##########Befor sleep called from calculateCurrencyConversionBulkHead funtion from CurrencyConversionBulkHeadController class ################
2025-08-17T13:52:03.235+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-6] c.c.CurrencyConversionBulkHeadController : ####### Fallback mehtod has triggered of class CurrencyConversionBulkHeadController due to: Bulkhead 'conconbulkhead' is full and does not permit further calls
2025-08-17T13:52:05.842+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-1] c.c.CurrencyConversionBulkHeadController : ##########After sleep called from calculateCurrencyConversionBulkHead funtion from CurrencyConversionBulkHeadController class ################
2025-08-17T13:52:05.920+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-2] c.c.CurrencyConversionBulkHeadController : ##########After sleep called from calculateCurrencyConversionBulkHead funtion from CurrencyConversionBulkHeadController class ################
2025-08-17T13:52:05.998+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-3] c.c.CurrencyConversionBulkHeadController : ##########After sleep called from calculateCurrencyConversionBulkHead funtion from CurrencyConversionBulkHeadController class ################
2025-08-17T13:52:06.075+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-4] c.c.CurrencyConversionBulkHeadController : ##########After sleep called from calculateCurrencyConversionBulkHead funtion from CurrencyConversionBulkHeadController class ################
2025-08-17T13:52:06.155+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-5] c.c.CurrencyConversionBulkHeadController : ##########After sleep called from calculateCurrencyConversionBulkHead funtion from CurrencyConversionBulkHeadController class ################
2025-08-17T13:52:06.337+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-4] c.c.CurrencyConversionBulkHeadController : ##########Befor sleep called from calculateCurrencyConversionBulkHead funtion from CurrencyConversionBulkHeadController class ################
2025-08-17T13:52:06.337+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-3] c.c.CurrencyConversionBulkHeadController : ##########Befor sleep called from calculateCurrencyConversionBulkHead funtion from CurrencyConversionBulkHeadController class ################
2025-08-17T13:52:06.337+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-1] c.c.CurrencyConversionBulkHeadController : ##########Befor sleep called from calculateCurrencyConversionBulkHead funtion from CurrencyConversionBulkHeadController class ################
2025-08-17T13:52:06.337+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-2] c.c.CurrencyConversionBulkHeadController : ##########Befor sleep called from calculateCurrencyConversionBulkHead funtion from CurrencyConversionBulkHeadController class ################
2025-08-17T13:52:11.342+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-4] c.c.CurrencyConversionBulkHeadController : ##########After sleep called from calculateCurrencyConversionBulkHead funtion from CurrencyConversionBulkHeadController class ################
2025-08-17T13:52:11.342+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-2] c.c.CurrencyConversionBulkHeadController : ##########After sleep called from calculateCurrencyConversionBulkHead funtion from CurrencyConversionBulkHeadController class ################
2025-08-17T13:52:11.342+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-3] c.c.CurrencyConversionBulkHeadController : ##########After sleep called from calculateCurrencyConversionBulkHead funtion from CurrencyConversionBulkHeadController class ################
2025-08-17T13:52:11.342+05:30  INFO 9168 --- [currency-exchange-service-Rate-limit-bulkhead] [nio-8000-exec-1] c.c.CurrencyConversionBulkHeadController : ##########After sleep called from calculateCurrencyConversionBulkHead funtion from CurrencyConversionBulkHeadController class ################

```

```

# BulkHead
resilience4j.bulkhead.instances.conconbulkhead.maxConcurrentCalls=5
# Maximum 5 parallel calls allowed

resilience4j.bulkhead.instances.conconbulkhead.maxWaitDuration=2s
# If all slots busy, wait up to 2 seconds for a free slot

We have made the 10 request at hte same time.  
First five api call has triggered successfully.

While 6th call there is no slots so fall back method has called.  
After execution of first 5 calls executed successfully  

The remaing 4 call has triggered and completed successfully.

```
## 5. **Time Limiting or Timeout Handling**
---

## 🔹 What is `@TimeLimiter`?

* `@TimeLimiter` is used to set a **maximum time limit** for a method call (like an API call, DB query, or remote service).
* If the method **does not finish within the given time**, it will be **canceled** and the **fallback method** is called (if defined).
* It prevents long-running calls from blocking threads forever.

⚠️ Note:

* `@TimeLimiter` works only with methods returning **`Future`** or **`CompletionStage` (e.g., `CompletableFuture`)**.
* It cannot directly work with synchronous methods.

---

### 2. Configuration (application.properties)

```properties
resilience4j.timelimiter.instances.myTimeLimiter.timeout-duration=2s
resilience4j.timelimiter.instances.myTimeLimiter.cancel-running-future=true
```

* `timeout-duration=2s` → if execution takes more than 2 seconds, cancel it.
* `cancel-running-future=true` → running task will be interrupted (if possible).

---

### 3. Service Example

```java
package com.amit.microservices.currencyexchangeservice.controller;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.amit.microservices.currencyexchangeservice.model.CurrencyConversion;

import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

@RestController
public class CurrencyConversionTimeLimiterController {
	
	private Logger logger = LoggerFactory.getLogger(CurrencyConversionTimeLimiterController.class);
	
	@Autowired
	private Environment environment;
	
	@GetMapping("/timelimiter")
	public String getTimeLimiterController() {
		logger.info("###### getTimeLimiterController call called from CurrencyConversionTimeLimiterController controller #######");
		return "Time_Limiter_Controller";
	}
	
	@GetMapping("/currency-conversion-time-limiter/from/{from}/to/{to}/quantity/{quantity}")
	@TimeLimiter(name = "concontimelimiter", fallbackMethod = "fallbackTimeLimiterResponse")
	public CompletableFuture<CurrencyConversion> calculateCurrencyConversionTimeLimiter(
	        @PathVariable String from,
	        @PathVariable String to,
	        @PathVariable BigDecimal quantity) {
	    
	    return CompletableFuture.supplyAsync(() -> {
	        try {
	            logger.info("########## Before sleep in calculateCurrencyConversionTimeLimiter method ##########");
	            // simulate delay
	            Thread.sleep(5000);
	            logger.info("########## After sleep in calculateCurrencyConversionTimeLimiter method ##########");
	            String port = environment.getProperty("local.server.port")
	                    + "_Returning_Hard_Coded_Values_For_Time_Limiter";

	            // create object
	            CurrencyConversion conversion1 = new CurrencyConversion(
	                    1001L,
	                    "USD",
	                    "INR",
	                    BigDecimal.valueOf(10),
	                    BigDecimal.valueOf(82),   // conversion rate
	                    BigDecimal.valueOf(820),  // total = 10 * 82
	                    port
	            );
	            return conversion1;
	        } catch (InterruptedException e) {
	            throw new RuntimeException(e);
	        }
	    });
	}

	
	// Fallback method (must have same parameters as original + Exception as last arg)
	public CompletableFuture<CurrencyConversion> fallbackTimeLimiterResponse(
	        String from,
	        String to,
	        BigDecimal quantity,
	        Throwable ex) {

	    logger.info("####### Fallback mehtod has triggered of class CurrencyConversionTimeLimiterController due to: " + ex.getMessage());

	    CurrencyConversion fallbackResponse = new CurrencyConversion(
	            1010L,  // dummy id
	            from,
	            to,
	            quantity,
	            BigDecimal.valueOf(65),  // default conversion rate
	            BigDecimal.valueOf(65).multiply(quantity), // total amount
	            "Fallback response: ohh! Sorry!! Looks like there are some technical problems. Please try again later. Error: Retry :" + ex.getMessage()
	    );
	    // wrap inside CompletableFuture
	    return CompletableFuture.completedFuture(fallbackResponse);
	}
}

```

---

## 🔹 Flow

1. Call `/timelimiter` → Service tries to sleep **5 sec**.
2. `@TimeLimiter` allows max **2 sec** → request will **timeout**.
3. `fallbackMethod()` is called → returns **"Request timed out, fallback called!"**.
---

## **URL**
```
http://localhost:8000/timelimiter  
http://localhost:8000/currency-conversion-time-limiter/from/USD/to/INR/quantity/10
```
## **Response**
```
2025-08-17T14:50:41.581+05:30  INFO 12948 --- [currency-exchange-service-Rate-limit-bulkhead] [onPool-worker-1] .CurrencyConversionTimeLimiterController : ########## Before sleep in calculateCurrencyConversionTimeLimiter method ##########
2025-08-17T14:50:43.583+05:30  INFO 12948 --- [currency-exchange-service-Rate-limit-bulkhead] [ool-10-thread-1] .CurrencyConversionTimeLimiterController : ####### Fallback mehtod has triggered of class CurrencyConversionTimeLimiterController due to: TimeLimiter 'concontimelimiter' recorded a timeout exception.
2025-08-17T14:50:46.594+05:30  INFO 12948 --- [currency-exchange-service-Rate-limit-bulkhead] [onPool-worker-1] .CurrencyConversionTimeLimiterController : ########## After sleep in calculateCurrencyConversionTimeLimiter method ##########

```
---

### 🔹 Why `@TimeLimiter` needs `Future` / `CompletionStage` / `CompletableFuture`

1. **Time-limiting means cancellation**

   * A `TimeLimiter` sets a max time a method is allowed to run.
   * If it takes longer, Resilience4j must **cancel the execution**.
   * But in Java, you can only *cancel* tasks that run asynchronously (like `Future.cancel(true)`), not normal synchronous methods.

2. **Synchronous methods can’t be interrupted safely**

   * If we method is a plain function returning an object (`CurrencyConversion`), and it hangs (say waiting on DB call), there’s **no safe way to stop it**.
   * Java doesn’t let libraries "kill" threads directly — only interrupt tasks that support interruption.

3. **Future/CompletableFuture/CompletionStage support interruption**

   * With async return types, Resilience4j wraps the call in a `ScheduledExecutorService`.
   * If the timeout expires, it can cancel the `Future`.
   * The caller immediately gets a `TimeoutException`, and your **fallback method** can be triggered.

---

### 🔹 Example

```java
@TimeLimiter(name = "currencyConversion", fallbackMethod = "fallbackTimeLimiterResponse")
public CompletableFuture<CurrencyConversion> convertCurrencyAsync(String from, String to, BigDecimal quantity) {
    return CompletableFuture.supplyAsync(() -> {
        // Simulate long running call
        try { Thread.sleep(5000); } catch (InterruptedException e) { }
        return new CurrencyConversion(...);
    });
}
```

* If timeout = 2s → after 2s, `TimeLimiter` cancels the future.
* The main thread doesn’t block forever, it just returns fallback.

---

✅ **Note:**
* `@TimeLimiter` only works with `Future`/`CompletionStage` because they give Resilience4j a way to cancel the task once the timeout is reached.   

* Synchronous return types (`String`, `CurrencyConversion`, etc.) cannot be canceled safely, so `TimeLimiter` cannot enforce its contract there.
---

# Using Multiple Resilience4j Aspects in a Single Method

* In microservices, we often need **fault tolerance**.
* Resilience4j allows applying **multiple patterns** (like Bulkhead, Circuit Breaker, Retry, etc.) on the same method.
* Each pattern is applied using its own **annotation** (e.g., `@Bulkhead`, `@TimeLimiter`).
* The **execution order** of these aspects is very important.
* By default, Resilience4j follows a **specific order**.
* We can also configure the **order manually** using properties.

---

## Default Execution Order (with reasons)

1. **Bulkhead**

   * First, to control the number of concurrent calls.
   * Example: Allow only 10 requests at the same time.

2. **Time Limiter**

   * Second, to make sure each request finishes within a set time.
   * Example: Fail if a request takes more than 2 seconds.

3. **Rate Limiter**

   * Third, to restrict the total number of calls in a given time window.
   * Example: Allow only 100 requests per minute.

4. **Circuit Breaker**

   * Fourth, to stop calling a failing service for some time.
   * Example: If 50% of last 10 requests failed, open the circuit.

5. **Retry**

   * Last, to automatically re-try failed requests (if allowed).
   * Example: Retry 3 times before failing.

---

## Configuration Example (application.properties)

```properties
resilience4j.bulkhead.bulkheadAspectOrder=1
resilience4j.timelimiter.timeLimiterAspectOrder=2
resilience4j.ratelimiter.rateLimiterAspectOrder=3
resilience4j.circuitbreaker.circuitBreakerAspectOrder=4
resilience4j.retry.retryAspectOrder=5
```

---

## **Note**:

* We can apply multiple aspects on one method.
* Order matters → Resilience4j ensures they run in the right sequence.
* The above order (1 to 5) is the **best practice and default**.
---
 