# Architecture

## Checkout saga

```mermaid
sequenceDiagram
    participant Client
    participant Order as Order Service
    participant Kafka
    participant Inventory as Inventory Service
    participant Payment as Payment Service
    participant Notification as Notification Service

    Client->>Order: POST /api/orders
    Order->>Order: order + outbox in one MySQL transaction
    Order-->>Client: 202 PENDING
    Order->>Kafka: OrderPlaced

    Kafka->>Inventory: OrderPlaced
    Inventory->>Inventory: lock stock rows + reserve + outbox
    Inventory->>Kafka: InventoryReserved / InventoryRejected

    Kafka->>Order: InventoryReserved
    Order->>Order: PAYMENT_PENDING + outbox
    Order->>Kafka: PaymentRequested

    Kafka->>Payment: PaymentRequested
    Payment->>Payment: charge + payment + outbox
    Payment->>Kafka: PaymentCompleted / PaymentFailed

    alt payment completed
        Kafka->>Order: PaymentCompleted
        Order->>Order: CONFIRMED + outbox
        Order->>Kafka: OrderConfirmed
        Kafka->>Inventory: OrderConfirmed
        Inventory->>Inventory: commit reservation
        Kafka->>Notification: OrderConfirmed
    else payment failed
        Kafka->>Order: PaymentFailed
        Order->>Order: CANCELLED + two outbox records
        Order->>Kafka: InventoryReleaseRequested
        Order->>Kafka: OrderCancelled
        Kafka->>Inventory: InventoryReleaseRequested
        Inventory->>Inventory: return reserved stock
        Kafka->>Notification: OrderCancelled
    end
```

## Service ownership

Each service owns its schema. There are no cross-service joins.

| Service | Database | Owns |
|---|---|---|
| order-service | MySQL `orders` | orders, order items, checkout state |
| inventory-service | MySQL `inventory` | stock and reservations |
| payment-service | MySQL `payments` | payment attempts/results |
| notification-service | MySQL `notifications` | delivery audit log |

## Delivery guarantees

Kafka delivery is treated as **at least once**.

Consumers persist `event_id` in `processed_message` in the same local transaction as the business change. A duplicate delivery therefore becomes a no-op.

Producers use the transactional outbox pattern. Business data and the event payload are committed to MySQL together. A scheduled publisher later sends unpublished rows to Kafka.

A crash can happen after Kafka accepts a record but before `published_at` is committed. That can publish the same event again. Consumer idempotency is what makes this safe.

## Inventory concurrency

Reservation locks stock rows using pessimistic database locks. SKUs are sorted before locking to keep lock order deterministic and reduce deadlock risk. The application validates the complete basket before changing any stock row, so a rejected basket does not partially reserve inventory.

## What would change in production

- Kafka: 3+ brokers, replication factor 3, TLS/SASL, ACLs and explicit topic retention.
- MySQL: managed HA deployment, backups, PITR, read replicas where appropriate.
- Payment: replace the demo gateway with a PCI-compliant provider token integration.
- Secrets: Vault/cloud secret manager rather than environment values in local Compose.
- Observability: OpenTelemetry traces, Prometheus metrics and structured logs.
- Security: OAuth2/OIDC, gateway-level authorization, service identities and mTLS.
- Outbox: Debezium CDC is an alternative when throughput makes polling undesirable.
- Contracts: Schema Registry with compatibility rules (Avro/Protobuf/JSON Schema).
