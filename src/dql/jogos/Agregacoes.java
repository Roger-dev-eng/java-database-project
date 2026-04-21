package dql.jogos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Agregacoes {

    public static Object[] contarTotal(Connection conexao) throws SQLException {
        String sql = "SELECT COUNT(*) AS total_jogo FROM jogos";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return new Object[]{
                    rs.getInt(1)
                };
            }
        }
        return null;
    }

    public static List<Object[]> agruparPorGenero(Connection conexao) throws SQLException {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT genero, COUNT(*) AS total FROM jogos GROUP BY genero";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] linha = new Object[]{
                    rs.getString("genero"),
                    rs.getInt("total")
                };
                resultados.add(linha);
            }
        }
        return resultados;
    }

    public static List<Object[]> agruparPorAno(Connection conexao) throws SQLException {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT ano_lancamento, COUNT(*) AS total FROM jogos GROUP BY ano_lancamento";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] linha = new Object[]{
                    rs.getInt("ano_lancamento"),
                    rs.getInt("total")
                };
                resultados.add(linha);
            }
        }
        return resultados;
    }

    public static List<Object[]> agruparPorDesenvolvedora(Connection conexao) throws SQLException {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT desenvolvedora, COUNT(*) AS total FROM jogos GROUP BY desenvolvedora";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] linha = new Object[]{
                    rs.getString("desenvolvedora"),
                    rs.getInt("total")
                };
                resultados.add(linha);
            }
        }
        return resultados;
    }

    public static List<Object[]> jogosMaisAvaliados(Connection conexao) throws SQLException {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT j.nome, COUNT(a.id_avaliacao) AS total_avaliacoes, AVG(a.nota) AS media_notas FROM jogos j LEFT JOIN avaliacoes a ON j.id_jogo = a.fk_jogo GROUP BY j.id_jogo, j.nome ORDER BY total_avaliacoes DESC";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] linha = new Object[]{
                    rs.getString("nome"),
                    rs.getInt("total_avaliacoes"),
                    rs.getDouble("media_notas")
                };
                resultados.add(linha);
            }
        }
        return resultados;
    }

}
