package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.Currency;

import dao.CurrencyDao;
import util.ParamValidator;

import javax.sql.DataSource;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
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

        List<Currency> currencies = currencyDao.getAllCurrencies();
        resp.getWriter().println(mapper.writeValueAsString(currencies));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String sign = req.getParameter("sign");

        ParamValidator.validateNotNull(code, name, sign);

        currencyDao.addCurrency(code, name, sign);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().println(mapper.writeValueAsString(currencyDao.getCurrencyByCode(code)));
    }
}
