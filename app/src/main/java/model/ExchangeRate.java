package model;

import java.math.BigDecimal;
import java.math.MathContext;

public class ExchangeRate {
    private int id;
    protected Currency baseCurrency;
    protected Currency targetCurrency;
    protected BigDecimal rate;

    public ExchangeRate() { }

    public ExchangeRate(Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public ExchangeRate(int id, Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate.setScale(2);
    }

    public int getId() {
        return id;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        if (rate.compareTo(new BigDecimal("0")) > 0) {
            this.rate = rate.setScale(2);
        }
    }
}
