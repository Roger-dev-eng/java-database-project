import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import ddl.Tabelas;

public class TesteConexao {
    public static void main(String[] args) {
        try (Connection conexao = Conexao.conectar()) {
            System.out.println("Conexao com o banco estabelecida com sucesso!\n");

            executarDDL(conexao);

        } catch (SQLException | IllegalStateException e) {
            System.out.println("Falha ao conectar ao banco: " + e.getMessage());
        }
    }

    private static void executarDDL(Connection conexao) throws SQLException {
        System.out.println("Executando scripts DDL...\n");

        Statement stmt = conexao.createStatement();

        try {
            System.out.println("Criando tabela 'jogos'...");
            stmt.execute(Tabelas.CREATE_JOGOS);
            System.out.println("✓ Tabela 'jogos' criada com sucesso!\n");

            System.out.println("Criando tabela 'jogadores'...");
            stmt.execute(Tabelas.CREATE_JOGADORES);
            System.out.println("✓ Tabela 'jogadores' criada com sucesso!\n");

            System.out.println("Criando tabela 'plataformas'...");
            stmt.execute(Tabelas.CREATE_PLATAFORMAS);
            System.out.println("✓ Tabela 'plataformas' criada com sucesso!\n");

            System.out.println("Criando tabela 'avaliacoes'...");
            stmt.execute(Tabelas.CREATE_AVALIACOES);
            System.out.println("✓ Tabela 'avaliacoes' criada com sucesso!\n");

            System.out.println("===========================================");
            System.out.println("✓ Todos os scripts DDL foram executados com sucesso!");
            System.out.println("===========================================");

        } catch (SQLException e) {
            System.out.println("✗ Erro ao executar os scripts DDL: " + e.getMessage());
        } finally {
            stmt.close();
        }
    }
}