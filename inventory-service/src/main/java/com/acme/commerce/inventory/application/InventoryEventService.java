package com.acme.commerce.inventory.application;

import com.acme.commerce.events.*;
import com.acme.commerce.inventory.domain.*;
import com.acme.commerce.inventory.messaging.ProcessedMessage;
import com.acme.commerce.inventory.messaging.ProcessedMessageRepository;
import com.acme.commerce.inventory.outbox.OutboxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class InventoryEventService {

    private static final String CONSUMER = "inventory-service-v1";

    private final StockItemRepository stocks;
    private final InventoryReservationRepository reservations;
    private final ProcessedMessageRepository processed;
    private final OutboxService outbox;

    public InventoryEventService(
            StockItemRepository stocks,
            InventoryReservationRepository reservations,
            ProcessedMessageRepository processed,
            OutboxService outbox
    ) {
        this.stocks = stocks;
        this.reservations = reservations;
        this.processed = processed;
        this.outbox = outbox;
    }

    @Transactional
    public void reserve(OrderPlacedEvent event) {
        if (alreadyProcessed(event.eventId())) return;

        Optional<InventoryReservation> existing = reservations.findByOrderId(event.orderId().toString());
        if (existing.isPresent()) {
            markProcessed(event.eventId());
            return;
        }

        Map<String, RequestedStock> requested = aggregate(event.items());
        List<String> skus = requested.keySet().stream().sorted().toList();

        Map<String, StockItem> locked = new LinkedHashMap<>();
        for (String sku : skus) {
            StockItem stock = stocks.findForUpdate(sku).orElse(null);
            if (stock == null) {
                reject(event, "Unknown SKU: " + sku);
                markProcessed(event.eventId());
                return;
            }
            locked.put(sku, stock);
        }

        for (String sku : skus) {
            RequestedStock line = requested.get(sku);
            StockItem stock = locked.get(sku);
            if (!stock.canReserve(line.quantity())) {
                reject(event, "Insufficient stock for " + sku + "; requested=" +
                        line.quantity() + ", available=" + stock.getAvailableQuantity());
                markProcessed(event.eventId());
                return;
            }
        }

        InventoryReservation reservation = new InventoryReservation(event.orderId().toString());
        for (String sku : skus) {
            int quantity = requested.get(sku).quantity();
            locked.get(sku).reserve(quantity);
            reservation.addItem(sku, quantity);
        }
        reservations.save(reservation);

        InventoryReservedEvent reserved = new InventoryReservedEvent(
                UUID.randomUUID(),
                event.correlationId(),
                Instant.now(),
                event.orderId()
        );
        outbox.enqueue(event.orderId().toString(), EventTopics.INVENTORY_RESERVED,
                event.orderId().toString(), reserved);

        markProcessed(event.eventId());
    }

    @Transactional
    public void release(InventoryReleaseRequestedEvent event) {
        if (alreadyProcessed(event.eventId())) return;

        InventoryReservation reservation = reservations.findByOrderId(event.orderId().toString())
                .orElseThrow(() -> new IllegalStateException(
                        "Reservation not found for order " + event.orderId()
                ));

        if (reservation.getStatus() == ReservationStatus.RESERVED) {
            List<ReservationItem> items = reservation.getItems().stream()
                    .sorted(Comparator.comparing(ReservationItem::getSku))
                    .toList();

            for (ReservationItem item : items) {
                StockItem stock = stocks.findForUpdate(item.getSku())
                        .orElseThrow(() -> new IllegalStateException("Stock row missing for " + item.getSku()));
                stock.release(item.getQuantity());
            }
            reservation.release();
        }

        markProcessed(event.eventId());
    }

    @Transactional
    public void commit(OrderConfirmedEvent event) {
        if (alreadyProcessed(event.eventId())) return;

        InventoryReservation reservation = reservations.findByOrderId(event.orderId().toString())
                .orElseThrow(() -> new IllegalStateException(
                        "Reservation not found for confirmed order " + event.orderId()
                ));

        if (reservation.getStatus() == ReservationStatus.RESERVED) {
            List<ReservationItem> items = reservation.getItems().stream()
                    .sorted(Comparator.comparing(ReservationItem::getSku))
                    .toList();

            for (ReservationItem item : items) {
                StockItem stock = stocks.findForUpdate(item.getSku())
                        .orElseThrow(() -> new IllegalStateException("Stock row missing for " + item.getSku()));
                stock.commit(item.getQuantity());
            }
            reservation.commit();
        }

        markProcessed(event.eventId());
    }

    private Map<String, RequestedStock> aggregate(List<OrderLinePayload> lines) {
        return lines.stream()
                .map(line -> new RequestedStock(line.sku(), line.quantity()))
                .collect(Collectors.toMap(
                        RequestedStock::sku,
                        Function.identity(),
                        (left, right) -> new RequestedStock(left.sku(), left.quantity() + right.quantity()),
                        LinkedHashMap::new
                ));
    }

    private void reject(OrderPlacedEvent source, String reason) {
        InventoryRejectedEvent rejected = new InventoryRejectedEvent(
                UUID.randomUUID(),
                source.correlationId(),
                Instant.now(),
                source.orderId(),
                reason
        );
        outbox.enqueue(source.orderId().toString(), EventTopics.INVENTORY_REJECTED,
                source.orderId().toString(), rejected);
    }

    private boolean alreadyProcessed(UUID eventId) {
        return processed.existsById(eventId.toString());
    }

    private void markProcessed(UUID eventId) {
        processed.save(new ProcessedMessage(eventId.toString(), CONSUMER));
    }

    private record RequestedStock(String sku, int quantity) {
    }
}
