package exception;

public class DatabaseException extends RuntimeException{
    public DatabaseException() {
        super("Database error");
    }

    public DatabaseException(Throwable cause) {
        super("Database error", cause);
    }
}
