package org.currency.exchange.util;

import org.currency.exchange.dto.CurrencyDto;
import org.currency.exchange.dto.ExchangeRateDto;
import org.currency.exchange.model.Currency;
import org.currency.exchange.model.ExchangeRate;

public class MapperToDto {
    public static ExchangeRateDto mapExchangeRateToDto(ExchangeRate exchangeRate) {
        return new ExchangeRateDto(
                exchangeRate.getId(),
                mapCurrencyToDto(exchangeRate.getBaseCurrency()),
                mapCurrencyToDto(exchangeRate.getTargetCurrency()),
                exchangeRate.getRate()
        );
    }

    public static CurrencyDto mapCurrencyToDto(Currency currency) {
        return new CurrencyDto(
                currency.getId(),
                currency.getCode(),
                currency.getFullName(),
                currency.getSign()
        );
    }
}
