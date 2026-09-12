CREATE TABLE customer_order (
    id                  CHAR(36)      NOT NULL,
    customer_id         CHAR(36)      NOT NULL,
    idempotency_key     VARCHAR(120)  NOT NULL,
    payment_method_token VARCHAR(160) NOT NULL,
    status              VARCHAR(32)   NOT NULL,
    total_amount        DECIMAL(19,2) NOT NULL,
    cancellation_reason VARCHAR(500)  NULL,
    created_at          TIMESTAMP(6)  NOT NULL,
    updated_at          TIMESTAMP(6)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_customer_order_idempotency (idempotency_key),
    KEY idx_customer_order_customer_created (customer_id, created_at),
    KEY idx_customer_order_status_created (status, created_at)
) ENGINE=InnoDB;

CREATE TABLE order_item (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    order_id     CHAR(36)      NOT NULL,
    sku          VARCHAR(80)   NOT NULL,
    product_name VARCHAR(180)  NOT NULL,
    quantity     INT           NOT NULL,
    unit_price   DECIMAL(19,2) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_order_item_order (order_id),
    KEY idx_order_item_sku (sku),
    CONSTRAINT fk_order_item_order
        FOREIGN KEY (order_id) REFERENCES customer_order (id)
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
