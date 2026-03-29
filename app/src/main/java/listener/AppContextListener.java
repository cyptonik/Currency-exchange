package listener;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import exception.DatabaseException;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@WebListener
public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:/var/lib/tomcat10/currency.db");
        config.setDriverClassName("org.sqlite.JDBC");
        config.setMaximumPoolSize(1);
        HikariDataSource ds = new HikariDataSource(config);

        initDatabase(ds);

        sce.getServletContext().setAttribute("dataSource", ds);
    }

    private void initDatabase(DataSource ds) {
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("""
            CREATE TABLE IF NOT EXISTS Currencies (
                ID INTEGER PRIMARY KEY AUTOINCREMENT,
                Code VARCHAR(10) NOT NULL UNIQUE,
                FullName VARCHAR(50) NOT NULL,
                Sign VARCHAR(5) NOT NULL
            )
        """);
            stmt.execute("""
            CREATE TABLE IF NOT EXISTS ExchangeRates (
                ID INTEGER PRIMARY KEY AUTOINCREMENT,
                BaseCurrencyId INTEGER NOT NULL,
                TargetCurrencyId INTEGER NOT NULL,
                Rate DECIMAL(10,6) NOT NULL,
                FOREIGN KEY (BaseCurrencyId) REFERENCES Currencies(ID),
                FOREIGN KEY (TargetCurrencyId) REFERENCES Currencies(ID),
                UNIQUE (BaseCurrencyId, TargetCurrencyId)
            )
        """);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        HikariDataSource ds = (HikariDataSource) sce.getServletContext().getAttribute("dataSource");

        if (ds != null) {
            ds.close();
        }
    }
}
