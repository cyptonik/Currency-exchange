package org.currency.exchange.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.currency.exchange.dao.CurrencyDao;
import org.currency.exchange.dto.CurrencyDto;
import org.currency.exchange.util.MapperToDto;
import org.currency.exchange.util.ParamValidator;

import javax.sql.DataSource;
import java.io.IOException;

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
        ParamValidator.validatePathInfo(pathInfo);

        String code = pathInfo.substring(1);
        ParamValidator.validateCurrencyCode(code);

        CurrencyDto currencyDto = MapperToDto.mapCurrencyToDto(currencyDao.getCurrencyByCode(code));
        resp.getWriter().println(mapper.writeValueAsString(currencyDto));
    }
}
