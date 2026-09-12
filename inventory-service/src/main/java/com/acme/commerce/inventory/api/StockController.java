package com.acme.commerce.inventory.api;

import com.acme.commerce.inventory.application.InventoryAdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class StockController {

    private final InventoryAdminService service;

    public StockController(InventoryAdminService service) {
        this.service = service;
    }

    @GetMapping
    public List<StockResponse> list() {
        return service.list();
    }

    @GetMapping("/{sku}")
    public StockResponse get(@PathVariable String sku) {
        return service.get(sku);
    }

    @PutMapping("/{sku}")
    public StockResponse createIfMissing(
            @PathVariable String sku,
            @Valid @RequestBody CreateStockRequest request
    ) {
        return service.createIfMissing(sku, request);
    }

    @PostMapping("/{sku}/adjust")
    public StockResponse adjust(
            @PathVariable String sku,
            @Valid @RequestBody AdjustStockRequest request
    ) {
        return service.adjust(sku, request.delta());
    }

    public record CreateStockRequest(
            @NotBlank @Size(max = 180) String productName,
            @NotNull Integer availableQuantity
    ) {
    }

    public record AdjustStockRequest(@NotNull Integer delta) {
    }

    public record StockResponse(
            String sku,
            String productName,
            int availableQuantity,
            int reservedQuantity
    ) {
    }
}
