package com.main.demo;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductSummaryResponse(
        @JsonProperty("product_id") String productId,
        String title,
        @JsonProperty("price_usd") BigDecimal priceUsd,
        @JsonProperty("price_mxn") BigDecimal priceMxn,
        @JsonProperty("stock_status") String stockStatus,
        @JsonProperty("available_units") int availableUnits,
        @JsonProperty("currency_conversion_source") String currencyConversionSource,
        String status) {
}
