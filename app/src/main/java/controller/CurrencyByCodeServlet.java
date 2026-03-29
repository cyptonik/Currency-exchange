package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CurrencyDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import dto.ErrorResponseDto;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/currency/*")
public class CurrencyByCodeServlet extends HttpServlet {
    private CurrencyDao currencyDao;
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        DataSource ds = (DataSource) getServletContext().getAttribute("dataSource");
        this.currencyDao = new CurrencyDao(ds);
        this.mapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("Code not found")));
            return;
        }

        String code = pathInfo.substring(1);

        if (code.length() != 3) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("Incorrect currency")));
            return;
        }

        try {
            Currency currency = currencyDao.getCurrencyByCode(code);

            if (currency == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("Currency not found")));
                return;
            }

            resp.getWriter().println(mapper.writeValueAsString(currency));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("Database error")));
        }
    }
}
