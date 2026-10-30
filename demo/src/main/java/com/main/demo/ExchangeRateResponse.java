package com.main.demo;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExchangeRateResponse(
        @JsonProperty("base_currency") String baseCurrency,
        @JsonProperty("target_currency") String targetCurrency,
        BigDecimal rate,
        LocalDate date,
        String source) {
}
