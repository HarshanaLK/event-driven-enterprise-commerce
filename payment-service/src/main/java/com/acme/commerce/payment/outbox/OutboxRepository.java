package com.acme.commerce.payment.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, String> {

    @Query(value = """
            SELECT *
            FROM outbox_event
            WHERE published_at IS NULL
              AND next_attempt_at <= UTC_TIMESTAMP(6)
            ORDER BY created_at
            LIMIT 50
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<OutboxEvent> lockNextBatch();
}
