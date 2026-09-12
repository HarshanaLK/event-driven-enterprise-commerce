package com.acme.commerce.order.messaging;

import com.acme.commerce.events.*;
import com.acme.commerce.order.application.OrderSagaService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class OrderSagaConsumer {

    private final JsonMapper json;
    private final OrderSagaService saga;

    public OrderSagaConsumer(JsonMapper json, OrderSagaService saga) {
        this.json = json;
        this.saga = saga;
    }

    @KafkaListener(topics = EventTopics.INVENTORY_RESERVED, groupId = "order-service-inventory-reserved-v1")
    public void inventoryReserved(String payload) {
        saga.inventoryReserved(json.readValue(payload, InventoryReservedEvent.class));
    }

    @KafkaListener(topics = EventTopics.INVENTORY_REJECTED, groupId = "order-service-inventory-rejected-v1")
    public void inventoryRejected(String payload) {
        saga.inventoryRejected(json.readValue(payload, InventoryRejectedEvent.class));
    }

    @KafkaListener(topics = EventTopics.PAYMENT_COMPLETED, groupId = "order-service-payment-completed-v1")
    public void paymentCompleted(String payload) {
        saga.paymentCompleted(json.readValue(payload, PaymentCompletedEvent.class));
    }

    @KafkaListener(topics = EventTopics.PAYMENT_FAILED, groupId = "order-service-payment-failed-v1")
    public void paymentFailed(String payload) {
        saga.paymentFailed(json.readValue(payload, PaymentFailedEvent.class));
    }
}
