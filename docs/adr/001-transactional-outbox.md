# ADR 001: Use a transactional outbox

**Status:** Accepted

## Context

Writing an order to MySQL and then publishing to Kafka creates a dual-write problem. Either operation can succeed while the other fails.

## Decision

Services that publish business events first write the event to `outbox_event` in the same MySQL transaction as their aggregate change. A separate publisher sends pending rows to Kafka.

The publisher uses `FOR UPDATE SKIP LOCKED`, allowing multiple service instances to poll without selecting the same pending rows at the same time.

## Consequences

- Business state and intent-to-publish are atomic.
- Kafka remains at-least-once, not exactly-once across MySQL and Kafka.
- Consumers must be idempotent.
- Old published outbox rows need an archival/retention job in a long-running production system.
