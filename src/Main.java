import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try (Connection conexao = Conexao.conectar()) {
            System.out.println("Conexao com o banco estabelecida com sucesso!");
        } catch (SQLException | IllegalStateException e) {
            System.out.println("Falha ao conectar ao banco: " + e.getMessage());
        }
    }
}