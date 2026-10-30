package com.ecommerce.exchange.client;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExchangeRateProviderResponse(
        LocalDate date,
        String base,
        String quote,
        BigDecimal rate) {
}
