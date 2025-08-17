
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

## **Dependencies Requied / Add these dependencies in all the microservices.**
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.5.3</version>
		<relativePath /> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.limit.service</groupId>
	<artifactId>K-distributed-tracing-for-services-a</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>K-distributed-tracing-for-services-a</name>
	<description>exploring distributed tracing.</description>
	<properties>
		<java.version>17</java.version>
		<spring-cloud.version>2025.0.0</spring-cloud.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
		<dependency>
			<groupId>io.micrometer</groupId>
			<artifactId>micrometer-tracing-bridge-brave</artifactId>
		</dependency>
		<dependency>
			<groupId>io.zipkin.reporter2</groupId>
			<artifactId>zipkin-reporter-brave</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-bootstrap</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<scope>runtime</scope>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>
	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-dependencies</artifactId>
				<version>${spring-cloud.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>
</project>
```

## **Add below properties in the application.properties file in each of the microservices**

## **Service A properties**
```properties

spring.application.name=distributed-tracing-for-service-a
server.port=8000

# Enable tracing (100% sampling)
management.tracing.sampling.probability=1.0

# Export traces to Zipkin
management.zipkin.tracing.endpoint=http://localhost:9411/api/v2/spans
# This is the correct way to explicitly define the log format
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p [traceId: %X{traceId},spanId: %X{spanId}] %-40.40c{1.} : %m%n

# %d{yyyy-MM-dd HH:mm:ss.SSS}: Prints the date and time.
# %-5p: Prints the log level (e.g., INFO, ERROR).
# [traceId: %X{traceId},spanId: %X{spanId}]: This is where you explicitly insert the Trace ID and Span ID from the MDC.
# %-40.40c{1.}: Prints the logger's name, padded to 40 characters.
# %m%n: Prints the log message and a new line.
```
## **For Service A**
```java
package com.amit.distributed.tracing;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DistributedTracingForService_A {
	public static void main(String[] args) {
		SpringApplication.run(DistributedTracingForService_A.class, args);
		System.out.println("DistributedTracingForService_A has started successfull!!");
	}
}
```
```java
package com.amit.distributed.tracing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/a")
public class DistributedTracingForServiceAController {
	
	private static final Logger log = LoggerFactory.getLogger(DistributedTracingForServiceAController.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	@GetMapping("/call")
	public String callServiceA() {
		log.info("Inside Service A");
		String response = restTemplate.getForObject("http://localhost:8001/b/call", String.class);
		return "Response from Service A → " + response;
	}
}

```

```java
package com.amit.distributed.tracing;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
    	System.out.println("################# Returing RestTemplate object A ######################33333");
        return builder.build();
    }
}
```
#### **Similarly Devlope the Micro-services B,C,D Where**
```
 Micro-services A calling > 
    Micro-services B > 
        Micro-services C > 
            Micro-services D. 
```

```
http://localhost:9411/  ==> For access the zipkin dashboard
http://localhost:8000/a/call ==> Try to access this --> It will call the rest of the other microservices.

http://localhost:8001/b/call 
http://localhost:8002/c/call  
http://localhost:8003/d/call 
```

## **Logs / Check the console logs in each of the microservices.**

```
2025-08-17 18:23:18.776 INFO  [traceId: 68a1d0be25bfc63f3b08f51bc452d532,spanId: 3b08f51bc452d532] .DistributedTracingForServiceAController : Inside Service A
2025-08-17 18:23:18.779 INFO  [traceId: 68a1d0be25bfc63f3b08f51bc452d532,spanId: a9a6b2881d7ff54e] .DistributedTracingForServiceBController : Inside Service B
2025-08-17 18:23:18.784 INFO  [traceId: 68a1d0be25bfc63f3b08f51bc452d532,spanId: ddba1c20662a794e] .DistributedTracingForServiceCController : Inside Service C
2025-08-17 18:23:18.787 INFO  [traceId: 68a1d0be25bfc63f3b08f51bc452d532,spanId: 934f83ae690fe38a] .DistributedTracingForServiceDController : Inside Service D

```