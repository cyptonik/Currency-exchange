package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        BigDecimal amount = new BigDecimal(req.getParameter("amount"));

        try {
            ExchangeRate exchangeRate = erDao.getExchangeRateByCode(from + to);
            if (exchangeRate != null) {
                // TODO: DTO надо
                ObjectNode node = mapper.valueToTree(exchangeRate);
                node.remove("id");
                node.put("amount", amount);
                node.put("convertedAmount", amount.multiply(exchangeRate.getRate()));

                resp.setContentType("application/json");
                resp.getWriter().println(mapper.writeValueAsString(node));
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.setContentType("application/json");
                resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("Database error")));
            }
        } catch(SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("Database error")));
        }
    }
}
