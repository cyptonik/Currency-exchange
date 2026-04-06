package org.currency.exchange.exception;

public class NotFoundException extends AppException {
    public NotFoundException(String message) {
        super(404, message);
    }
}
