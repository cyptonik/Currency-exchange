package dto;

import model.Currency;

import java.math.BigDecimal;

public class ExchangeDto {
    public Currency baseCurrency;
    public Currency targetCurrency;
    public BigDecimal rate;
    public BigDecimal amount;
    public BigDecimal convertedAmount;

    public ExchangeDto(Currency baseCurrency, Currency targetCurrency, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
    }
}
