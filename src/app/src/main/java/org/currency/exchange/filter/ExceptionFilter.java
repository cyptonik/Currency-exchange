package org.currency.exchange.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.currency.exchange.dto.ErrorResponseDto;
import org.currency.exchange.exception.AppException;
import org.currency.exchange.exception.DatabaseException;

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
        } catch(AppException e) {
            sendError(httpResp, e.getStatus(), e.getMessage());
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
