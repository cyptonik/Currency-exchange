package exception;

public class ExchangeRateNotFoundException extends RuntimeException{
    public ExchangeRateNotFoundException() {
        super("Exchange rate not found");
    }

    public ExchangeRateNotFoundException(String message) {
        super(message);
    }
}
