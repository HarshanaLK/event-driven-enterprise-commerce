# Event-Driven Enterprise Commerce

A backend reference project for a commerce checkout built around **Java 21, Spring Boot, Kafka and MySQL**.

This is intentionally not a CRUD-only store. The interesting work is in consistency, concurrency and failure handling across service boundaries.

## What is implemented

- Four independently deployable Spring Boot services.
- Database-per-service using four MySQL schemas/containers.
- Apache Kafka event backbone.
- Orchestrated checkout Saga.
- Transactional Outbox pattern.
- At-least-once consumer idempotency.
- Inventory row locking to prevent overselling.
- Payment-failure compensation that releases inventory.
- Idempotent order creation through `Idempotency-Key`.
- Dead-letter topics after consumer retries.
- Flyway database migrations.
- Health/metrics endpoints through Actuator.
- Unit tests and GitHub Actions CI.
- Docker Compose local environment.

## Run locally on Windows without Docker

For the corrected local setup using **MySQL 8.4** and **Kafka 4.3.1**, see [`LOCAL_RUN_WINDOWS.md`](LOCAL_RUN_WINDOWS.md).


## Frontend (Next.js)

A complete `frontend/` application is included. It uses Next.js, TypeScript and Tailwind CSS and proxies requests to the four Spring Boot services, so no CORS changes are required.

Start it with:

```powershell
.\scripts\start-frontend-local.ps1
```

Then open `http://localhost:3000`. See `LOCAL_RUN_WINDOWS.md` for the complete no-Docker startup sequence.
