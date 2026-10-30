package com.ecommerce.stock.service;

import com.ecommerce.stock.dto.InventoryResponse;
import com.ecommerce.stock.exception.InventoryNotFoundException;
import com.ecommerce.stock.model.Inventory;
import com.ecommerce.stock.repository.InventoryRepository;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public InventoryResponse getByProductId(String productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(productId));

        String stockStatus = inventory.getAvailableUnits() > 0 ? "IN_STOCK" : "OUT_OF_STOCK";
        return new InventoryResponse(
                inventory.getProductId(),
                stockStatus,
                inventory.getAvailableUnits(),
                inventory.getWarehouseLocation(),
                inventory.getEstimatedDelivery());
    }
}
