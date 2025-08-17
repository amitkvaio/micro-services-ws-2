 
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

## Properties of Bulkhead

Some common configs you can set in `application.properties`:

```properties

resilience4j.bulkhead.instances.conconbulkhead.maxConcurrentCalls=5
# Maximum 5 parallel calls allowed

resilience4j.bulkhead.instances.conconbulkhead.maxWaitDuration=2s
# If all slots busy, wait up to 2 seconds for a free slot
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
