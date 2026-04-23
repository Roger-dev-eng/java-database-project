package InterfaceSwing.telas;

import app.db.Database;
import java.sql.Connection;
import java.sql.SQLException;

public class Conexao {
    public static Connection conectar() throws SQLException {
        return Database.conectar();
    }
}
