package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dto.ErrorResponseDto;
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
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("Code not found")));
            return;
        }

        String code = pathInfo.substring(1);

        if (code.length() != 6) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("Incorrect currency pair")));
            return;
        }

        try {
            ExchangeRate er = erDao.getExchangeRateByCode(code);

            if (er == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("Currency not found")));
                return;
            }

            resp.getWriter().println(mapper.writeValueAsString(er));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("Database error")));
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("Code not found")));
            return;
        }

        String code = pathInfo.substring(1);

        if (code.length() != 6) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("Incorrect currency pair")));
            return;
        }

        String body = req.getReader().lines().collect(Collectors.joining("&"));
        String rateStr = String.valueOf(Arrays.stream(body.split("&"))
                                            .filter(p -> p.startsWith("rate"))
                                            .map(p -> p.split("=")[1])
                                            .findFirst()
                                            .orElseThrow(() -> new RuntimeException("rate missing"))
        );

        if (rateStr.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("rate parameter not found")));
            return;
        }

        BigDecimal newRate = new BigDecimal(rateStr);

        try {
            ExchangeRate er = erDao.getExchangeRateByCode(code);
            if (er == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("One of the pairs is not present in the database")));
            }
            er.setRate(newRate);

            erDao.updateExchangeRate(er.getBaseCurrency().getId(), er.getTargetCurrency().getId(), newRate);
            resp.getWriter().println(mapper.writeValueAsString(er));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("Database error")));
        }
    }
}
