package com.acme.commerce.payment.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Component
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxPublisher(OutboxRepository repository, KafkaTemplate<String, String> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelayString = "${app.outbox.poll-interval-ms:500}")
    @Transactional
    public void publish() {
        for (OutboxEvent event : repository.lockNextBatch()) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getEventKey(), event.getPayload())
                        .get(5, TimeUnit.SECONDS);
                event.markPublished();
            } catch (Exception ex) {
                event.markFailed(ex.getMessage());
                log.warn("Outbox publish failed. id={} topic={}", event.getId(), event.getTopic(), ex);
            }
        }
    }
}
