package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CurrencyDao;
import dto.ExchangeDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dao.ExchangeRatesDao;
import service.ExchangeService;
import util.ParamValidator;

import javax.sql.DataSource;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
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

        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amountStr = req.getParameter("amount");

        ParamValidator.validateNotNull(from, to, amountStr);
        BigDecimal amount = new BigDecimal(amountStr);

        ExchangeDto exchangeDto = (new ExchangeService(erDao, currencyDao)).convert(from, to, amount);
        resp.getWriter().println(mapper.writeValueAsString(exchangeDto));
    }
}
