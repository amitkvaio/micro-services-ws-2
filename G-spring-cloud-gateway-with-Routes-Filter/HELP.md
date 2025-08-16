```Java
@Bean
public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
   return builder.routes()
      .route(p -> p.path("/get")
      .uri("http://httpbin.org:80"))
      .build();
}
```
---

### 🔹 What it means

* `p.path("/get")` → Any incoming HTTP request with path `/get` will be matched.
* `.uri("http://httpbin.org:80")` → The request will be forwarded to **httpbin.org** (a public test service).

When we call your gateway (e.g., `http://localhost:8765/get`), it internally makes a request to **[http://httpbin.org:80/get](http://httpbin.org:80/get)**.

---

### 🔹 What is `httpbin.org`?

* `httpbin.org` is a **publicly available test API**.
* It is widely used for **testing HTTP requests and responses**.
* It returns details about the request we made (headers, method, IP, etc.).

Example:
If we send a request:

```
http://httpbin.org/get?name=amit
```

Response might look like:

```json
{
  "args": {
    "name": "amit"
  },
  "headers": {
    "Accept": "*/*",
    "Host": "httpbin.org"
  },
  "origin": "122.160.x.x",
  "url": "http://httpbin.org/get?name=amit"
}
```

---

### 🔹 Use Case in our Code

* This URI (`http://httpbin.org:80`) is used only for **demo or testing**.
* It helps verify that **routing is working** correctly in your API Gateway.

---

## **Note**
The `http://httpbin.org:80` URI is not a real microservice of yours. It’s a **public test server**.
It is used in Spring Cloud Gateway examples to test that routes are working properly before connecting to real microservices.

---
```java
@Bean
public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
return builder.routes()
				
		.route(p -> p.path("/get")				
		.filters(f -> f.addRequestHeader("MyHeader", "MyURI")
			       .addRequestParameter("Param", "MyValue"))	
		.uri("http://httpbin.org:80"))
		.build();
	}

```
---
Perfect 👍 now you are looking at **filters in Spring Cloud Gateway**.

Let’s break it down step by step.

---

### 🔹 Your code

```java
@Bean
public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
   return builder.routes()
      .route(p -> p.path("/get")
         .filters(f -> f.addRequestHeader("MyHeader", "MyURI")
                        .addRequestParameter("Param", "MyValue"))
         .uri("http://httpbin.org:80"))
      .build();
}
```

---

### 🔹 What this does

* `.filters(...)` → means before forwarding the request, the Gateway will **modify or enrich the request**.
* `addRequestHeader("MyHeader", "MyURI")` → Adds a new HTTP header.
* `addRequestParameter("Param", "MyValue")` → Adds a query parameter to the request.

So when we hit **our gateway**, it won’t forward the request as-is — it will **add extra info** to the request before sending it to the target URI (`httpbin.org` here).

---

### 🔹 Practical Example

Suppose our Gateway is running on:

```
http://localhost:8765
```

We make a request:

```
http://localhost:8765/get
```

---

1. It matches path `/get`.
2. It adds:

   * Header → `MyHeader: MyURI`
   * Query Parameter → `Param=MyValue`
3. Then forwards to:

```
http://httpbin.org:80/get?Param=MyValue
```

with header:

```
MyHeader: MyURI
```

---

#### What you will see in the response (from `httpbin.org`):

```json
{
  "args": {
    "Param": "MyValue"
  },
  "headers": {
    "Accept": "*/*",
    "Host": "httpbin.org",
    "MyHeader": "MyURI"
  },
  "origin": "122.160.x.x",
  "url": "http://httpbin.org/get?Param=MyValue"
}
```

---

### Real-World Use Cases of Filters

  **Authentication**

   * Add an `Authorization` header with JWT or OAuth2 token.
   * Example:

     ```java
     f.addRequestHeader("Authorization", "Bearer <token>")
     ```
---
# **Rewrite Path**
---
```java
.route(p -> p.path("/currency-conversion-new/**")
    .filters(f -> f.rewritePath("/currency-conversion-new/(?<segment>.*)",
                                "/currency-conversion-feign/${segment}"))
    .uri("lb://currency-conversion"))
```
---

### 🔹 What it does ?

1. **Incoming path** → The route will match any request starting with
   `/currency-conversion-new/**`

   Example:

   ```
   http://localhost:8765/currency-conversion-new/from/USD/to/INR
   ```

2. **rewritePath filter**

   * Original path: `/currency-conversion-new/(?<segment>.*)`
     → This uses a **regex pattern**.
     → `(?<segment>.*)` means "capture everything after `/currency-conversion-new/` and call it `segment`".

   * Rewritten path: `/currency-conversion-feign/${segment}`
     → `${segment}` inserts whatever was captured.

   So `/currency-conversion-new/from/USD/to/INR` becomes:
   `/currency-conversion-feign/from/USD/to/INR`

3. **Forward to service**

   * `.uri("lb://currency-conversion")`
     → Gateway will send the rewritten request to the microservice **currency-conversion** (using Eureka load balancing).

---

### 🔹 Practical Example

**Requested url**

```
http://localhost:8765/currency-conversion-new/from/USD/to/INR
```

**Inside Gateway:**

* It matches path `/currency-conversion-new/**`
* Applies rewrite:

  ```
  /currency-conversion-new/from/USD/to/INR
  → /currency-conversion-feign/from/USD/to/INR
  ```
* Forwards to:

  ```
  lb://currency-conversion/currency-conversion-feign/from/USD/to/INR
  ```

👉 The **currency-conversion service** actually receives the request as:

```
http://currency-conversion/currency-conversion-feign/from/USD/to/INR
```

---

### 🔹 Why use `rewritePath`?

* Sometimes clients call our API with a path that is **different from the backend service path**.
* We does not want to force clients to know internal microservice paths.
* **rewritePath makes the gateway translate client-friendly URLs into backend URLs.**

---

### 🔹 Real-World Use Cases

1. **Backward compatibility**

   * Old clients call `/currency-conversion-new/**`, but your new microservice expects `/currency-conversion-feign/**`.
   * Instead of breaking clients, rewrite the path.

2. **Hiding internal paths**

   * Internal service uses `/internal-api/v1/orders/**`
   * Expose to clients only as `/orders/**`.
   * Example:

     ```java
     f.rewritePath("/orders/(?<path>.*)", "/internal-api/v1/orders/${path}")
     ```

3. **Versioning APIs**

   * Public API: `/v1/products/**`
   * Internal service: `/api/products/**`

---

## **Note**
`f.rewritePath` changes the **incoming URL path** before sending it to the backend service.
It’s useful when you want clients to use **simple or old paths**, but your backend microservice expects a **different path**.

---
## **URL**
http://localhost:8765/get  
http://localhost:8765/currency-exchange/from/USD/to/INR  
http://localhost:8765/currency-conversion-feign/from/USD/to/INR/quantity/10  
http://localhost:8765/currency-conversion/from/USD/to/INR/quantity/10  
http://localhost:8765/currency-conversion-new/from/USD/to/INR/quantity/10