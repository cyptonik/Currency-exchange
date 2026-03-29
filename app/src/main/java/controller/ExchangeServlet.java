package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ExchangeDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import dto.ErrorResponseDto;
import model.ExchangeRate;

import dao.ExchangeRatesDao;
import util.ParamValidator;

import javax.sql.DataSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private ExchangeRatesDao erDao;
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        DataSource ds = (DataSource) getServletContext().getAttribute("dataSource");

        this.erDao = new ExchangeRatesDao(ds);
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

        ExchangeRate exchangeRate = erDao.getExchangeRateByCode(from + to);

        ExchangeDto exchangeDto = new ExchangeDto(
                exchangeRate.getBaseCurrency(),
                exchangeRate.getTargetCurrency(),
                exchangeRate.getRate(),
                amount,
                amount.multiply(exchangeRate.getRate()));

        resp.getWriter().println(mapper.writeValueAsString(exchangeDto));
//        try {
//            // TODO: сделать перевод валют через существующие (см. задание)
//            ExchangeRate exchangeRate = erDao.getExchangeRateByCode(from + to);
//            if (exchangeRate != null) {
//                ExchangeDto exchangeDto = new ExchangeDto(
//                        exchangeRate.getBaseCurrency(),
//                        exchangeRate.getTargetCurrency(),
//                        exchangeRate.getRate(),
//                        amount,
//                        amount.multiply(exchangeRate.getRate()));
//
//                resp.getWriter().println(mapper.writeValueAsString(exchangeDto));
//            } else {
//                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
//                resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("Exchange rate is not present in the database")));
//            }
//        } catch(SQLException e) {
//            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("Database error")));
//        }
    }
}
