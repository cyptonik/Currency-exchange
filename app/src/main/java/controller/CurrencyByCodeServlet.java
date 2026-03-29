package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CurrencyDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import model.ErrorResponseDto;

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
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("Code not found")));
            return;
        }

        String code = pathInfo.substring(1);

        try {
            Currency currency = currencyDao.getCurrencyByCode(code);

            if (currency != null) {
                resp.setContentType("application/json");
                resp.getWriter().println(mapper.writeValueAsString(currency));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.setContentType("application/json");
                resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("Currency not found")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
