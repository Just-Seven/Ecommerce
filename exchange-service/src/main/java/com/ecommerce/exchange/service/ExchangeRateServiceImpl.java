package com.ecommerce.exchange.service;

import com.ecommerce.exchange.client.ExchangeRateClient;
import com.ecommerce.exchange.client.ExchangeRateProviderResponse;
import com.ecommerce.exchange.dto.ExchangeRateResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private static final Pattern CURRENCY_CODE = Pattern.compile("[A-Za-z]{3}");

    private final ExchangeRateClient exchangeRateClient;
    private final String providerName;

    public ExchangeRateServiceImpl(
            ExchangeRateClient exchangeRateClient,
            @Value("${exchange.provider.name}") String providerName) {
        this.exchangeRateClient = exchangeRateClient;
        this.providerName = providerName;
    }

    @Override
    public ExchangeRateResponse getLatestRate(String baseCurrency, String targetCurrency) {
        String base = normalizeCurrencyCode(baseCurrency);
        String target = normalizeCurrencyCode(targetCurrency);

        try {
            ExchangeRateProviderResponse providerResponse = exchangeRateClient.getLatestRate(base, target);
            if (providerResponse == null || providerResponse.rate() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY, "El proveedor no devolvió una tasa de cambio válida.");
            }

            return new ExchangeRateResponse(
                    providerResponse.base(),
                    providerResponse.quote(),
                    providerResponse.rate(),
                    providerResponse.date(),
                    providerName);
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY, "No fue posible consultar el proveedor de tipo de cambio.", exception);
        }
    }

    private String normalizeCurrencyCode(String currencyCode) {
        if (currencyCode == null || !CURRENCY_CODE.matcher(currencyCode).matches()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La moneda debe ser un código de tres letras, como USD o MXN.");
        }
        return currencyCode.toUpperCase(Locale.ROOT);
    }
}
