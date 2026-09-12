package com.acme.commerce.events;

public final class EventTopics {
    public static final String ORDER_PLACED = "commerce.order.placed.v1";
    public static final String INVENTORY_RESERVED = "commerce.inventory.reserved.v1";
    public static final String INVENTORY_REJECTED = "commerce.inventory.rejected.v1";
    public static final String PAYMENT_REQUESTED = "commerce.payment.requested.v1";
    public static final String PAYMENT_COMPLETED = "commerce.payment.completed.v1";
    public static final String PAYMENT_FAILED = "commerce.payment.failed.v1";
    public static final String INVENTORY_RELEASE_REQUESTED = "commerce.inventory.release-requested.v1";
    public static final String ORDER_CONFIRMED = "commerce.order.confirmed.v1";
    public static final String ORDER_CANCELLED = "commerce.order.cancelled.v1";

    private EventTopics() {
    }
}
