package app.db;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class Database {
    private static final HikariDataSource POOL = criarPool();

    private Database() {
    }

    public static Connection conectar() throws SQLException {
        return POOL.getConnection();
    }

    private static HikariDataSource criarPool() {
        String url = System.getenv("DB_URL");
        String usuario = System.getenv("DB_USER");
        String senha = System.getenv("DB_PASSWORD");

        if (url == null || usuario == null || senha == null
                || url.isBlank() || usuario.isBlank() || senha.isBlank()) {
            throw new IllegalStateException("Defina DB_URL, DB_USER e DB_PASSWORD nas variaveis de ambiente.");
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url.trim());
        config.setUsername(usuario.trim());
        config.setPassword(senha);
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(10000);
        config.setValidationTimeout(5000);
        return new HikariDataSource(config);
    }
}
