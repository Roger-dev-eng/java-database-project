package app.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

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

        String supabaseUrl = System.getenv("SUPABASE_DB_URL");
        if ((url == null || url.isBlank()) && supabaseUrl != null && !supabaseUrl.isBlank()) {
            String connectionString = supabaseUrl.trim();
            if (connectionString.startsWith("jdbc:postgresql://")) {
                url = connectionString;
            } else {
                URI parsed = URI.create(connectionString.replaceFirst("^postgres(?:ql)?://", "https://"));
                String userInfo = parsed.getUserInfo();
                if (userInfo != null && userInfo.contains(":")) {
                    String[] credentials = userInfo.split(":", 2);
                    usuario = URLDecoder.decode(credentials[0], StandardCharsets.UTF_8);
                    senha = URLDecoder.decode(credentials[1], StandardCharsets.UTF_8);
                }
                String path = parsed.getPath() == null ? "" : parsed.getPath();
                String query = parsed.getQuery() == null ? "" : "?" + parsed.getQuery();
                url = "jdbc:postgresql://" + parsed.getHost() + ":" + parsed.getPort() + path + query;
            }
        }

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
