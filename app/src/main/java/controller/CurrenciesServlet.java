package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.Currency;

import dao.CurrencyDao;
import dto.ErrorResponseDto;

import javax.sql.DataSource;

import java.io.IOException;
import java.sql.SQLException;
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
        try {
            List<Currency> currencies = currencyDao.getAllCurrencies();
            String json = mapper.writeValueAsString(currencies);

            resp.getWriter().println(json);
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("Database error")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String sign = req.getParameter("sign");

        if (code == null || name == null || sign == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("Missing parameters")));
            return;
        }

        try {
            currencyDao.addCurrency(code, name, sign);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().println(mapper.writeValueAsString(currencyDao.getCurrencyByCode(code)));
        } catch(SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.startsWith("23")) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("Currency with this code already exists")));
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().println(mapper.writeValueAsString(new ErrorResponseDto("Database error")));
            }
        }
    }
}
