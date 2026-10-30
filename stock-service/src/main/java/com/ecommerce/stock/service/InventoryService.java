package com.ecommerce.stock.service;

import com.ecommerce.stock.dto.InventoryResponse;

public interface InventoryService {
    InventoryResponse getByProductId(String productId);
}
