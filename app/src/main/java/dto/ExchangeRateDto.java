package dto;

import model.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ExchangeRateDto {
    private final int id;
    private final CurrencyDto baseCurrency;
    private final CurrencyDto targetCurrency;
    private final BigDecimal rate;

    public ExchangeRateDto(int id, CurrencyDto baseCurrency, CurrencyDto targetCurrency, BigDecimal rate) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public CurrencyDto getBaseCurrency() {
        return baseCurrency;
    }

    public CurrencyDto getTargetCurrency() {
        return targetCurrency;
    }

    public BigDecimal getRate() {
        return rate.setScale(2, RoundingMode.HALF_EVEN);
    }
}
