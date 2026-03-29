package util;

import exception.InvalidParametersException;

public class ParamValidator {
    public static void validateNotNull(String ... params) {
        for (String i : params) {
            if (i == null) {
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

    public static void validatePathInfo(String pathInfo) {
        if (pathInfo == null || pathInfo.equals("/")) { throw new InvalidParametersException("Code not found"); }
    }

}
