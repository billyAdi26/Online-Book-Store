# Online Bookstore — Microservices

A demo microservices project built with Java 25 and Spring Boot 4, demonstrating core microservices concepts including service decomposition, REST communication, and the Saga orchestration pattern.

---

## Tech Stack

| | |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.0.6 |
| Database | PostgreSQL 18 (one per service) |
| ORM | Spring Data JPA / Hibernate |
| Mapping | MapStruct |
| HTTP Client | Spring RestClient |
| Build Tool | Gradle 9.4.1 |
| Containerization | Docker + Docker Compose |

---

## Services

### Catalog Service — port `8081`
Manages book data. Standalone service, no dependencies on other services.

### Inventory Service — port `8082`
Manages stock levels. Exposes endpoints for stock check, reservation, and release — consumed by Order Service during the Saga flow.

### Order Service — port `8083`
Manages cart and orders. Orchestrates the Saga pattern when a customer places an order.

---

## Architecture

```
Browser
  │
  ├── Browse books      → Catalog Service
  ├── Manage cart       → Order Service
  └── Place order       → Order Service → Saga Orchestrator
                                              │
                                    ┌─────────┴─────────┐
                                    │                   │
                              Check stock         Reserve stock
                              (Inventory)         (Inventory)
                                    │
                              Success → Confirm Order
                              Failure → Release stock → Cancel Order
```

---

## Saga Pattern

The Saga orchestrator lives inside Order Service. When a customer places an order:

1. Validate cart items
2. Save order (`PENDING` / `STARTED`)
3. Check stock for all items — fail → compensate
4. Reserve stock for all items — fail → release reserved → compensate
5. Confirm order (`CONFIRMED` / `COMPLETED`)
6. Clear cart

On any failure, compensating actions roll back reserved stock and mark the order as `CANCELLED`.

---

## Databases

Each service owns its database — no shared DB, no cross-DB joins.
Cross-service references (e.g. `bookId` in inventory and order) are logical only — enforced at the application level, not via DB foreign keys.
