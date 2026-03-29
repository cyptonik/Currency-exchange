package util;

import exception.InvalidParametersException;

public class ParamValidator {
    public static void validateNotNull(String ... params) throws InvalidParametersException {
        for (String i : params) {
            if (i == null) {
                throw new InvalidParametersException();
            }
        }
    }
}
