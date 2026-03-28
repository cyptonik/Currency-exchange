package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.ExchangeRate;
import dao.ExchangeRatesDao;

import javax.sql.DataSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
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
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Code is missing");
            return;
        }


        String code = pathInfo.substring(1);

        if (code.length() != 6) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid currency pair code");
            return;
        }

        try {
            ExchangeRate er = erDao.getExchangeRateByCode(code);
            String json = mapper.writeValueAsString(er);

            resp.setContentType("application/json");
            resp.getWriter().println(json);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Code is missing");
            return;
        }

        String code = pathInfo.substring(1);

        if (code.length() != 6) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid currency pair code");
            return;
        }

        String body = req.getReader().lines().collect(Collectors.joining("&"));
        String rateStr = String.valueOf(Arrays.stream(body.split("&"))
                                            .filter(p -> p.startsWith("rate"))
                                            .map(p -> p.split("=")[1])
                                            .findFirst()
                                            .orElseThrow(() -> new RuntimeException("rate missing"))
        );
        BigDecimal newRate = new BigDecimal(rateStr);

        try {
            ExchangeRate er = erDao.getExchangeRateByCode(code);
            er.setRate(newRate);

            boolean rowsWereRead = erDao.updateExchangeRate(er.getBaseCurrency().getId(), er.getTargetCurrency().getId(), newRate);

            if (rowsWereRead) {
                resp.setContentType("application/json");
                resp.getWriter().println(mapper.writeValueAsString(er));
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Exchange rate not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
