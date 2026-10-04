# Dockerize Each Service

This chapter explains how to run each microservice as a container.
Docker gives every service a consistent runtime, so the same service can run on a developer machine, test server, or cloud environment.

## Agenda

- [Problem Solved In This Chapter](#problem-solved-in-this-chapter)
- [What You Will Learn](#what-you-will-learn)
- [Simple Docker Flow](#simple-docker-flow)
- [Live Classroom Demo](#live-classroom-demo)
- [Dockerfile Template](#dockerfile-template)
- [How To Build And Run](#how-to-build-and-run)
- [Key Points Or Common Mistakes](#key-points-or-common-mistakes)
- [Chapter Summary](#chapter-summary)
- [Interview Questions And Answers](#interview-questions-and-answers)

## Problem Solved In This Chapter

Running many microservices directly from IDEs can become difficult.
Each service may need Java, environment variables, ports, and startup order.

This chapter solves that problem by packaging each service into its own Docker image.

## What You Will Learn

- What a Docker image is.
- What a Docker container is.
- How to write a Dockerfile for a Spring Boot service.
- How to pass environment variables to a container.
- How to map container ports to local machine ports.

## Simple Docker Flow

```text
Spring Boot project
      |
      v
mvn package
      |
      v
Docker image
      |
      v
Docker container
```

## Live Classroom Demo

This chapter includes a tiny Spring Boot service so you can show Docker working live.

Open this folder:

```bash
cd R-dockerize-each-service
```

Build the Maven JAR:

```bash
mvn clean package -DskipTests
```

Build the image:

```bash
docker build -t dockerized-spring-boot-service:local .
```

Run the container:

```bash
docker run --rm --name dockerized-spring-boot-service -p 8203:8203 dockerized-spring-boot-service:local
```

Open in browser:

```text
http://localhost:8203/docker/status
```

Or test from another terminal:

```powershell
Invoke-RestMethod http://localhost:8203/docker/status
```

Expected output:

```json
{
  "service": "dockerized-spring-boot-service",
  "status": "UP",
  "message": "This Spring Boot microservice is ready to run inside Docker"
}
```

Teaching point:

```text
The service is not running directly on the laptop runtime.
It is running inside a Docker container and is exposed through port mapping.
```

## Dockerfile Template

Use this template inside each Spring Boot service folder.
The same template is also available as:

```text
Dockerfile.spring-boot-template
```

```dockerfile
FROM eclipse-temurin:17-jre

WORKDIR /app

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

## How To Build And Run

For the fastest classroom demo, use:

```bash
mvn clean package -DskipTests
docker build -t dockerized-spring-boot-service:local .
docker run --rm --name dockerized-spring-boot-service -p 8203:8203 dockerized-spring-boot-service:local
```

For a real Spring Boot service, use the steps below.

Build the Spring Boot JAR first:

```bash
mvn clean package -DskipTests
```

Build the Docker image:

```bash
docker build -t currency-exchange-service:local .
```

Run the container:

```bash
docker run --name currency-exchange-service -p 8000:8000 currency-exchange-service:local
```

Pass an environment variable:

```bash
docker run --name currency-exchange-service -p 8000:8000 -e EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://host.docker.internal:8761/eureka currency-exchange-service:local
```

## Key Points Or Common Mistakes

- Build the JAR before building the Docker image.
- Do not copy `target`, `.git`, IDE files, or logs unnecessarily into the image.
- Use one image per service.
- Keep image names clear, such as `naming-server:local` or `api-gateway:local`.
- Expose the correct service port.
- In Docker networks, use service names instead of `localhost`.
- Do not put secrets directly in Dockerfiles.

## Chapter Summary

In this chapter, we solved the problem of packaging each microservice as a container.
Each service can now be built and run in a repeatable way.

Next chapter: [Docker Compose Setup](../S-docker-compose-setup/README.md).
It solves the next problem: starting Eureka, Gateway, database, and services with one command.

## Interview Questions And Answers

**Q1. What is Docker?**  
Docker packages an application and its runtime into a container.

**Q2. What is a Docker image?**  
An image is a reusable package used to create containers.

**Q3. What is a Docker container?**  
A container is a running instance of an image.

**Q4. Why dockerize microservices?**  
It makes services easier to run consistently across different environments.

**Q5. What is the purpose of `EXPOSE`?**  
It documents the port used by the container.

**Q6. Why should we use `.dockerignore`?**  
It prevents unnecessary files from being copied into the Docker build context.

**Q7. Why should containers avoid `localhost` for other services?**  
Inside a container, `localhost` means the container itself, not another service.

**Q8. Should secrets be stored in Docker images?**  
No. Pass secrets using environment variables or a secret manager.
