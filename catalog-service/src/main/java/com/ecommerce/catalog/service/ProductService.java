package com.ecommerce.catalog.service;

import com.ecommerce.catalog.dto.ProductResponse;

public interface ProductService {
    ProductResponse getByProductId(String productId);
}
