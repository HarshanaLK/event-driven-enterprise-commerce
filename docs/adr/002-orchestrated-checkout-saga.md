# ADR 002: Orchestrate checkout in the order service

**Status:** Accepted

## Context

Checkout spans inventory and payment. A database transaction cannot safely cover multiple independently owned databases.

## Decision

The order service owns checkout state and reacts to result events. It issues the next command/event in the workflow.

Compensation is explicit: if payment fails after stock is reserved, the order service publishes `InventoryReleaseRequested`.

## Consequences

- The workflow is visible in one place.
- Individual services keep ownership of their own data.
- The order service knows the high-level checkout process, creating some orchestration coupling.
- Additional steps such as fraud and shipping can be inserted without distributed ACID transactions.
