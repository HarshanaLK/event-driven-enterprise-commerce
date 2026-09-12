package com.acme.commerce.inventory.messaging;

import com.acme.commerce.events.*;
import com.acme.commerce.inventory.application.InventoryEventService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class InventoryEventConsumer {

    private final JsonMapper json;
    private final InventoryEventService service;

    public InventoryEventConsumer(JsonMapper json, InventoryEventService service) {
        this.json = json;
        this.service = service;
    }

    @KafkaListener(topics = EventTopics.ORDER_PLACED, groupId = "inventory-service-order-placed-v1")
    public void orderPlaced(String payload) {
        service.reserve(json.readValue(payload, OrderPlacedEvent.class));
    }

    @KafkaListener(topics = EventTopics.INVENTORY_RELEASE_REQUESTED, groupId = "inventory-service-release-v1")
    public void releaseRequested(String payload) {
        service.release(json.readValue(payload, InventoryReleaseRequestedEvent.class));
    }

    @KafkaListener(topics = EventTopics.ORDER_CONFIRMED, groupId = "inventory-service-confirmed-v1")
    public void orderConfirmed(String payload) {
        service.commit(json.readValue(payload, OrderConfirmedEvent.class));
    }
}
