CREATE TABLE notification_log (
    id          CHAR(36)     NOT NULL,
    order_id    CHAR(36)     NOT NULL,
    customer_id CHAR(36)     NOT NULL,
    type        VARCHAR(32)  NOT NULL,
    channel     VARCHAR(32)  NOT NULL,
    message     VARCHAR(500) NOT NULL,
    created_at  TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_notification_order_created (order_id, created_at),
    KEY idx_notification_customer_created (customer_id, created_at)
) ENGINE=InnoDB;

CREATE TABLE processed_message (
    event_id      CHAR(36)     NOT NULL,
    consumer_name VARCHAR(120) NOT NULL,
    processed_at  TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (event_id),
    KEY idx_processed_consumer_time (consumer_name, processed_at)
) ENGINE=InnoDB;
