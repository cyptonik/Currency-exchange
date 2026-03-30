package util;

import dto.CurrencyDto;
import dto.ExchangeRateDto;
import model.Currency;
import model.ExchangeRate;

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
