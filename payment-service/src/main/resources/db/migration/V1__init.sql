CREATE TABLE payment (
    id                 CHAR(36)      NOT NULL,
    order_id           CHAR(36)      NOT NULL,
    customer_id        CHAR(36)      NOT NULL,
    amount             DECIMAL(19,2) NOT NULL,
    status             VARCHAR(24)   NOT NULL,
    provider_reference VARCHAR(120)  NULL,
    failure_reason     VARCHAR(500)  NULL,
    created_at         TIMESTAMP(6)  NOT NULL,
    updated_at         TIMESTAMP(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_payment_order (order_id),
    KEY idx_payment_customer_created (customer_id, created_at),
    KEY idx_payment_status_created (status, created_at)
) ENGINE=InnoDB;

CREATE TABLE outbox_event (
    id              CHAR(36)      NOT NULL,
    aggregate_id    VARCHAR(64)   NOT NULL,
    event_type      VARCHAR(120)  NOT NULL,
    topic_name      VARCHAR(180)  NOT NULL,
    event_key       VARCHAR(120)  NOT NULL,
    payload         LONGTEXT      NOT NULL,
    created_at      TIMESTAMP(6)  NOT NULL,
    published_at    TIMESTAMP(6)  NULL,
    next_attempt_at TIMESTAMP(6)  NOT NULL,
    attempts        INT           NOT NULL DEFAULT 0,
    last_error      VARCHAR(1000) NULL,
    PRIMARY KEY (id),
    KEY idx_outbox_unpublished (published_at, next_attempt_at, created_at)
) ENGINE=InnoDB;

CREATE TABLE processed_message (
    event_id      CHAR(36)     NOT NULL,
    consumer_name VARCHAR(120) NOT NULL,
    processed_at  TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (event_id),
    KEY idx_processed_consumer_time (consumer_name, processed_at)
) ENGINE=InnoDB;
