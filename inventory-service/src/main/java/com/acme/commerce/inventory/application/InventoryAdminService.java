package com.acme.commerce.inventory.application;

import com.acme.commerce.inventory.api.StockController.CreateStockRequest;
import com.acme.commerce.inventory.api.StockController.StockResponse;
import com.acme.commerce.inventory.domain.StockItem;
import com.acme.commerce.inventory.domain.StockItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryAdminService {

    private final StockItemRepository stocks;

    public InventoryAdminService(StockItemRepository stocks) {
        this.stocks = stocks;
    }

    @Transactional(readOnly = true)
    public List<StockResponse> list() {
        return stocks.findAllByOrderBySkuAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StockResponse get(String sku) {
        return toResponse(stocks.findById(sku)
                .orElseThrow(() -> new IllegalArgumentException("SKU not found: " + sku)));
    }

    @Transactional
    public StockResponse createIfMissing(String sku, CreateStockRequest request) {
        StockItem stock = stocks.findById(sku)
                .orElseGet(() -> stocks.save(new StockItem(
                        sku,
                        request.productName(),
                        request.availableQuantity()
                )));
        return toResponse(stock);
    }

    @Transactional
    public StockResponse adjust(String sku, int delta) {
        StockItem stock = stocks.findForUpdate(sku)
                .orElseThrow(() -> new IllegalArgumentException("SKU not found: " + sku));
        stock.adjustAvailable(delta);
        return toResponse(stock);
    }

    private StockResponse toResponse(StockItem stock) {
        return new StockResponse(
                stock.getSku(),
                stock.getProductName(),
                stock.getAvailableQuantity(),
                stock.getReservedQuantity()
        );
    }
}
