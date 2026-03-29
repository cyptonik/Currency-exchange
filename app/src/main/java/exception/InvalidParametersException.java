package exception;

public class InvalidParametersException extends RuntimeException {
    public InvalidParametersException() {
        super("Required parameters are missing");
    }

    public InvalidParametersException(String message) {
        super(message);
    }
}
