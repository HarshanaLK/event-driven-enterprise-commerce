# Event contracts

Topics are versioned. Breaking changes create a new topic/version instead of silently changing an existing contract.

| Topic | Producer | Consumer(s) |
|---|---|---|
| `commerce.order.placed.v1` | order-service | inventory-service |
| `commerce.inventory.reserved.v1` | inventory-service | order-service |
| `commerce.inventory.rejected.v1` | inventory-service | order-service |
| `commerce.payment.requested.v1` | order-service | payment-service |
| `commerce.payment.completed.v1` | payment-service | order-service |
| `commerce.payment.failed.v1` | payment-service | order-service |
| `commerce.inventory.release-requested.v1` | order-service | inventory-service |
| `commerce.order.confirmed.v1` | order-service | inventory-service, notification-service |
| `commerce.order.cancelled.v1` | order-service | notification-service |

Every event contains:

- `eventId`: idempotency identity for one event.
- `correlationId`: the checkout correlation, currently the order id.
- `occurredAt`: UTC timestamp.
- business identifiers required by the receiving bounded context.

Events deliberately avoid sharing database entities between services.
