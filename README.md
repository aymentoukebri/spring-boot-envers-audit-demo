# Spring Boot Envers Audit Demo

Production-ready REST API built with **Spring Boot 4**, **Java 21**, and **PostgreSQL**.
Features full CRUD operations, Hibernate Envers audit trail, and event-driven revision notifications.

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 4.0.3 / Spring Framework 7 |
| Language | Java 21 |
| Build | Maven |
| Database | PostgreSQL 17 |
| ORM | Hibernate 7.1 / Spring Data JPA |
| Auditing | Hibernate Envers |
| Mapping | MapStruct 1.6.3 |
| Validation | Jakarta Validation (Bean Validation 3.1) |
| API Docs | OpenAPI 3 / springdoc-openapi 3.0.0 |
| Monitoring | Spring Actuator |
| Containerization | Docker / Docker Compose |

## Architecture

```
com.example.productapi
├── config/              # Spring configuration (Auditing, OpenAPI, context holder)
├── controller/          # REST controllers
├── dto/                 # Request/response records
├── entity/              # JPA entities + audit base class
├── event/               # Domain events (RevisionEvent)
├── exception/           # Global exception handler + custom exceptions
├── listener/            # Envers revision listener + Spring event listener
├── mapper/              # MapStruct interfaces
├── repository/          # Spring Data JPA repositories
└── service/             # Business logic
```

### Data Flow

```
HTTP Request
    │
    ▼
ProductController          ── validates @RequestBody
    │
    ▼
ProductService             ── business logic + @Transactional
    │
    ▼
ProductRepository          ── Spring Data JPA
    │
    ▼
Hibernate                  ── persists to 'products' table
    │
    ▼
Envers                     ── automatically writes to 'products_aud' + 'revinfo'
    │
    ▼
CustomRevisionListener     ── publishes RevisionEvent
    │
    ▼
RevisionEventListener      ── logs audit notification
```

## Prerequisites

- Java 21+
- PostgreSQL 17 (or Docker)
- Maven 3.9+

## Getting Started

### Option 1 — Docker Compose (recommended)

```bash
# Start PostgreSQL + application
docker compose up --build
```

### Option 2 — Local Development

1. **Start PostgreSQL** and create the database:

```sql
CREATE DATABASE productdb;
```

2. **Run the application:**

```bash
./mvnw spring-boot:run
```

The app starts on **http://localhost:8089** by default.

### Environment Variables

All settings have defaults and can be overridden via environment variables:

| Variable | Default | Description |
|---|---|---|
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `productdb` | Database name |
| `DB_USERNAME` | `root` | Database username |
| `DB_PASSWORD` | `rootforever` | Database password |
| `DB_POOL_SIZE` | `10` | HikariCP max pool size |
| `SERVER_PORT` | `8089` | Application port |
| `JPA_DDL_AUTO` | `update` | Hibernate DDL strategy |
| `LOG_LEVEL` | `INFO` | Application log level |

## API Endpoints

### Product CRUD

| Method | Endpoint | Description | Status |
|---|---|---|---|
| `POST` | `/api/v1/products` | Create a product | `201` |
| `GET` | `/api/v1/products` | List all products | `200` |
| `GET` | `/api/v1/products/{id}` | Get product by ID | `200` |
| `PUT` | `/api/v1/products/{id}` | Update a product | `200` |
| `DELETE` | `/api/v1/products/{id}` | Delete a product | `204` |

### Audit / Revisions

| Method | Endpoint | Description | Status |
|---|---|---|---|
| `GET` | `/api/v1/products/{id}/revisions?page=0&size=20` | Paginated revision history | `200` |

### Infrastructure

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/actuator/health` | Health check |
| `GET` | `/actuator/info` | App info |
| `GET` | `/actuator/metrics` | Metrics |
| `GET` | `/swagger-ui.html` | Swagger UI |
| `GET` | `/api-docs` | OpenAPI JSON spec |

## Usage Examples

### Create a Product

```bash
curl -X POST http://localhost:8089/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MacBook Pro 16\"",
    "description": "Apple MacBook Pro with M3 Max chip",
    "price": 3499.99
  }'
```

**Response** `201 Created`:
```json
{
  "id": 1,
  "name": "MacBook Pro 16\"",
  "description": "Apple MacBook Pro with M3 Max chip",
  "price": 3499.99,
  "createdAt": "2026-02-25T10:30:00Z",
  "updatedAt": "2026-02-25T10:30:00Z",
  "createdBy": "system",
  "updatedBy": "system"
}
```

### Update a Product

```bash
curl -X PUT http://localhost:8089/api/v1/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MacBook Pro 14\"",
    "description": "Apple MacBook Pro with M3 Pro chip",
    "price": 1999.99
  }'
```

### Get Revision History

```bash
curl http://localhost:8089/api/v1/products/1/revisions?page=0&size=10
```

**Response** `200 OK`:
```json
{
  "content": [
    {
      "revisionNumber": 2,
      "revisionType": "MOD",
      "revisionTimestamp": "2026-02-25T10:35:00Z",
      "product": {
        "id": 1,
        "name": "MacBook Pro 14\"",
        "price": 1999.99,
        "createdBy": "system",
        "updatedBy": "system"
      }
    },
    {
      "revisionNumber": 1,
      "revisionType": "ADD",
      "revisionTimestamp": "2026-02-25T10:30:00Z",
      "product": {
        "id": 1,
        "name": "MacBook Pro 16\"",
        "price": 3499.99,
        "createdBy": "system",
        "updatedBy": "system"
      }
    }
  ],
  "totalElements": 2,
  "totalPages": 1,
  "number": 0
}
```

### Validation Error

```bash
curl -X POST http://localhost:8089/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "", "price": -5}'
```

**Response** `400 Bad Request` (RFC 7807 ProblemDetail):
```json
{
  "type": "https://api.example.com/errors/validation",
  "title": "Validation Error",
  "status": 400,
  "detail": "Validation failed",
  "errors": {
    "name": "Name is required",
    "price": "Price must be greater than zero"
  }
}
```

## Auditing System

### How It Works

Every `@Audited` entity is automatically tracked by Hibernate Envers:

1. **On any INSERT/UPDATE/DELETE**, Envers writes a snapshot to the `products_aud` table
2. The `CustomRevisionListener` intercepts each revision and publishes a Spring `RevisionEvent`
3. The `RevisionEventListener` logs an alert:

```
[AUDIT NOTIFICATION] Entity 'Product' with id=1 was CREATED | revision=1 | timestamp=2026-02-25T10:30:00Z
[AUDIT NOTIFICATION] Entity 'Product' with id=1 was MODIFIED | revision=2 | timestamp=2026-02-25T10:35:00Z
[AUDIT NOTIFICATION] Entity 'Product' with id=1 was DELETED | revision=3 | timestamp=2026-02-25T10:40:00Z
```

### Extending Notifications

The `RevisionEventListener` is the extension point. Add Slack, email, or webhook notifications by injecting the appropriate client:

```java
@EventListener
public void onRevision(RevisionEvent event) {
    // Already logs automatically

    // Add any of these:
    slackClient.send("#audit-alerts", "...");
    emailService.send("admin@example.com", "...");
    webhookClient.post("https://hooks.example.com/audit", event);
}
```

### Adding Audit to a New Entity

1. Extend `AbstractAuditableEntity`
2. Add `@Audited`

```java
@Entity
@Audited
public class Order extends AbstractAuditableEntity {
    // your fields — auditing + notifications work automatically
}
```

No other changes needed. The revision listener is generic and works for all audited entities.

## Database Schema

```
┌──────────────┐     ┌──────────────────┐     ┌──────────────┐     ┌──────────────┐
│   products   │     │  products_aud    │     │   revinfo    │     │  revchanges  │
├──────────────┤     ├──────────────────┤     ├──────────────┤     ├──────────────┤
│ id        PK │     │ id          PK   │────▶│ rev       PK │◀────│ rev       FK │
│ name         │     │ rev      PK, FK  │     │ revtstmp     │     │ entityname   │
│ description  │     │ revtype          │     └──────────────┘     └──────────────┘
│ price        │     │ name             │
│ created_at   │     │ name_mod         │
│ updated_at   │     │ description      │
│ created_by   │     │ description_mod  │
│ updated_by   │     │ price            │
└──────────────┘     │ price_mod        │
                     │ ...              │
                     └──────────────────┘
```

## Postman Collection

Import the files from `postman/` into Postman:

- `Product_API.postman_collection.json` — 12 requests organized in CRUD, Audit, and Health folders
- `Product_API.postman_environment.json` — environment variables (`host`, `port`)

## Project Structure

```
spring-boot-envers-audit-demo/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── postman/
│   ├── Product_API.postman_collection.json
│   └── Product_API.postman_environment.json
└── src/
    ├── main/
    │   ├── java/com/example/productapi/
    │   │   ├── ProductApiApplication.java
    │   │   ├── config/
    │   │   │   ├── AuditingConfig.java
    │   │   │   ├── OpenApiConfig.java
    │   │   │   └── SpringContextHolder.java
    │   │   ├── controller/
    │   │   │   └── ProductController.java
    │   │   ├── dto/
    │   │   │   ├── ProductRequest.java
    │   │   │   ├── ProductResponse.java
    │   │   │   └── ProductRevisionResponse.java
    │   │   ├── entity/
    │   │   │   ├── AbstractAuditableEntity.java
    │   │   │   ├── CustomRevisionEntity.java
    │   │   │   └── Product.java
    │   │   ├── event/
    │   │   │   └── RevisionEvent.java
    │   │   ├── exception/
    │   │   │   ├── GlobalExceptionHandler.java
    │   │   │   └── ResourceNotFoundException.java
    │   │   ├── listener/
    │   │   │   ├── CustomRevisionListener.java
    │   │   │   └── RevisionEventListener.java
    │   │   ├── mapper/
    │   │   │   └── ProductMapper.java
    │   │   ├── repository/
    │   │   │   └── ProductRepository.java
    │   │   └── service/
    │   │       ├── ProductRevisionService.java
    │   │       └── ProductService.java
    │   └── resources/
    │       ├── application.yml
    │       └── db/migration/
    │           └── V1__create_products_table.sql
    └── test/
        ├── java/.../ProductApiApplicationTests.java
        └── resources/application-test.yml
```
