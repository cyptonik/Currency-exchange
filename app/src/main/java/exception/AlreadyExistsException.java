package exception;

public class AlreadyExistsException extends AppException{
    public AlreadyExistsException(String message) {
        super(409, message);
    }

    public AlreadyExistsException(String message, Throwable cause) {
        super(409, message, cause);
    }
}
