package dao;

import exception.AlreadyExistsException;
import exception.DatabaseException;
import exception.NotFoundException;
import model.Currency;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDao {
    private final DataSource dataSource;

    public CurrencyDao(DataSource dataSource) { this.dataSource = dataSource; }

    public List<Currency> getAllCurrencies() {
        List<Currency> currencies = new ArrayList<>();
        String query = "SELECT * FROM Currencies";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                currencies.add(mapRow(rs));
            }
        } catch(SQLException e) {
            throw new DatabaseException(e);
        }

        return currencies;
    }

    public Currency getCurrencyByCode(String code) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM currencies WHERE code = ?")) {
            ps.setString(1, code);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) { throw new NotFoundException("Currency with this code is not present in the database"); }
                return mapRow(rs);
            }
        } catch(SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public boolean addCurrency(String code, String name, String sign) {
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
        } catch(SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.startsWith("23")) {
                throw new AlreadyExistsException("Currency with this code already exists", e);
            } else {
                throw new DatabaseException(e);
            }
        }
    }

    private Currency mapRow(ResultSet rs) throws SQLException {
        return new Currency(rs.getInt("id"),
                rs.getString("code"),
                rs.getString("fullName"),
                rs.getString("sign"));
    }
}
