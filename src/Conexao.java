import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    public static Connection conectar() throws SQLException {
        String url = System.getenv("DB_URL");
        String usuario = System.getenv("DB_USER");
        String senha = System.getenv("DB_PASSWORD");

        if (url == null || usuario == null || senha == null) {
            throw new IllegalStateException("Defina DB_URL, DB_USER e DB_PASSWORD nas variáveis de ambiente.");
        }

        return DriverManager.getConnection(url, usuario, senha);
    }
}
