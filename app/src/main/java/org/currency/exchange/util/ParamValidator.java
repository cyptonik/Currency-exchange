package org.currency.exchange.util;

import org.currency.exchange.exception.InvalidParametersException;

import java.math.BigDecimal;

public class ParamValidator {
    private ParamValidator() {}

    public static void validateNotNull(String ... params) {
        for (String i : params) {
            if (i == null || i.isEmpty()) {
                throw new InvalidParametersException();
            }
        }
    }

    public static void validateCurrencyCode(String code) {
        if (code.length() != 3) throw new InvalidParametersException("Invalid currency code");
    }

    public static void validateCurrencyPair(String code) {
        if (code.length() != 6) throw new InvalidParametersException("Invalid currency pair");
    }

    public static void validateAmount(String amount) {
        try {
            BigDecimal parsedAmount = new BigDecimal(amount);
            if (parsedAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidParametersException("Amount should be greater than zero");
            }
        } catch (NumberFormatException e) {
            throw new InvalidParametersException("Amount must be a valid integer number");
        }
    }

    public static void validatePathInfo(String pathInfo) {
        if (pathInfo == null || pathInfo.equals("/")) { throw new InvalidParametersException("Code not found"); }
    }

}
