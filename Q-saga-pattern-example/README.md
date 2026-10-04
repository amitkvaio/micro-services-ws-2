# Saga Pattern Example

This chapter explains how to handle distributed transactions safely in microservices.
A single business flow may touch Order Service, Payment Service, Inventory Service, and Notification Service.
Saga helps coordinate these steps without using one large database transaction.

## Agenda

- [Problem Solved In This Chapter](#problem-solved-in-this-chapter)
- [What You Will Learn](#what-you-will-learn)
- [Saga Flow](#saga-flow)
- [Simple Example](#simple-example)
- [Live Classroom Demo](#live-classroom-demo)
- [Choreography Style](#choreography-style)
- [Orchestration Style](#orchestration-style)
- [How To Run Or Practice](#how-to-run-or-practice)
- [Key Points Or Common Mistakes](#key-points-or-common-mistakes)
- [Chapter Summary](#chapter-summary)
- [Interview Questions And Answers](#interview-questions-and-answers)

## Problem Solved In This Chapter

In microservices, each service usually owns its own database.
We should not use one shared database transaction across all services.

This chapter solves that problem using Saga.
Saga breaks one large business transaction into smaller local transactions and compensation steps.

## What You Will Learn

- Why distributed transactions are hard in microservices.
- What Saga means.
- How local transaction and compensation work.
- Difference between choreography and orchestration.
- Why idempotency and retries are important.

## Saga Flow

```text
1. Order Service creates order with status PENDING
2. Payment Service reserves or charges payment
3. Inventory Service reserves stock
4. Notification Service sends confirmation
5. Order Service marks order CONFIRMED
```

Failure example:

```text
1. Order created
2. Payment completed
3. Inventory failed
4. Payment is refunded
5. Order is marked CANCELLED
```

## Simple Example

The sample flow is stored in:

```text
sample-flow/order-saga-events.ndjson
```

One successful saga uses this ID:

```text
saga-order-1001
```

One failed saga uses this ID:

```text
saga-order-1002
```

Search one Saga ID in logs or events to understand the full business flow.

## Live Classroom Demo

Run the Spring Boot Maven project:

```bash
mvn spring-boot:run
```

Show the full saga demo:

```powershell
Invoke-RestMethod http://localhost:8202/saga/demo
```

Show only the successful flow:

```powershell
Invoke-RestMethod http://localhost:8202/saga/success
```

Show the failed flow with compensation:

```powershell
Invoke-RestMethod http://localhost:8202/saga/failure
```

Expected output contains:

```text
Saga ID          | Service            | Action                   | Status
saga-order-1001  | Order Service      | Create order             | PENDING
saga-order-1001  | Payment Service    | Take payment             | PAID
saga-order-1001  | Inventory Service  | Reserve stock            | RESERVED
saga-order-1001  | Order Service      | Confirm order            | CONFIRMED
saga-order-1002  | Inventory Service  | Reserve stock            | FAILED
saga-order-1002  | Payment Service    | Refund payment           | REFUNDED
saga-order-1002  | Order Service      | Cancel order             | CANCELLED
```

Optional script demo:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\run-saga-demo.ps1
```

Teaching point:

```text
Saga uses local transactions and compensation.
When inventory fails, payment is refunded and order is cancelled.
```

## Choreography Style

In choreography, services react to events without one central controller.

```text
OrderCreated -> PaymentCompleted -> InventoryReserved -> OrderConfirmed
```

Benefits:

- Simple for small flows.
- No central coordinator.
- Services stay loosely coupled.

Trade-off:

- Flow can become hard to understand when many services are involved.

## Orchestration Style

In orchestration, one Saga orchestrator tells each service what to do.

```text
Saga Orchestrator -> Create Order
Saga Orchestrator -> Process Payment
Saga Orchestrator -> Reserve Inventory
Saga Orchestrator -> Confirm Order
```

Benefits:

- Flow is easier to see in one place.
- Better for complex workflows.

Trade-off:

- The orchestrator becomes an important component and must be reliable.

## How To Run Or Practice

This chapter has a runnable Spring Boot Maven demo and a design example.
Start with:

```bash
mvn spring-boot:run
```

Then call:

```powershell
Invoke-RestMethod http://localhost:8202/saga/demo
```

Use the sample event file to understand the flow:

```text
sample-flow/order-saga-events.ndjson
```

Practice task:

1. Create an Order Service event named `OrderCreated`.
2. Let Payment Service consume it and publish `PaymentCompleted`.
3. Let Inventory Service consume it and publish `InventoryReserved` or `InventoryFailed`.
4. Add compensation: if inventory fails, publish `PaymentRefunded`.

## Key Points Or Common Mistakes

- Do not share one database across all services just to make transactions easy.
- Every service should complete its own local transaction.
- Add compensation actions for failure cases.
- Use a `sagaId` to connect all events in one business flow.
- Make each event consumer idempotent.
- Plan timeout and retry behavior.
- Store saga state if the flow is important for business.

## Chapter Summary

In this chapter, we solved the problem of distributed transactions using the Saga pattern.
Saga keeps each service independent and still makes the business flow safe.

Next chapter: [Dockerize Each Service](../R-dockerize-each-service/README.md).
It solves the next problem: packaging every microservice as a container.

## Interview Questions And Answers

**Q1. What is Saga pattern?**  
Saga is a way to manage a business transaction across multiple services using local transactions and compensation.

**Q2. Why not use one database transaction across services?**  
Because services should be independent and may use different databases.

**Q3. What is compensation?**  
Compensation is an action that reverses a completed step when a later step fails.

**Q4. What is choreography?**  
Choreography means services react to events and no central service controls the full flow.

**Q5. What is orchestration?**  
Orchestration means one coordinator controls the saga steps.

**Q6. Why is idempotency important in Saga?**  
Events may be retried, so repeated processing should not create duplicate business actions.

**Q7. What is a Saga ID?**  
Saga ID connects all events and logs for one business transaction.

**Q8. When should we use Saga?**  
Use Saga when one business process updates data owned by multiple microservices.
