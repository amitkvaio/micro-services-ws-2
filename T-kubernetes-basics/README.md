# Kubernetes Basics

This chapter explains how to deploy microservices to Kubernetes locally using Minikube or kind.
Kubernetes helps run, scale, restart, and connect containers in a cluster.

## Agenda

- [Problem Solved In This Chapter](#problem-solved-in-this-chapter)
- [What You Will Learn](#what-you-will-learn)
- [Kubernetes Architecture](#kubernetes-architecture)
- [Live Classroom Demo](#live-classroom-demo)
- [Local Setup](#local-setup)
- [How To Deploy](#how-to-deploy)
- [Useful Commands](#useful-commands)
- [Key Points Or Common Mistakes](#key-points-or-common-mistakes)
- [Chapter Summary](#chapter-summary)
- [Interview Questions And Answers](#interview-questions-and-answers)

## Problem Solved In This Chapter

Docker Compose is good for local multi-container development.
But real microservices need stronger deployment features like self-healing, scaling, service discovery, and rolling updates.

This chapter solves that problem by introducing Kubernetes basics.

## What You Will Learn

- What Pod, Deployment, and Service mean.
- How to deploy a microservice locally.
- How services communicate inside Kubernetes.
- How to expose Gateway outside the cluster.
- How Minikube or kind helps practice Kubernetes locally.

## Kubernetes Architecture

```text
Deployment
   |
   v
ReplicaSet
   |
   v
Pod running container
   |
   v
Service gives stable network name
```

For this workspace:

```text
api-gateway -> currency services -> naming-server
```

## Live Classroom Demo

This chapter includes simple Kubernetes manifests in:

```text
live-demo/
```

They deploy two small HTTP services:

- `exchange-service`
- `conversion-service`

Start a local cluster using Minikube:

```bash
minikube start
```

Or using kind:

```bash
kind create cluster --name microservices-local
```

Deploy the live demo:

```bash
kubectl apply -f live-demo/
```

Check pods:

```bash
kubectl get pods -n microservices-live-demo
```

Expected output should show running pods:

```text
exchange-service-xxxxx      1/1     Running
conversion-service-xxxxx    1/1     Running
```

Port-forward the exchange service:

```bash
kubectl port-forward svc/exchange-service 8000:8000 -n microservices-live-demo
```

Test from another terminal:

```powershell
Invoke-RestMethod http://localhost:8000
```

Expected output:

```json
{
  "service": "exchange-service",
  "message": "Hello from Kubernetes exchange service"
}
```

Scale the exchange service:

```bash
kubectl scale deployment exchange-service --replicas=2 -n microservices-live-demo
kubectl get pods -n microservices-live-demo
```

Clean up:

```bash
kubectl delete -f live-demo/
```

Teaching point:

```text
Kubernetes runs containers as Pods.
Deployment manages replicas.
Service gives a stable name and access point.
```

## Local Setup

Option 1: Minikube

```bash
minikube start
```

Option 2: kind

```bash
kind create cluster --name microservices-local
```

Build Docker images first, or push them to a registry that your local cluster can access.

## How To Deploy

For the fastest classroom demo:

```bash
kubectl apply -f live-demo/
kubectl get pods -n microservices-live-demo
kubectl port-forward svc/exchange-service 8000:8000 -n microservices-live-demo
```

Then test:

```powershell
Invoke-RestMethod http://localhost:8000
```

For the workspace-style manifests, use the steps below.

Apply the sample manifests:

```bash
kubectl apply -f manifests/
```

Check pods:

```bash
kubectl get pods -n microservices-demo
```

Check services:

```bash
kubectl get svc -n microservices-demo
```

Forward the Gateway port:

```bash
kubectl port-forward svc/api-gateway 8765:8765 -n microservices-demo
```

Open:

```text
http://localhost:8765
```

Remove the demo:

```bash
kubectl delete -f manifests/
```

## Useful Commands

```bash
kubectl get all -n microservices-demo
kubectl logs deployment/api-gateway -n microservices-demo
kubectl describe pod <pod-name> -n microservices-demo
kubectl rollout status deployment/api-gateway -n microservices-demo
kubectl scale deployment currency-exchange-service --replicas=2 -n microservices-demo
```

## Key Points Or Common Mistakes

- A Pod is the smallest runnable unit in Kubernetes.
- A Deployment manages replicas and rolling updates.
- A Service gives a stable DNS name for Pods.
- Use ConfigMap for non-secret configuration.
- Use Secret for sensitive values.
- Do not use `localhost` to call another service inside Kubernetes.
- Use readiness and liveness probes in real projects.
- Keep image names and tags clear.

## Chapter Summary

In this chapter, we solved the problem of deploying microservices to a local Kubernetes cluster.
You can now practice Pods, Deployments, Services, scaling, logs, and port forwarding.

This is the final chapter in this extended workspace sequence.

**Practice task:** Deploy Eureka, Gateway, and two services to Minikube or kind, then scale one service to two replicas and check the logs.

## Interview Questions And Answers

**Q1. What is Kubernetes?**  
Kubernetes is a container orchestration platform that runs and manages containers.

**Q2. What is a Pod?**  
A Pod is the smallest deployable unit in Kubernetes.

**Q3. What is a Deployment?**  
A Deployment manages Pod replicas, updates, and self-healing.

**Q4. What is a Service?**  
A Service gives a stable network name and access point for Pods.

**Q5. What is Minikube?**  
Minikube runs a local Kubernetes cluster for learning and development.

**Q6. What is kind?**  
kind runs Kubernetes clusters using Docker containers.

**Q7. Why should we use readiness probes?**  
They tell Kubernetes when a container is ready to receive traffic.

**Q8. What is the difference between Docker Compose and Kubernetes?**  
Compose is simpler for local development. Kubernetes is more powerful for orchestration, scaling, and production-style deployments.
