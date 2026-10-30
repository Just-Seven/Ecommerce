package com.ecommerce.exchange.service;

import com.ecommerce.exchange.dto.ExchangeRateResponse;

public interface ExchangeRateService {
    ExchangeRateResponse getLatestRate(String baseCurrency, String targetCurrency);
}
