# Distributed Tracing - Service A

This project demonstrates distributed tracing with Spring Boot microservices.
Service A starts the request flow and calls Service B.

Request flow:

```text
Service A -> Service B -> Service C -> Service D
```

## Agenda

- Understand Trace ID and Span ID.
- Configure Micrometer Tracing and Zipkin.
- Start the distributed tracing flow from Service A.
- Call Service B and verify trace propagation.

## Problem Solved In This Chapter

This chapter solves the problem of debugging a request that travels through many services.
Distributed tracing gives one Trace ID for the full request journey.

## What Is Distributed Tracing?

In microservices, one user request can travel through many services.
Logs are spread across different applications, so debugging becomes difficult.

Distributed tracing solves this by adding:

- Trace ID: one id for the full request journey.
- Span ID: one id for each service call or operation.

Using these ids, we can follow one request across all services.

## Sleuth And Zipkin

Older Spring Boot projects used **Spring Cloud Sleuth**.
For Spring Boot 3.x and Spring Cloud 2022+, Sleuth is deprecated.

Use:

- Micrometer Tracing
- Brave bridge
- Zipkin reporter

## Dependencies

Add these dependencies in each microservice:

```xml
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
```

## Service A Configuration

`application.properties`

```properties
spring.application.name=distributed-tracing-for-service-a
server.port=8000

management.tracing.sampling.probability=1.0
management.zipkin.tracing.endpoint=http://localhost:9411/api/v2/spans

logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p [traceId: %X{traceId},spanId: %X{spanId}] %-40.40c{1.} : %m%n
```

## Main Class

```java
@SpringBootApplication
public class DistributedTracingForServiceA {
    public static void main(String[] args) {
        SpringApplication.run(DistributedTracingForServiceA.class, args);
        System.out.println("DistributedTracingForService_A has started successfully.");
    }
}
```

## Controller

```java
@RestController
@RequestMapping("/a")
public class DistributedTracingForServiceAController {

    private static final Logger log =
            LoggerFactory.getLogger(DistributedTracingForServiceAController.class);

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/call")
    public String callServiceA() {
        log.info("Inside Service A");
        String response = restTemplate.getForObject("http://localhost:8001/b/call", String.class);
        return "Response from Service A -> " + response;
    }
}
```

## RestTemplate Bean

```java
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
```

## How To Run

1. Start Zipkin on port `9411`.
2. Start Service A on port `8000`.
3. Start Service B on port `8001`.
4. Start Service C on port `8002`.
5. Start Service D on port `8003`.
6. Call Service A and check logs in all services.

## URLs

Zipkin dashboard:

```text
http://localhost:9411/
```

Service APIs:

```text
http://localhost:8000/a/call
http://localhost:8001/b/call
http://localhost:8002/c/call
http://localhost:8003/d/call
```

## Sample Logs

```text
Service A traceId=68a1d0be25bfc63f3b08f51bc452d532 spanId=3b08f51bc452d532
Service B traceId=68a1d0be25bfc63f3b08f51bc452d532 spanId=a9a6b2881d7ff54e
Service C traceId=68a1d0be25bfc63f3b08f51bc452d532 spanId=ddba1c20662a794e
Service D traceId=68a1d0be25bfc63f3b08f51bc452d532 spanId=934f83ae690fe38a
```

The same Trace ID proves that one request travelled through all four services.
Different Span IDs show each individual service operation.

## Interview Points

- Trace ID identifies the complete request flow.
- Span ID identifies one operation inside the request flow.
- Zipkin is used to view traces in a UI.
- Micrometer Tracing is the modern replacement for Sleuth in Spring Boot 3.

## Chapter Summary

In this chapter, Service A started the distributed trace and called Service B.
The next chapter solves the problem of continuing the same trace through Service B.

## Interview Questions And Answers

**Q1. What is Trace ID?**  
Trace ID is the common id for the complete request journey across services.

**Q2. What is Span ID?**  
Span ID identifies one operation or service call inside a trace.

**Q3. What is Zipkin?**  
Zipkin is a UI and tracing system used to view request flow and latency.

**Q4. What replaced Sleuth in Spring Boot 3?**  
Micrometer Tracing is used instead of Spring Cloud Sleuth.
