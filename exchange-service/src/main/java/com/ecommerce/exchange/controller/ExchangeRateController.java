package com.ecommerce.exchange.controller;

import com.ecommerce.exchange.dto.ExchangeRateResponse;
import com.ecommerce.exchange.service.ExchangeRateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/microservices/exchange")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping
    public ExchangeRateResponse getExchangeRate(
            @RequestParam("from") String baseCurrency,
            @RequestParam("to") String targetCurrency) {
        return exchangeRateService.getLatestRate(baseCurrency, targetCurrency);
    }
}
