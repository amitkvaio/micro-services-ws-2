# Centralized Logging With ELK/OpenSearch

This chapter shows how logs from many microservices can be searched in one UI.
It uses OpenSearch and OpenSearch Dashboards as a simple ELK-style local setup.

Request tracing tells us where one request travelled.
Centralized logging tells us what each service logged during that request.

## Agenda

- [Problem Solved In This Chapter](#problem-solved-in-this-chapter)
- [What You Will Learn](#what-you-will-learn)
- [Architecture](#architecture)
- [Simple Example](#simple-example)
- [How To Run](#how-to-run)
- [How To Search Logs In One UI](#how-to-search-logs-in-one-ui)
- [How Real Services Can Send Logs](#how-real-services-can-send-logs)
- [Key Points Or Common Mistakes](#key-points-or-common-mistakes)
- [Chapter Summary](#chapter-summary)
- [Interview Questions And Answers](#interview-questions-and-answers)

## Problem Solved In This Chapter

In a microservices system, every service writes its own logs.
If logs stay only in each service console, debugging is slow.

This chapter solves that problem by collecting logs in OpenSearch so we can search all service logs from one UI.

## What You Will Learn

- Why centralized logging is needed in microservices.
- How OpenSearch stores logs in an index.
- How OpenSearch Dashboards helps search logs in one screen.
- How to search logs by `serviceName`, `traceId`, `level`, `endpoint`, and message.
- How centralized logs work together with distributed tracing.

## Architecture

```text
Service A logs
Service B logs
Service C logs
Service D logs
        |
        v
OpenSearch index: microservices-logs-demo
        |
        v
OpenSearch Dashboards UI
```

In real projects, services usually send logs through Logstash, Filebeat, Fluent Bit, or an OpenTelemetry collector.
For this chapter, we keep it simple and import sample microservice logs directly into OpenSearch.

## Simple Example

The sample logs contain multiple services using the same Trace ID:

```text
trace-2026-elk-demo-001
```

That Trace ID appears in:

- `distributed-tracing-service-a`
- `distributed-tracing-service-b`
- `distributed-tracing-service-c`
- `distributed-tracing-service-d`

When you search this Trace ID in OpenSearch Dashboards, you can see the full log flow in one place.

## How To Run

Open a terminal in this chapter folder:

```bash
cd O-Centralized-loggin-with-ELK-open-search
```

Start the Spring Boot Maven project:

```bash
mvn spring-boot:run
```

Test the live Java example:

```powershell
Invoke-RestMethod http://localhost:8200/logs/demo
```

Search by Trace ID:

```powershell
Invoke-RestMethod "http://localhost:8200/logs/search?traceId=trace-2026-elk-demo-001"
```

Search only errors:

```powershell
Invoke-RestMethod "http://localhost:8200/logs/search?level=ERROR"
```

Expected output contains service log records like:

```json
{
  "serviceName": "currency-exchange-service",
  "level": "ERROR",
  "traceId": "trace-2026-elk-demo-001",
  "endpoint": "/currency-exchange/from/USD/to/INR",
  "message": "Database timeout while reading exchange value"
}
```

The console logs also print the same trace ID, which helps students understand how logs can be searched across services.

## Optional OpenSearch UI Demo

Start OpenSearch and OpenSearch Dashboards:

```bash
docker compose up -d
```

Import the sample logs:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\import-sample-logs.ps1
```

Open the UI:

```text
http://localhost:5601
```

OpenSearch API:

```text
http://localhost:9200
```

Stop the stack:

```bash
docker compose down
```

## How To Search Logs In One UI

1. Open `http://localhost:5601`.
2. Go to **Discover**.
3. Create an index pattern named `microservices-logs-*`.
4. Select `@timestamp` as the time field.
5. Search using the examples below.

Search one request across all services:

```text
traceId:"trace-2026-elk-demo-001"
```

Search logs from one service:

```text
serviceName:"distributed-tracing-service-c"
```

Search only errors:

```text
level:"ERROR"
```

Search one endpoint:

```text
endpoint:"/currency-exchange/from/USD/to/INR"
```

You can also search from PowerShell:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\search-sample-logs.ps1 -TraceId trace-2026-elk-demo-001
```

## How Real Services Can Send Logs

A real Spring Boot service should include useful fields in every log:

```text
timestamp, serviceName, environment, level, traceId, spanId, endpoint, message
```

For simple local practice, each service can write logs to a file:

```properties
spring.application.name=currency-exchange-service
logging.file.name=logs/${spring.application.name}.log
```

In production, prefer structured JSON logs and ship them using a log collector.
Common choices are:

- Logstash
- Filebeat
- Fluent Bit
- OpenTelemetry Collector

## Key Points Or Common Mistakes

- Centralized logging is not the same as distributed tracing.
- Use the same `traceId` in logs and traces so debugging becomes easy.
- Do not log passwords, tokens, OTPs, card numbers, or secrets.
- Add `serviceName` and `environment` to every log event.
- Keep log messages simple and searchable.
- Use `ERROR` only for real failures; too many false errors make production debugging hard.
- OpenSearch stores logs; OpenSearch Dashboards helps search and visualize them.

## Chapter Summary

In this chapter, we solved the problem of searching logs from many services in one UI.
We used OpenSearch as the log store and OpenSearch Dashboards as the search screen.

Chapters K to N showed how to trace one request across services.
Chapter O shows how to search the logs for that same request.

Next chapter: [Event-Driven Microservices With Kafka/RabbitMQ](../P-event-driven-microservices-with-kafka-rabbitmq/README.md).
It solves the next problem: asynchronous communication between services.

**Practice task:** Add logs from your running Service A, B, C, and D applications into the same OpenSearch index and search by Trace ID.

## Interview Questions And Answers

**Q1. What is centralized logging?**  
Centralized logging means collecting logs from many services into one searchable system.

**Q2. Why is centralized logging important in microservices?**  
Because each service runs separately, and checking each console manually is slow.

**Q3. What is OpenSearch used for here?**  
OpenSearch stores and indexes logs so they can be searched quickly.

**Q4. What is OpenSearch Dashboards?**  
It is the UI used to search, filter, and visualize logs stored in OpenSearch.

**Q5. What fields should a good log contain?**  
It should contain timestamp, service name, level, trace ID, endpoint, and message.

**Q6. How does centralized logging help with distributed tracing?**  
Tracing shows the request path, and logs explain what happened inside each service.

**Q7. What should we avoid logging?**  
Never log passwords, tokens, personal secrets, or sensitive customer data.

**Q8. What is the difference between ELK and OpenSearch?**  
ELK commonly means Elasticsearch, Logstash, and Kibana. OpenSearch is an open-source search and dashboard stack that can be used for similar log search use cases.
