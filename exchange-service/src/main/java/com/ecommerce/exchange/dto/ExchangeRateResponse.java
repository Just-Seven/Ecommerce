package com.ecommerce.exchange.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExchangeRateResponse(
        @JsonProperty("base_currency")
        String baseCurrency,
        @JsonProperty("target_currency")
        String targetCurrency,
        BigDecimal rate,
        LocalDate date,
        String source) {
}
