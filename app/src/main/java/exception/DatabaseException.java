package exception;

public class DatabaseException extends RuntimeException{
    public DatabaseException(Throwable cause) {
        super("Database error", cause);
    }
}
