package com.acme.commerce.payment.messaging;

import com.acme.commerce.events.EventTopics;
import com.acme.commerce.events.PaymentRequestedEvent;
import com.acme.commerce.payment.application.PaymentEventService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class PaymentEventConsumer {

    private final JsonMapper json;
    private final PaymentEventService service;

    public PaymentEventConsumer(JsonMapper json, PaymentEventService service) {
        this.json = json;
        this.service = service;
    }

    @KafkaListener(topics = EventTopics.PAYMENT_REQUESTED, groupId = "payment-service-request-v1")
    public void paymentRequested(String payload) {
        service.requestPayment(json.readValue(payload, PaymentRequestedEvent.class));
    }
}
