# Amway Java Coding Assessment

Java 21 solution for the [specification](specs/001-java-assessment/spec.md), [implementation plan](specs/001-java-assessment/plan.md), and [task list](specs/001-java-assessment/tasks.md).

## Modules

- `calculator`: framework-free calculator library with `BigDecimal`, Command Pattern operations, six-decimal display, and LIFO undo/redo.
- `lucky-draw`: stateless Spring Boot REST service with configurable campaigns, weighted prizes, JWT roles, MySQL persistence, draw limits, and concurrency-safe inventory.

## Build and test

Requirements: JDK 21 and Maven 3.9+.

```bash
mvn clean verify
```

Pure unit tests run without a database. API, repository, transaction, and concurrency integration tests all run against an isolated MySQL 8.4 Testcontainer, so Docker must be running to execute them. When Docker is unavailable, those integration tests are reported as skipped rather than silently falling back to a different database engine.

## Run locally

The simplest option starts MySQL and the API together:

```bash
docker compose up --build
```

Or run `mvn -pl lucky-draw spring-boot:run` after starting MySQL. Configuration is externalized:

| Variable | Local default | Purpose |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/lucky_draw?...` | JDBC URL |
| `DB_USERNAME` | `lucky_draw` | Database user |
| `DB_PASSWORD` | `lucky_draw` | Database password |
| `DB_POOL_MIN_IDLE` | `2` | Minimum idle connections |
| `DB_POOL_MAX_SIZE` | `20` | Maximum connections |
| `DB_CONNECTION_TIMEOUT_MS` | `30000` | Pool connection timeout |
| `JWT_SECRET` | development-only value | HMAC secret, at least 32 characters |
| `JWT_TTL` | `PT1H` | Token lifetime |
| `PORT` | `8080` | HTTP port |

Production must override all credentials and secrets. The service never logs JWTs, passwords, or authorization headers.

## Calculator design

`DefaultCalculator` is the invoker. `AddCommand`, `SubtractCommand`, `MultiplyCommand`, `DivideCommand`, and `ClearCommand` implement the command contract. Two state stacks preserve undo and redo history. A failed command is evaluated before history changes, so division by zero and invalid input cannot corrupt the current value.

## Lucky Draw API

Swagger UI: <http://localhost:8080/swagger-ui.html>

All API routes require `Authorization: Bearer <JWT>`. The JWT subject is the user ID; the `roles` claim contains `ROLE_USER` or `ROLE_ADMIN`. Draw ownership is never accepted from request input.

| Method | Route | Role |
|---|---|---|
| `GET` | `/api/v1/campaigns/{campaignId}` | USER or ADMIN |
| `POST` | `/api/v1/campaigns/{campaignId}/draws` | USER or ADMIN |
| `POST` | `/api/v1/admin/campaigns` | ADMIN |
| `POST` | `/api/v1/admin/campaigns/{campaignId}/prizes` | ADMIN |
| `PUT` | `/api/v1/admin/campaigns/{campaignId}/prizes/{prizeId}` | ADMIN |

Campaign creation:

```bash
curl -X POST http://localhost:8080/api/v1/admin/campaigns \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"name":"Launch","maxDrawPerUser":3,"status":"ACTIVE","noPrizeProbability":70,"prizes":[{"name":"A","quantity":1,"probability":1},{"name":"B","quantity":10,"probability":9},{"name":"C","quantity":100,"probability":20}]}'
```

Campaign lookup:

```bash
curl http://localhost:8080/api/v1/campaigns/1 -H "Authorization: Bearer $TOKEN"
```

One draw and multiple draws use the same endpoint. `requestId` makes retries idempotent:

```bash
curl -X POST http://localhost:8080/api/v1/campaigns/1/draws \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"requestId":"order-123","count":1}'

curl -X POST http://localhost:8080/api/v1/campaigns/1/draws \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"requestId":"order-124","count":10}'
```

## Consistency and scaling

Each draw runs in its own database transaction. A pessimistic row lock serializes a user's campaign counter; a conditional update decrements inventory only while quantity is positive. If another request wins the selected unit first, selection is retried at most three times and then returns `NO_PRIZE`. Results, counters, and inventory commit or roll back together.

The application keeps no correctness-critical state in process memory, so multiple instances can run behind a load balancer. Java `synchronized` cannot protect separate processes. A Redis lock is unnecessary for this scope because the shared database already owns the transaction, row locks, unique constraints, and atomic inventory mutation; adding another lock system would create extra failure modes without strengthening the invariants.
