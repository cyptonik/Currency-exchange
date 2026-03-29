package service;

import dao.CurrencyDao;
import dao.ExchangeRatesDao;
import dto.ExchangeDto;
import exception.NotFoundException;
import model.Currency;
import model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class ExchangeService {
    private final ExchangeRatesDao erDao;
    private final CurrencyDao currencyDao;
    private final RoundingMode roundingMode;

    public ExchangeService(ExchangeRatesDao erDao, CurrencyDao currencyDao) {
        this.erDao = erDao;
        this.currencyDao = currencyDao;
        this.roundingMode = RoundingMode.HALF_EVEN;
    }

    public ExchangeService(ExchangeRatesDao erDao, CurrencyDao currencyDao, RoundingMode roundingMode) {
        this.erDao = erDao;
        this.currencyDao = currencyDao;
        this.roundingMode = roundingMode;
    }

    public ExchangeDto convert(String from, String to, BigDecimal amount) {
        Currency currencyFrom = currencyDao.getCurrencyByCode(from);
        Currency currencyTo = currencyDao.getCurrencyByCode(to);

        return convertFromDirect(from, to, amount)
                .orElseGet(() -> convertFromInverse(currencyFrom, currencyTo, amount)
                        .orElseGet(() -> convertFromUsd(currencyFrom, currencyTo, amount)
                                .orElseThrow(() -> new NotFoundException("Couldn't find exchange rates"))));

    }

    private Optional<ExchangeDto> convertFromDirect(String from, String to, BigDecimal amount) {
        try {
            ExchangeRate exchangeRate = erDao.getExchangeRateByCode(from + to);
            return Optional.of(new ExchangeDto(
                    exchangeRate.getBaseCurrency(),
                    exchangeRate.getTargetCurrency(),
                    exchangeRate.getRate(),
                    amount,
                    amount.multiply(exchangeRate.getRate()))
            );
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    private Optional<ExchangeDto> convertFromInverse(Currency currencyFrom, Currency currencyTo, BigDecimal amount) {
        try {
            ExchangeRate inverseExchangeRate = erDao.getExchangeRateByCode(currencyTo.getCode() + currencyFrom.getCode());
            BigDecimal rate = BigDecimal.ONE.divide(inverseExchangeRate.getRate(), 10, roundingMode);

            return Optional.of(new ExchangeDto(
                    currencyFrom,
                    currencyTo,
                    rate,
                    amount,
                    amount.multiply(rate))
            );
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    private Optional<ExchangeDto> convertFromUsd(Currency currencyFrom, Currency currencyTo, BigDecimal amount) {
        try {
            BigDecimal usdToFromRate = getUsdRate(currencyFrom);
            BigDecimal usdToToRate = getUsdRate(currencyTo);

            BigDecimal rate = usdToToRate.divide(usdToFromRate, 10, roundingMode);

            return Optional.of(new ExchangeDto(
                    currencyFrom,
                    currencyTo,
                    rate,
                    amount,
                    amount.multiply(rate)
            ));
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    private BigDecimal getUsdRate(Currency currency) {
        if (currency.getCode().equals("USD")) return BigDecimal.ONE;

        try {
            return erDao.getExchangeRateByCode("USD" + currency.getCode()).getRate();
        } catch (NotFoundException e) {
            BigDecimal inverse = erDao.getExchangeRateByCode(currency.getCode() + "USD").getRate();
            return BigDecimal.ONE.divide(inverse, 6, roundingMode);
        }
    }
}
