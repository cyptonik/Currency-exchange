package exception;

import jakarta.servlet.http.HttpServletResponse;

public class NotFoundException extends AppException {
    public NotFoundException(String message) {
        super(HttpServletResponse.SC_NOT_FOUND, message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(HttpServletResponse.SC_NOT_FOUND, message, cause);
    }
}
