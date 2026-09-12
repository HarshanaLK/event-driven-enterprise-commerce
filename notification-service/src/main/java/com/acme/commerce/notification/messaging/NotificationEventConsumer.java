package com.acme.commerce.notification.messaging;

import com.acme.commerce.events.*;
import com.acme.commerce.notification.application.NotificationEventService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class NotificationEventConsumer {

    private final JsonMapper json;
    private final NotificationEventService service;

    public NotificationEventConsumer(JsonMapper json, NotificationEventService service) {
        this.json = json;
        this.service = service;
    }

    @KafkaListener(topics = EventTopics.ORDER_CONFIRMED, groupId = "notification-service-confirmed-v1")
    public void orderConfirmed(String payload) {
        service.orderConfirmed(json.readValue(payload, OrderConfirmedEvent.class));
    }

    @KafkaListener(topics = EventTopics.ORDER_CANCELLED, groupId = "notification-service-cancelled-v1")
    public void orderCancelled(String payload) {
        service.orderCancelled(json.readValue(payload, OrderCancelledEvent.class));
    }
}
