package com.main.demo;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CatalogProductResponse(
        @JsonProperty("product_id") String productId,
        String name,
        String description,
        String category,
        @JsonProperty("price_usd") BigDecimal priceUsd) {
}
