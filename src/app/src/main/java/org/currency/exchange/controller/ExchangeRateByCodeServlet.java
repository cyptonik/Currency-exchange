package org.currency.exchange.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.currency.exchange.dao.ExchangeRatesDao;
import org.currency.exchange.dto.ExchangeRateDto;
import org.currency.exchange.exception.InvalidParametersException;
import org.currency.exchange.model.ExchangeRate;
import org.currency.exchange.util.MapperToDto;
import org.currency.exchange.util.ParamValidator;

import javax.sql.DataSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;

@WebServlet("/exchangeRate/*")
public class ExchangeRateByCodeServlet extends HttpServlet {
    private ExchangeRatesDao erDao;
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        DataSource ds = (DataSource) getServletContext().getAttribute("dataSource");

        this.erDao = new ExchangeRatesDao(ds);
        this.mapper = new ObjectMapper();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        String pathInfo = req.getPathInfo();
        ParamValidator.validatePathInfo(pathInfo);

        String code = pathInfo.substring(1);
        ParamValidator.validateCurrencyPair(code);

        ExchangeRateDto exchangeRateDto = MapperToDto.mapExchangeRateToDto(erDao.getExchangeRateByCode(code));
        resp.getWriter().println(mapper.writeValueAsString(exchangeRateDto));
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        String pathInfo = req.getPathInfo();
        ParamValidator.validatePathInfo(pathInfo);

        String code = pathInfo.substring(1);
        ParamValidator.validateCurrencyPair(code);

        String body = req.getReader().lines().collect(Collectors.joining("&"));
        String rateStr = Arrays.stream(body.split("&"))
                                            .filter(p -> p.startsWith("rate"))
                                            .map(p -> p.split("=")[1])
                                            .findFirst()
                                            .orElseThrow(() -> new InvalidParametersException("Rate parameter is missing"));

        ParamValidator.validateAmount(rateStr);
        BigDecimal newRate = new BigDecimal(rateStr);

        ExchangeRate patchedExchangeRate = erDao.getExchangeRateByCode(code);
        patchedExchangeRate.setRate(newRate);
        erDao.updateExchangeRate(patchedExchangeRate.getBaseCurrency().getId(), patchedExchangeRate.getTargetCurrency().getId(), newRate);

        ExchangeRateDto exchangeRateDto = MapperToDto.mapExchangeRateToDto(patchedExchangeRate);
        resp.getWriter().println(mapper.writeValueAsString(exchangeRateDto));
    }
}
