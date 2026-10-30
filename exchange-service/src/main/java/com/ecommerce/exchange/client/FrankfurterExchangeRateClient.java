package com.ecommerce.exchange.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Locale;

@Component
public class FrankfurterExchangeRateClient implements ExchangeRateClient {

    private final RestClient restClient;

    public FrankfurterExchangeRateClient(
            @Value("${exchange.provider.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public ExchangeRateProviderResponse getLatestRate(String baseCurrency, String targetCurrency) {
        return restClient.get()
                .uri("/v2/rate/{base}/{quote}",
                        baseCurrency.toLowerCase(Locale.ROOT),
                        targetCurrency.toLowerCase(Locale.ROOT))
                .retrieve()
                .body(ExchangeRateProviderResponse.class);
    }
}
