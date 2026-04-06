package org.currency.exchange.exception;

public class InvalidParametersException extends AppException {
    public InvalidParametersException() {
        super(400, "Required parameters are missing");
    }

    public InvalidParametersException(String message) {
        super(400, message);
    }
}
