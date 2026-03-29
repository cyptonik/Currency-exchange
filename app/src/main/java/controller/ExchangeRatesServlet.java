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
import util.ParamValidator;

import javax.sql.DataSource;

import java.io.IOException;
import java.math.BigDecimal;
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

        List<ExchangeRate> er = erDao.getAllExchangeRates();
        resp.getWriter().println(mapper.writeValueAsString(er));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String code1 = req.getParameter("baseCurrencyCode");
        String code2 = req.getParameter("targetCurrencyCode");
        String rateParam = req.getParameter("rate");

        ParamValidator.validateNotNull(code1, code2, rateParam);

        BigDecimal rate = new BigDecimal(rateParam);

        Currency c1 = currencyDao.getCurrencyByCode(code1);
        Currency c2 = currencyDao.getCurrencyByCode(code2);

        erDao.addExchangeRate(c1.getId(), c2.getId(), rate);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().println(mapper.writeValueAsString(new ExchangeRate(c1, c2, rate)));
    }
}
