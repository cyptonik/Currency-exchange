package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.Currency;
import dto.ErrorResponseDto;
import model.ExchangeRate;

import dao.ExchangeRatesDao;
import dao.CurrencyDao;

import javax.sql.DataSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private ExchangeRatesDao erDao;
    private CurrencyDao currencyDao;
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        DataSource ds = (DataSource) getServletContext().getAttribute("dataSource");

        this.erDao = new ExchangeRatesDao(ds);
        this.currencyDao = new CurrencyDao(ds);
        this.mapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            List<ExchangeRate> er = erDao.getAllExchangeRates();
            String json = mapper.writeValueAsString(er);

            resp.getWriter().println(json);
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("Database error")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String code1 = req.getParameter("baseCurrencyCode");

        if (code1 == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("baseCurrencyCode parameter not found")));
            return;
        }

        String code2 = req.getParameter("targetCurrencyCode");

        if (code2 == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("targetCurrencyCode parameter not found")));
            return;
        }

        String rateParam = req.getParameter("rate");

        if (rateParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("rate parameter not found")));
            return;
        }

        BigDecimal rate = new BigDecimal(rateParam);

        try {
            Currency c1 = currencyDao.getCurrencyByCode(code1);
            Currency c2 = currencyDao.getCurrencyByCode(code2);

            if (c1 == null || c2 == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("One of the pairs is not present in the database")));
                return;
            }

            erDao.addExchangeRate(c1.getId(), c2.getId(), rate);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().println(mapper.writeValueAsString(new ExchangeRate(c1, c2, rate)));
        } catch(SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.startsWith("23")) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("Currency pair with these codes already exists")));
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("Database error")));
            }
        }
    }
}
