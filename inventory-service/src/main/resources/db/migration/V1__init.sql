CREATE TABLE stock_item (
    sku                VARCHAR(80)  NOT NULL,
    product_name       VARCHAR(180) NOT NULL,
    available_quantity INT          NOT NULL,
    reserved_quantity  INT          NOT NULL,
    updated_at         TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (sku),
    CONSTRAINT chk_stock_available CHECK (available_quantity >= 0),
    CONSTRAINT chk_stock_reserved CHECK (reserved_quantity >= 0)
) ENGINE=InnoDB;

CREATE TABLE inventory_reservation (
    id         CHAR(36)     NOT NULL,
    order_id   CHAR(36)     NOT NULL,
    status     VARCHAR(24)  NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_inventory_reservation_order (order_id),
    KEY idx_inventory_reservation_status (status, updated_at)
) ENGINE=InnoDB;

CREATE TABLE inventory_reservation_item (
    id             BIGINT      NOT NULL AUTO_INCREMENT,
    reservation_id CHAR(36)    NOT NULL,
    sku            VARCHAR(80) NOT NULL,
    quantity       INT         NOT NULL,
    PRIMARY KEY (id),
    KEY idx_reservation_item_reservation (reservation_id),
    KEY idx_reservation_item_sku (sku),
    CONSTRAINT fk_reservation_item_reservation
        FOREIGN KEY (reservation_id) REFERENCES inventory_reservation (id)
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

INSERT INTO stock_item (sku, product_name, available_quantity, reserved_quantity, updated_at)
VALUES
    ('LAPTOP-PRO-14', 'Pro Laptop 14', 20, 0, UTC_TIMESTAMP(6)),
    ('MECH-KEY-01', 'Mechanical Keyboard', 50, 0, UTC_TIMESTAMP(6)),
    ('MOUSE-WL-02', 'Wireless Mouse', 80, 0, UTC_TIMESTAMP(6));
