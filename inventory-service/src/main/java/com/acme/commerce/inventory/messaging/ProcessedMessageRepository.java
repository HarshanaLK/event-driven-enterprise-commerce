package com.acme.commerce.inventory.messaging;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, String> {
}
