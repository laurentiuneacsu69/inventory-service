#  Ecommerce Microservices

Backend application for managing an online store, built with Java Spring Boot using a microservices architecture.

---

##  Architecture

```
┌─────────────────────────────────────────────────────────┐
│                        CLIENT                           │
└────────────────────────┬────────────────────────────────┘
                         │ JWT Token (Keycloak)
          ┌──────────────┴──────────────┐
          │                             │
          ▼                             ▼
┌──────────────────┐         ┌──────────────────┐
│ Inventory Service│◄────────│  Order Service   │
│   (port 8081)    │  REST   │   (port 8082)    │
│   MongoDB        │(sync)   │   PostgreSQL     │
└──────────────────┘         └────────┬─────────┘
                                      │
                                      │ Kafka (async)
                                      ▼
                             ┌──────────────────┐
                             │ Inventory Service│
                             │ (decrease stock) │
                             └──────────────────┘

┌─────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE                       │
│  Keycloak (8080) │ Kafka (9092) │ Jaeger (16686)        │
│  Grafana (3000)  │ Prometheus (9090) │ MongoDB (27017)  │
│  PostgreSQL (5432)                                      │
└─────────────────────────────────────────────────────────┘
```

### Service Communication
- **Synchronous (REST)** — Order Service checks stock at Inventory Service before placing an order
- **Asynchronous (Kafka)** — after placing an order, Order Service publishes an `order-placed` event which Inventory Service consumes and decreases the stock

---

##  Technologies

| Technology | Usage |
|---|---|
| Java | Programming language |
| Spring Boot | Backend framework |
| Spring Security + OAuth2 | Authentication |
| Spring Kafka | Async messaging |
| MongoDB | Inventory Service database |
| PostgreSQL | Order Service database |
| Liquibase | Database migrations |
| Keycloak | Identity and Access Management |
| Kafka | Message broker |
| Jaeger | Distributed tracing |
| Prometheus | Metrics collection |
| Grafana | Metrics visualization |
| Docker | Containerization |
| Lombok | Boilerplate reduction |
---

##  Prerequisites

- **Docker Desktop** — for running containers
- **Java 21** — for running Spring Boot applications
- **Maven 3.9+** — for build
- **Postman / Bruno / Insomnia** — for API testing

---

##  How to Run

### 1. Clone the repository

```bash
git clone <repo-url>
cd ecommerce-microservices
```

### 2. Start infrastructure (Inventory Service)

```bash
cd inventory-service
docker compose up -d
```

This starts:
- MongoDB (port 27017)
- Kafka (port 9092)
- Keycloak (port 8080)
- Jaeger (port 16686)
- Prometheus (port 9090)
- Grafana (port 3000)

### 3. Start infrastructure (Order Service)

```bash
cd order-service
docker compose up -d
```

This starts:
- PostgreSQL (port 5432)

### 4. Start Inventory Service

```bash
cd inventory-service
mvn spring-boot:run
```

Application starts at `http://localhost:8081`

### 5. Start Order Service

```bash
cd order-service
mvn spring-boot:run
```

Application starts at `http://localhost:8082`

---

##  Keycloak Configuration

### 1. Access Keycloak
Go to `http://localhost:8080` with credentials:
- **Username:** `admin`
- **Password:** `admin`

### 2. Realm Configuration
- Create a new realm named `ecommerce`

### 3. Client Configuration
- Create a client with ID `ecommerce-client`
- Enable **Client authentication**
- Copy **Client Secret** from the Credentials tab

### 4. Roles Configuration
Create the following roles in **Realm roles**:
- `ADMIN` — full access (CRUD products, update order status)
- `USER` — can place orders and view their status

### 5. Users Configuration

**Administrator:**
- Username: `admin`
- Email: `admin@ecommerce.com`
- Password: `admin123`
- Role: `ADMIN`

**Regular user:**
- Username: `user`
- Email: `user@ecommerce.com`
- Password: `user123`
- Role: `USER`

---

##  Authentication

### Get JWT Token

```http
POST http://localhost:8080/realms/ecommerce/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=password
client_id=ecommerce-client
client_secret=<CLIENT_SECRET>
username=admin
password=admin123
```

The response contains an `access_token` which must be sent in the `Authorization` header with every request:

```
Authorization: Bearer <access_token>
```

> ⚠️ Token expires in **5 minutes**. Use `refresh_token` to get a new one.

---

##  Inventory Service (port 8081)

### Entities
- **Product** — inventory product with name, description, price and quantity

### Endpoints

| Method | Endpoint | Description | Required Role |
|---|---|---|---|
| `POST` | `/api/products` | Create a new product | ADMIN |
| `GET` | `/api/products` | List all products | Authenticated |
| `GET` | `/api/products/{id}` | Find product by ID | Authenticated |
| `PUT` | `/api/products/{id}` | Update a product | ADMIN |
| `DELETE` | `/api/products/{id}` | Delete a product | ADMIN |
| `GET` | `/api/products/{id}/stock-check?quantity={qty}` | Check stock | Internal |

### Example request — Create product

```json
POST /api/products
Authorization: Bearer <token>

{
  "name": "Laptop Stand",
  "description": "Ergonomic aluminum laptop stand",
  "price": 50.00,
  "quantity": 100
}
```

---

##  Order Service (port 8082)

### Entities
- **Order** — order with status, customer email and total
- **OrderItem** — order line with product, quantity and price

### Order Status Flow
```
PLACED → CONFIRMED → SHIPPED → DELIVERED
                  ↘ CANCELLED
```

### Endpoints

| Method | Endpoint | Description | Required Role |
|---|---|---|---|
| `POST` | `/api/orders` | Place a new order | Authenticated |
| `GET` | `/api/orders` | List all orders | Authenticated |
| `GET` | `/api/orders/{id}` | Find order by ID | Authenticated |
| `PATCH` | `/api/orders/{id}/status?status={status}` | Update order status | ADMIN |

### Example request — Place order

```json
POST /api/orders
Authorization: Bearer <token>

{
  "customerEmail": "user@ecommerce.com",
  "items": [
    {
      "productId": "prod-001",
      "productName": "Laptop Stand",
      "quantity": 2,
      "price": 50.00
    }
  ]
}
```

### Example request — Update status

```http
PATCH /api/orders/1/status?status=CONFIRMED
Authorization: Bearer <token>
```

---

##  Complete Order Flow

1. Client obtains JWT token from Keycloak
2. Client sends order to Order Service with token
3. Order Service **synchronously** checks stock at Inventory Service
4. If stock is sufficient, Order Service saves order in PostgreSQL with status `PLACED`
5. Order Service publishes `order-placed` event on Kafka
6. Inventory Service consumes the event and decreases stock in MongoDB
7. Admin can change order status via `PATCH /api/orders/{id}/status`

---

##  Observability

### Jaeger — Distributed Tracing
Access the UI at: `http://localhost:16686`
- Visualize request traces across services
- Identify latencies and performance issues

### Grafana — Monitoring
Access the UI at: `http://localhost:3000`
- **Username:** `admin`
- **Password:** `admin`
- Visualize JVM metrics, HTTP requests, memory usage

### Prometheus — Metrics Collection
Access the UI at: `http://localhost:9090`
- Inventory Service metrics: `http://localhost:8081/actuator/prometheus`
- Order Service metrics: `http://localhost:8082/actuator/prometheus`

---

##  Databases

### MongoDB (Inventory Service)
```
Host: localhost:27017
Database: inventory_db
Collection: products
```

### PostgreSQL (Order Service)
```
Host: localhost:5432
Database: order_db
Username: postgres
Password: postgres
Tables: orders, order_items
```

---

##  Running Tests

```bash
# Inventory Service
cd inventory-service
mvn test

# Order Service
cd order-service
mvn test
```

---

##  Project Structure

```
ecommerce-microservices/
├── inventory-service/
│   ├── src/main/java/com/ecommerce/inventory_service/
│   │   ├── config/          # Kafka, Security, DataInitializer configs
│   │   ├── controller/      # REST Controllers
│   │   ├── domain/          # MongoDB entities
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── event/           # Kafka Events
│   │   ├── mapper/          # DTO Mappers
│   │   ├── repository/      # MongoDB Repositories
│   │   └── service/         # Business Logic
│   ├── docker-compose.yml   # MongoDB, Kafka, Keycloak, Jaeger, Grafana
│   └── pom.xml
│
└── order-service/
    ├── src/main/java/com/ecommerce/order_service/
    │   ├── config/          # Kafka, Security, WebClient configs
    │   ├── controller/      # REST Controllers
    │   ├── domain/          # JPA entities
    │   ├── dto/             # Data Transfer Objects
    │   ├── event/           # Kafka Events
    │   ├── mapper/          # DTO Mappers
    │   ├── repository/      # JPA Repositories
    │   └── service/         # Business Logic
    ├── docker-compose.yml   # PostgreSQL
    └── pom.xml
```

---

## ️ Troubleshooting

**Port already in use:**
```bash
netstat -ano | findstr :<PORT>
taskkill /PID <PID> /F
```

**Keycloak token expired:**
Get a new token with the same POST request to Keycloak.

**Kafka not starting:**
```bash
docker compose down
docker compose up -d
```

**MongoDB / PostgreSQL connection issues:**
Check that Docker containers are running:
```bash
docker ps
```
