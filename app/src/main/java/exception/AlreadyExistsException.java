package exception;

import jakarta.servlet.http.HttpServletResponse;

public class AlreadyExistsException extends AppException{
    public AlreadyExistsException(String message) {
        super(HttpServletResponse.SC_CONFLICT, message);
    }

    public AlreadyExistsException(String message, Throwable cause) {
        super(HttpServletResponse.SC_CONFLICT, message, cause);
    }
}
