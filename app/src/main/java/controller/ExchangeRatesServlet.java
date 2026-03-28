package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.Currency;
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
        try {
            List<ExchangeRate> er = erDao.getAllExchangeRates();
            String json = mapper.writeValueAsString(er);

            resp.setContentType("application/json");
            resp.getWriter().println(json);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code1 = req.getParameter("baseCurrencyCode");
        String code2 = req.getParameter("targetCurrencyCode");
        BigDecimal rate = new BigDecimal(req.getParameter("rate"));

        try {
            Currency c1 = currencyDao.getCurrencyByCode(code1);
            Currency c2 = currencyDao.getCurrencyByCode(code2);

            boolean rowsWereRead = erDao.addExchangeRate(c1.getId(), c2.getId(), rate);

            if (rowsWereRead) {
                resp.setContentType("application/json");
                resp.getWriter().println(mapper.writeValueAsString(new ExchangeRate(c1, c2, rate)));
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Exchange rate not found");
            }
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
