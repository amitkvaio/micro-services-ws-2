# Event-Driven Microservices With Kafka/RabbitMQ

This chapter explains asynchronous communication between microservices.
Instead of one service waiting for another service through a direct REST call, a service publishes an event and another service consumes it later.

## Agenda

- [Problem Solved In This Chapter](#problem-solved-in-this-chapter)
- [What You Will Learn](#what-you-will-learn)
- [Architecture](#architecture)
- [Simple Example](#simple-example)
- [Live Classroom Demo Without Kafka/RabbitMQ](#live-classroom-demo-without-kafkarabbitmq)
- [Kafka Example](#kafka-example)
- [RabbitMQ Example](#rabbitmq-example)
- [How To Run](#how-to-run)
- [Key Points Or Common Mistakes](#key-points-or-common-mistakes)
- [Chapter Summary](#chapter-summary)
- [Interview Questions And Answers](#interview-questions-and-answers)

## Problem Solved In This Chapter

Synchronous REST calls are simple, but they tightly connect services.
If Service B is slow or down, Service A can also become slow or fail.

This chapter solves that problem by using events.
One service publishes an event, and other services process it independently.

## What You Will Learn

- What event-driven communication means.
- When to use Kafka and when to use RabbitMQ.
- How producer and consumer services communicate asynchronously.
- Why events should include useful fields like `eventId`, `eventType`, `traceId`, and `timestamp`.
- How async communication improves loose coupling.

## Architecture

```text
Order Service
    |
    | publishes OrderCreated event
    v
Kafka topic or RabbitMQ queue
    |
    +--> Payment Service consumes event
    +--> Inventory Service consumes event
    +--> Notification Service consumes event
```

## Simple Example

The sample event is stored in:

```text
sample-events/order-created.json
```

Example event:

```json
{
  "eventId": "evt-1001",
  "eventType": "OrderCreated",
  "traceId": "trace-order-1001",
  "orderId": "ORD-1001",
  "customerId": "CUST-501",
  "amount": 2500.0,
  "currency": "INR",
  "createdAt": "2026-10-04T10:30:00Z"
}
```

## Live Classroom Demo Without Kafka/RabbitMQ

Use this demo first when you want to explain the concept quickly without installing Kafka or RabbitMQ.
It simulates a producer, a broker file, and three consumers.

Run from this chapter folder:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\run-local-event-demo.ps1
```

Expected output:

```text
EVENT PRODUCER
Published event: OrderCreated for ORD-1001
Published event: OrderCreated for ORD-1002

EVENT CONSUMERS
Payment Service consumed ORD-1001, traceId=trace-order-1001
Inventory Service consumed ORD-1001, traceId=trace-order-1001
Notification Service consumed ORD-1001, traceId=trace-order-1001
```

Check the generated event queue file:

```powershell
Get-Content .\target\orders.events.jsonl
```

Teaching point:

```text
The producer did not call Payment, Inventory, or Notification directly.
It published an event, and consumers processed it independently.
```

## Kafka Example

Kafka is a good fit when events must be stored, replayed, and consumed by many services.

Topic:

```text
orders.events
```

Producer idea:

```java
kafkaTemplate.send("orders.events", orderCreatedEvent);
```

Consumer idea:

```java
@KafkaListener(topics = "orders.events", groupId = "payment-service")
public void consume(OrderCreatedEvent event) {
    // process payment
}
```

## RabbitMQ Example

RabbitMQ is a good fit for queue-based work distribution and routing.

Exchange:

```text
orders.exchange
```

Queue:

```text
payment.queue
```

Producer idea:

```java
rabbitTemplate.convertAndSend("orders.exchange", "order.created", orderCreatedEvent);
```

Consumer idea:

```java
@RabbitListener(queues = "payment.queue")
public void consume(OrderCreatedEvent event) {
    // process payment
}
```

## How To Run

For the quickest classroom demo, run:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\run-local-event-demo.ps1
```

For a real broker demo, use Kafka or RabbitMQ below.

Start Kafka locally:

```bash
docker compose -f docker-compose.kafka.yml up -d
```

Kafka UI:

```text
http://localhost:8088
```

Start RabbitMQ locally:

```bash
docker compose -f docker-compose.rabbitmq.yml up -d
```

RabbitMQ UI:

```text
http://localhost:15672
```

Default RabbitMQ login:

```text
guest / guest
```

Stop the local tools:

```bash
docker compose -f docker-compose.kafka.yml down
docker compose -f docker-compose.rabbitmq.yml down
```

## Key Points Or Common Mistakes

- Do not use async events when the caller needs an immediate answer.
- Keep event names clear, such as `OrderCreated`, `PaymentCompleted`, or `InventoryReserved`.
- Add `traceId` to events so logs and traces can be connected.
- Make consumers idempotent because the same event may be delivered more than once.
- Use retry and dead-letter queues for failed messages.
- Do not put large files or sensitive secrets inside events.

## Chapter Summary

In this chapter, we solved async communication between services using Kafka or RabbitMQ.
This reduces tight coupling between services and makes the system more flexible.

Next chapter: [Saga Pattern Example](../Q-saga-pattern-example/README.md).
It solves the next problem: handling distributed transactions safely when multiple services must work together.

## Interview Questions And Answers

**Q1. What is event-driven architecture?**  
It is a style where services communicate by publishing and consuming events.

**Q2. Why use Kafka or RabbitMQ instead of only REST?**  
They help services communicate asynchronously and reduce direct dependency between services.

**Q3. What is a producer?**  
A producer is a service that publishes an event.

**Q4. What is a consumer?**  
A consumer is a service that reads and processes an event.

**Q5. What is the difference between Kafka and RabbitMQ?**  
Kafka is strong for event streaming and replay. RabbitMQ is strong for queue-based message routing.

**Q6. What is idempotency?**  
Idempotency means processing the same event more than once should not create wrong duplicate results.

**Q7. What is a dead-letter queue?**  
It stores messages that could not be processed after retries.

**Q8. Why should events include a Trace ID?**  
Trace ID helps connect the event with logs and request flow across services.
