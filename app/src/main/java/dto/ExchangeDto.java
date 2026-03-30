package dto;

import model.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ExchangeDto {
    private final CurrencyDto baseCurrency;
    private final CurrencyDto targetCurrency;
    private final BigDecimal rate;
    private final BigDecimal amount;
    private final BigDecimal convertedAmount;

    public ExchangeDto(CurrencyDto baseCurrency, CurrencyDto targetCurrency, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate.setScale(2, RoundingMode.HALF_EVEN);
        this.amount = amount.setScale(2, RoundingMode.HALF_EVEN);
        this.convertedAmount = convertedAmount.setScale(2, RoundingMode.HALF_EVEN);
    }

    public CurrencyDto getBaseCurrency() {
        return baseCurrency;
    }

    public CurrencyDto getTargetCurrency() {
        return targetCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }
}
