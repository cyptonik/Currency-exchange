package org.currency.exchange.exception;

public class DatabaseException extends RuntimeException{
    public DatabaseException (String message) {
        super(message);
    }
    public DatabaseException(Throwable cause) {
        super("Database error", cause);
    }
}
