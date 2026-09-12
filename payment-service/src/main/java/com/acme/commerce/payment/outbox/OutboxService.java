package com.acme.commerce.payment.outbox;

import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

@Service
public class OutboxService {

    private final OutboxRepository repository;
    private final JsonMapper jsonMapper;

    public OutboxService(OutboxRepository repository, JsonMapper jsonMapper) {
        this.repository = repository;
        this.jsonMapper = jsonMapper;
    }

    public void enqueue(String aggregateId, String topic, String eventKey, Object event) {
        String payload = jsonMapper.writeValueAsString(event);
        repository.save(new OutboxEvent(
                aggregateId,
                event.getClass().getSimpleName(),
                topic,
                eventKey,
                payload
        ));
    }
}
