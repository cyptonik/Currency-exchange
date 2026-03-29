package service;

import dao.CurrencyDao;
import dao.ExchangeRatesDao;
import dto.ExchangeDto;
import exception.NotFoundException;
import model.Currency;
import model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

        try {
            return convertFromDirect(from, to, amount);
        } catch (NotFoundException e1) {
            try {
                return convertFromInverse(currencyFrom, currencyTo, amount);
            } catch (NotFoundException e2) {
                try {
                    return convertFromUsd(currencyFrom, currencyTo, amount);
                } catch (NotFoundException e3) {
                    throw new NotFoundException("Couldn't find exchange rates");
                }
            }
        }
    }

    private ExchangeDto convertFromDirect(String from, String to, BigDecimal amount) {
        ExchangeRate exchangeRate = erDao.getExchangeRateByCode(from + to);
        return new ExchangeDto(
                exchangeRate.getBaseCurrency(),
                exchangeRate.getTargetCurrency(),
                exchangeRate.getRate(),
                amount,
                amount.multiply(exchangeRate.getRate()));
    }

    private ExchangeDto convertFromInverse(Currency currencyFrom, Currency currencyTo, BigDecimal amount) {
        ExchangeRate inverseExchangeRate = erDao.getExchangeRateByCode(currencyTo.getCode() + currencyFrom.getCode());
        BigDecimal rate = BigDecimal.ONE.divide(inverseExchangeRate.getRate(), 10, roundingMode);

        return new ExchangeDto(
                currencyFrom,
                currencyTo,
                rate,
                amount,
                amount.multiply(rate));
    }

    private ExchangeDto convertFromUsd(Currency currencyFrom, Currency currencyTo, BigDecimal amount) {
        BigDecimal usdToFromRate = getUsdRate(currencyFrom);
        BigDecimal usdToToRate = getUsdRate(currencyTo);

        BigDecimal rate = usdToToRate.divide(usdToFromRate, 10, roundingMode);

        return new ExchangeDto(
                currencyFrom,
                currencyTo,
                rate,
                amount,
                amount.multiply(rate)
        );
    }

    private BigDecimal getUsdRate(Currency currency) {
        if (currency.getCode().equals("USD")) return BigDecimal.ONE;

        try {
            return erDao.getExchangeRateByCode("USD" + currency.getCode()).getRate();
        } catch (NotFoundException e) {
            BigDecimal inverse = erDao.getExchangeRateByCode(currency.getCode() + "USD").getRate();
            return BigDecimal.ONE.divide(inverse, 6, RoundingMode.HALF_UP);
        }
    }
}
