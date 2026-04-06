package org.currency.exchange.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.currency.exchange.dao.CurrencyDao;
import org.currency.exchange.dao.ExchangeRatesDao;
import org.currency.exchange.dto.ExchangeDto;
import org.currency.exchange.service.ExchangeService;
import org.currency.exchange.util.ParamValidator;

import javax.sql.DataSource;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private ObjectMapper mapper;
    private ExchangeService exchangeService;

    @Override
    public void init() throws ServletException {
        DataSource ds = (DataSource) getServletContext().getAttribute("dataSource");

        this.mapper = new ObjectMapper();
        this.exchangeService = new ExchangeService(new ExchangeRatesDao(ds), new CurrencyDao(ds));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amountStr = req.getParameter("amount");

        ParamValidator.validateNotNull(from, to, amountStr);
        ParamValidator.validateAmount(amountStr);
        BigDecimal amount = new BigDecimal(amountStr);

        ExchangeDto exchangeDto = exchangeService.convert(from, to, amount);
        resp.getWriter().println(mapper.writeValueAsString(exchangeDto));
    }
}
