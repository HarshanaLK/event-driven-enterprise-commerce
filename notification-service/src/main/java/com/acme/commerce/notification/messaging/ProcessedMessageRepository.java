package com.acme.commerce.notification.messaging;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, String> {
}
