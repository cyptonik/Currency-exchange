package dao;

import model.Currency;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDao {
    private final DataSource dataSource;

    public CurrencyDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Currency> getAllCurrencies() throws SQLException {
        List<Currency> exchangeRates = new ArrayList<>();
        String query = "SELECT * FROM Currencies";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Currency currency = new Currency(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("fullName"),
                        rs.getString("sign"));
                exchangeRates.add(currency);
            }
        }
        return exchangeRates;
    }

    public Currency getCurrencyByCode(String code) throws SQLException {
        Currency currency = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM currencies WHERE code = ?")) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                currency = new Currency(rs.getInt("id"),
                                        rs.getString("code"),
                                        rs.getString("fullName"),
                                        rs.getString("sign"));
            }
        }

        return currency;
    }

    public boolean addCurrency(String code, String name, String sign) throws SQLException {
        String query =
                "INSERT INTO Currencies (Code, FullName, Sign) " +
                "VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, code);
            ps.setString(2, name);
            ps.setString(3, sign);

            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }
}
