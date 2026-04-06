package org.currency.exchange.dao;

import org.currency.exchange.model.Currency;
import org.currency.exchange.model.ExchangeRate;
import org.currency.exchange.exception.AlreadyExistsException;
import org.currency.exchange.exception.DatabaseException;
import org.currency.exchange.exception.NotFoundException;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRatesDao {
    private final DataSource dataSource;

    public ExchangeRatesDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<ExchangeRate> getAllExchangeRates() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        String query = "SELECT er.id, " +
                "c1.id AS base_id, c1.code AS base_code, c1.fullname AS base_fullname, c1.sign AS base_sign, " +
                "c2.id AS target_id, c2.code AS target_code, c2.fullname AS target_fullname, c2.sign AS target_sign, " +
                "er.rate " +
                "FROM ExchangeRates er " +
                "JOIN Currencies c1 ON er.BaseCurrencyId = c1.ID " +
                "JOIN Currencies c2 ON er.TargetCurrencyId = c2.ID";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                exchangeRates.add(mapRow(rs));
            }
        } catch(SQLException e) {
            throw new DatabaseException(e);
        }

        return exchangeRates;
    }

    public ExchangeRate getExchangeRateByCode(String code) {
        String query =
                "SELECT er.id AS id, " +
                        "c1.id AS base_id, c1.code AS base_code, c1.fullname AS base_fullname, c1.sign AS base_sign, " +
                        "c2.id AS target_id, c2.code AS target_code, c2.fullname AS target_fullname, c2.sign AS target_sign, " +
                        "er.rate as rate " +
                        "FROM ExchangeRates er " +
                        "JOIN Currencies c1 ON er.BaseCurrencyId = c1.ID " +
                        "JOIN Currencies c2 ON er.TargetCurrencyId = c2.ID " +
                        "WHERE c1.code = ? AND c2.code = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, code.substring(0, 3));
            ps.setString(2, code.substring(3, 6));

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) { throw new NotFoundException("Exchange rate with this code is not present in the database"); }
                return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public ExchangeRate addExchangeRate(Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
        String query =
                "INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) " +
                "VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, baseCurrency.getId());
            ps.setInt(2, targetCurrency.getId());
            ps.setBigDecimal(3, rate);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    return new ExchangeRate(id, baseCurrency, targetCurrency, rate);
                }
            }
            throw new DatabaseException("No generated keys");
        } catch(SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
                throw new AlreadyExistsException("Exchange rate with this pair already exists", e);
            } else {
                throw new DatabaseException(e);
            }
        }
    }

    public void updateExchangeRate(int baseCurrencyId, int targetCurrencyId, BigDecimal newRate) {
        String query = "UPDATE ExchangeRates SET rate = ? WHERE baseCurrencyId = ? and targetCurrencyId = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setBigDecimal(1, newRate);
            ps.setInt(2, baseCurrencyId);
            ps.setInt(3, targetCurrencyId);

            ps.executeUpdate();
        } catch(SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private ExchangeRate mapRow(ResultSet rs) throws SQLException {
        Currency c1 = new Currency(
                rs.getInt("base_id"),
                rs.getString("base_code"),
                rs.getString("base_fullname"),
                rs.getString("base_sign")
        );
        Currency c2 = new Currency(
                rs.getInt("target_id"),
                rs.getString("target_code"),
                rs.getString("target_fullname"),
                rs.getString("target_sign")
        );
        return new ExchangeRate(rs.getInt("id"), c1, c2, rs.getBigDecimal("rate"));
    }
}
