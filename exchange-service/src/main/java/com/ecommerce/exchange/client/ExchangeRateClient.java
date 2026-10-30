package com.ecommerce.exchange.client;

public interface ExchangeRateClient {
    ExchangeRateProviderResponse getLatestRate(String baseCurrency, String targetCurrency);
}
