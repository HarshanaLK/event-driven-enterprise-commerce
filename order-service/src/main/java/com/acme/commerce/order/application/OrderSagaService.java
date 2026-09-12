package com.acme.commerce.order.application;

import com.acme.commerce.events.*;
import com.acme.commerce.order.domain.CommerceOrder;
import com.acme.commerce.order.domain.OrderRepository;
import com.acme.commerce.order.domain.OrderStatus;
import com.acme.commerce.order.messaging.ProcessedMessage;
import com.acme.commerce.order.messaging.ProcessedMessageRepository;
import com.acme.commerce.order.outbox.OutboxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class OrderSagaService {

    private static final String CONSUMER = "order-checkout-saga-v1";

    private final OrderRepository orders;
    private final ProcessedMessageRepository processed;
    private final OutboxService outbox;

    public OrderSagaService(
            OrderRepository orders,
            ProcessedMessageRepository processed,
            OutboxService outbox
    ) {
        this.orders = orders;
        this.processed = processed;
        this.outbox = outbox;
    }

    @Transactional
    public void inventoryReserved(InventoryReservedEvent event) {
        if (alreadyProcessed(event.eventId())) return;

        CommerceOrder order = requireOrder(event.orderId());
        if (order.getStatus() == OrderStatus.PENDING) {
            order.markPaymentPending();

            PaymentRequestedEvent paymentRequested = new PaymentRequestedEvent(
                    UUID.randomUUID(),
                    event.correlationId(),
                    Instant.now(),
                    event.orderId(),
                    UUID.fromString(order.getCustomerId()),
                    order.getTotalAmount(),
                    order.getPaymentMethodToken()
            );
            outbox.enqueue(order.getId(), EventTopics.PAYMENT_REQUESTED, order.getId(), paymentRequested);
        }

        markProcessed(event.eventId());
    }

    @Transactional
    public void inventoryRejected(InventoryRejectedEvent event) {
        if (alreadyProcessed(event.eventId())) return;

        CommerceOrder order = requireOrder(event.orderId());
        if (order.getStatus() == OrderStatus.PENDING) {
            order.cancel(event.reason());
            publishCancelled(order, event.correlationId(), event.reason());
        }

        markProcessed(event.eventId());
    }

    @Transactional
    public void paymentCompleted(PaymentCompletedEvent event) {
        if (alreadyProcessed(event.eventId())) return;

        CommerceOrder order = requireOrder(event.orderId());
        if (order.getStatus() == OrderStatus.PAYMENT_PENDING) {
            order.confirm();

            OrderConfirmedEvent confirmed = new OrderConfirmedEvent(
                    UUID.randomUUID(),
                    event.correlationId(),
                    Instant.now(),
                    event.orderId(),
                    UUID.fromString(order.getCustomerId())
            );
            outbox.enqueue(order.getId(), EventTopics.ORDER_CONFIRMED, order.getId(), confirmed);
        }

        markProcessed(event.eventId());
    }

    @Transactional
    public void paymentFailed(PaymentFailedEvent event) {
        if (alreadyProcessed(event.eventId())) return;

        CommerceOrder order = requireOrder(event.orderId());
        if (order.getStatus() == OrderStatus.PAYMENT_PENDING) {
            order.cancel(event.reason());

            InventoryReleaseRequestedEvent release = new InventoryReleaseRequestedEvent(
                    UUID.randomUUID(),
                    event.correlationId(),
                    Instant.now(),
                    event.orderId(),
                    "Payment failed: " + event.reason()
            );
            outbox.enqueue(order.getId(), EventTopics.INVENTORY_RELEASE_REQUESTED, order.getId(), release);
            publishCancelled(order, event.correlationId(), event.reason());
        }

        markProcessed(event.eventId());
    }

    private void publishCancelled(CommerceOrder order, UUID correlationId, String reason) {
        OrderCancelledEvent cancelled = new OrderCancelledEvent(
                UUID.randomUUID(),
                correlationId,
                Instant.now(),
                UUID.fromString(order.getId()),
                UUID.fromString(order.getCustomerId()),
                reason
        );
        outbox.enqueue(order.getId(), EventTopics.ORDER_CANCELLED, order.getId(), cancelled);
    }

    private CommerceOrder requireOrder(UUID orderId) {
        return orders.findById(orderId.toString())
                .orElseThrow(() -> new IllegalStateException("Order not found for saga event: " + orderId));
    }

    private boolean alreadyProcessed(UUID eventId) {
        return processed.existsById(eventId.toString());
    }

    private void markProcessed(UUID eventId) {
        processed.save(new ProcessedMessage(eventId.toString(), CONSUMER));
    }
}
