
---

# 📌 What is Distributed Tracing?

* In **microservices architecture**, a single request from the client may travel through multiple services.
* Example:
  `Client → API Gateway → Order Service → Payment Service → Inventory Service`
* Debugging an issue is **hard** because logs are scattered across different services.
* **Distributed Tracing** solves this problem by assigning a unique **Trace ID** and **Span IDs** for each request flow.
* This helps us **track the entire journey of a request** across services.

---

# 📌 What is Sleuth?

* **Spring Cloud Sleuth** automatically adds unique IDs (Trace ID, Span ID) to logs.
* Every log line is tagged with these IDs.
* Example log output:

  ```
  [traceId=4f29a4c3f9b7dabc spanId=4f29a4c3f9b7dabc] INFO  OrderService - Creating new order
  ```

---

# 📌 What is Zipkin?

* **Zipkin** is a distributed tracing system.
* It collects trace data from microservices and shows it in a **web UI**.
* With Zipkin you can **visualize the request flow** between microservices.

---

# 📌 How Sleuth + Zipkin Work Together

1. **Sleuth** adds trace IDs to logs and sends trace data.
2. **Zipkin** collects this data and provides visualization.
3. Developers can then easily **track latency, bottlenecks, and failures**.
---
## **Deprecated**

Since Spring Boot 3.x (and Spring Cloud 2022+), spring-cloud-sleuth is deprecated.
Now tracing is handled using **Micrometer Tracing + Brave bridge + Zipkin Reporter**.

## **Dependencies Requied**
```
<dependencies>
    <!-- Spring Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Micrometer Tracing with Brave -->
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-tracing-bridge-brave</artifactId>
    </dependency>

    <!-- Zipkin Reporter -->
    <dependency>
        <groupId>io.zipkin.reporter2</groupId>
        <artifactId>zipkin-reporter-brave</artifactId>
    </dependency>
</dependencies>

```

## **Add below properties in the application.properties file in each of the microservices**

```properties

spring.application.name=service-a
server.port=8081

# Enable tracing (100% sampling)
management.tracing.sampling.probability=1.0

# Export traces to Zipkin
management.zipkin.tracing.endpoint=http://localhost:9411/api/v2/spans

```

```
http://localhost:9411/  
http://localhost:8000/a/call  
http://localhost:8001/b/call 
http://localhost:8002/c/call  
http://localhost:8003/d/call 
```