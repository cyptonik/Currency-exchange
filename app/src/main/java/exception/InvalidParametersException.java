package exception;

import jakarta.servlet.http.HttpServletResponse;

public class InvalidParametersException extends AppException {
    public InvalidParametersException() {
        super(HttpServletResponse.SC_BAD_REQUEST, "Required parameters are missing");
    }

    public InvalidParametersException(String message) {
        super(HttpServletResponse.SC_BAD_REQUEST, message);
    }
}
