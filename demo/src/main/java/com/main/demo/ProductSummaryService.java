package com.main.demo;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class ProductSummaryService {

    private final WebClient catalogClient;
    private final WebClient stockClient;
    private final WebClient exchangeClient;
    private final String exchangeSourceName;

    public ProductSummaryService(
            @Value("${services.catalog.base-url}") String catalogBaseUrl,
            @Value("${services.stock.base-url}") String stockBaseUrl,
            @Value("${services.exchange.base-url}") String exchangeBaseUrl,
            @Value("${services.exchange.source-name}") String exchangeSourceName) {
        this.catalogClient = WebClient.builder().baseUrl(catalogBaseUrl).build();
        this.stockClient = WebClient.builder().baseUrl(stockBaseUrl).build();
        this.exchangeClient = WebClient.builder().baseUrl(exchangeBaseUrl).build();
        this.exchangeSourceName = exchangeSourceName;
    }

    public Mono<ProductSummaryResponse> getProductSummary(String productId) {
        Mono<CatalogProductResponse> product = catalogClient.get()
                .uri("/microservices/products/{id}", productId)
                .retrieve()
                .bodyToMono(CatalogProductResponse.class);

        Mono<StockResponse> stock = stockClient.get()
                .uri("/microservices/stock/{productId}", productId)
                .retrieve()
                .bodyToMono(StockResponse.class);

        Mono<ExchangeRateResponse> exchangeRate = exchangeClient.get()
                .uri(uriBuilder -> uriBuilder.path("/microservices/exchange")
                        .queryParam("from", "USD")
                        .queryParam("to", "MXN")
                        .build())
                .retrieve()
                .bodyToMono(ExchangeRateResponse.class);

        return Mono.zip(product, stock, exchangeRate)
                .map(results -> toSummary(productId, results.getT1(), results.getT2(), results.getT3()));
    }

    private ProductSummaryResponse toSummary(
            String productId,
            CatalogProductResponse product,
            StockResponse stock,
            ExchangeRateResponse exchangeRate) {
        BigDecimal priceMxn = product.priceUsd()
                .multiply(exchangeRate.rate())
                .setScale(2, RoundingMode.HALF_UP);

        return new ProductSummaryResponse(
                productId,
                product.name(),
                product.priceUsd(),
                priceMxn,
                stock.stockStatus(),
                stock.availableUnits(),
                exchangeSourceName,
                "SUCCESS");
    }
}
