# Docker Compose Setup

This chapter explains how to start multiple microservices with one command.
Docker Compose is useful for local development because it can start Eureka, API Gateway, database, and services together.

## Agenda

- [Problem Solved In This Chapter](#problem-solved-in-this-chapter)
- [What You Will Learn](#what-you-will-learn)
- [Local Architecture](#local-architecture)
- [Live Classroom Demo](#live-classroom-demo)
- [Compose File](#compose-file)
- [How To Run](#how-to-run)
- [Useful URLs](#useful-urls)
- [Key Points Or Common Mistakes](#key-points-or-common-mistakes)
- [Chapter Summary](#chapter-summary)
- [Interview Questions And Answers](#interview-questions-and-answers)

## Problem Solved In This Chapter

Starting every microservice manually is slow.
Students may forget to start Eureka first, or they may use the wrong URL between services.

This chapter solves that problem using Docker Compose.
One command can start the local system.

## What You Will Learn

- How Docker Compose groups services.
- How containers communicate using service names.
- How to start Eureka, Gateway, database, and services together.
- Why startup order matters.
- How to stop and clean local containers.

## Local Architecture

```text
Client
  |
  v
API Gateway
  |
  +--> Currency Exchange Service
  +--> Currency Conversion Service
  |
  v
Eureka Naming Server

MySQL Database is available for services that need persistence.
```

## Live Classroom Demo

This chapter includes a small runnable Compose stack in:

```text
live-demo/docker-compose.yml
```

It starts:

- MySQL
- One Spring Boot Maven service from this chapter

Run it:

```bash
cd S-docker-compose-setup/live-demo
mvn -f ..\pom.xml clean package -DskipTests
docker compose up -d
```

Check running containers:

```bash
docker compose ps
```

Test the Spring Boot service:

```powershell
Invoke-RestMethod http://localhost:8204/compose/status
```

Expected output:

```json
{
  "service": "docker-compose-setup-demo",
  "status": "UP",
  "message": "This Spring Boot service can be started from Docker Compose"
}
```

Stop the demo:

```bash
docker compose down
```

Teaching point:

```text
One command starts MySQL and a Spring Boot service.
For the full workspace, use the main docker-compose.yml in this chapter.
```

## Compose File

The sample file is:

```text
docker-compose.yml
```

It contains:

- Eureka naming server
- API Gateway
- Currency exchange service
- Currency conversion service
- MySQL database

The file uses `build.context` values that point to the existing chapter folders.
Build the service JARs before using the compose file.

## How To Run

For the fastest classroom demo:

```bash
cd S-docker-compose-setup/live-demo
mvn -f ..\pom.xml clean package -DskipTests
docker compose up -d
```

Then test:

```powershell
Invoke-RestMethod http://localhost:8204/compose/status
```

For the actual workspace services, use the steps below.

From this chapter folder:

```bash
cd S-docker-compose-setup
```

Build the JAR for each service you want to run:

```bash
cd ../A-naming-server && mvn clean package -DskipTests
cd ../B-currency-exchange-service && mvn clean package -DskipTests
cd ../C-currency-conversion-service && mvn clean package -DskipTests
cd ../F-spring-api-gateway-With-Routes && mvn clean package -DskipTests
```

Start the stack:

```bash
cd ../S-docker-compose-setup
docker compose up --build
```

Stop the stack:

```bash
docker compose down
```

Stop and remove database volume:

```bash
docker compose down -v
```

## Useful URLs

| Component | URL |
| --- | --- |
| Eureka | `http://localhost:8761` |
| API Gateway | `http://localhost:8765` |
| MySQL | `localhost:3306` |

## Key Points Or Common Mistakes

- Start Eureka before services register with it.
- In Compose, use service names like `naming-server`, not `localhost`.
- Build JAR files before building Docker images.
- Keep ports unique on the host machine.
- Use environment variables for configuration.
- Do not store real production passwords in compose files.
- Add health checks for real projects.

## Chapter Summary

In this chapter, we solved the problem of starting many services manually.
Docker Compose gives one local command for the microservices stack.

Next chapter: [Kubernetes Basics](../T-kubernetes-basics/README.md).
It solves the next problem: deploying microservices to a local Kubernetes cluster.

## Interview Questions And Answers

**Q1. What is Docker Compose?**  
Docker Compose runs multiple containers together using one YAML file.

**Q2. Why is Docker Compose useful for microservices?**  
It starts related services, networks, and databases together for local development.

**Q3. How do containers call each other in Compose?**  
They use Compose service names as hostnames.

**Q4. What is `depends_on`?**  
It controls startup order, but it does not always mean the dependency is fully ready.

**Q5. Why should we avoid `localhost` between containers?**  
Inside a container, `localhost` points to that same container.

**Q6. What is a Docker volume?**  
A volume stores data outside the container lifecycle.

**Q7. Why use environment variables in Compose?**  
They keep configuration flexible for different environments.

**Q8. Is Docker Compose the same as Kubernetes?**  
No. Compose is simpler and mostly local. Kubernetes is stronger for orchestration and production-style deployments.
