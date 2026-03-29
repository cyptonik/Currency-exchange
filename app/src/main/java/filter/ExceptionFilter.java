package filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ErrorResponseDto;
import exception.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class ExceptionFilter implements Filter {
    private ObjectMapper mapper;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.mapper = new ObjectMapper();
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResp = (HttpServletResponse) resp;
        try {
            chain.doFilter(req, resp);
        } catch (InvalidParametersException e) {
            sendError(httpResp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (NotFoundException e) {
            sendError(httpResp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (AlreadyExistsException e) {
            sendError(httpResp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (DatabaseException e) {
            sendError(httpResp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(status);
        resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto(message)));
    }
}
