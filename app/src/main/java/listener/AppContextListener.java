package listener;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL driver not found", e);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/currency_exchange");
        config.setUsername("postgres");
        config.setPassword("postgres");
        config.setMaximumPoolSize(10);

        HikariDataSource ds = new HikariDataSource(config);

        sce.getServletContext().setAttribute("dataSource", ds);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        HikariDataSource ds = (HikariDataSource) sce.getServletContext().getAttribute("dataSource");

        if (ds != null) {
            ds.close();
        }
    }
}
