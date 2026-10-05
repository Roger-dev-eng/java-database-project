package dql.avaliacoes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Agregacoes {

    public static Object[] contarTotal(Connection conexao) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM avaliacoes";
        
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

    public static Object[] mediaNotasGerais(Connection conexao) throws SQLException {
        String sql = "SELECT AVG(nota) AS media_notas FROM avaliacoes";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return new Object[]{
                    rs.getDouble(1)
                };
            }
        }
        return null;
    }

    public static List<Object[]> mediaNotasPorJogo(Connection conexao) throws SQLException {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT j.nome AS jogo_nome, AVG(a.nota) AS media_notas, COUNT(a.id_avaliacao) AS total_avaliacoes " +
                     "FROM avaliacoes a " +
                     "INNER JOIN jogos j ON a.fk_jogo = j.id_jogo " +
                     "GROUP BY j.id_jogo, j.nome " +
                     "ORDER BY media_notas DESC";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] linha = new Object[]{
                    rs.getString("jogo_nome"),
                    rs.getDouble("media_notas"),
                    rs.getInt("total_avaliacoes")
                };
                resultados.add(linha);
            }
        }
        return resultados;
    }

    public static List<Object[]> mediaNotasPorJogador(Connection conexao) throws SQLException {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT jg.nickname, AVG(a.nota) AS media_notas, COUNT(a.id_avaliacao) AS total_avaliacoes " +
                     "FROM avaliacoes a " +
                     "INNER JOIN jogadores jg ON a.fk_jogador = jg.id_jogador " +
                     "GROUP BY jg.id_jogador, jg.nickname " +
                     "ORDER BY media_notas DESC";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] linha = new Object[]{
                    rs.getString("nickname"),
                    rs.getDouble("media_notas"),
                    rs.getInt("total_avaliacoes")
                };
                resultados.add(linha);
            }
        }
        return resultados;
    }

    public static List<Object[]> distribuicaoNotas(Connection conexao) throws SQLException {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT nota, COUNT(*) AS quantidade FROM avaliacoes GROUP BY nota ORDER BY nota";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] linha = new Object[]{
                    rs.getInt("nota"),
                    rs.getInt("quantidade")
                };
                resultados.add(linha);
            }
        }
        return resultados;
    }

    public static List<Object[]> jogosMaisAvaliados(Connection conexao) throws SQLException {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT j.nome AS jogo_nome, COUNT(a.id_avaliacao) AS total_avaliacoes, AVG(a.nota) AS media_notas " +
                     "FROM jogos j " +
                     "LEFT JOIN avaliacoes a ON j.id_jogo = a.fk_jogo " +
                     "GROUP BY j.id_jogo, j.nome " +
                     "HAVING COUNT(a.id_avaliacao) > 0 " +
                     "ORDER BY total_avaliacoes DESC, media_notas DESC";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] linha = new Object[]{
                    rs.getString("jogo_nome"),
                    rs.getInt("total_avaliacoes"),
                    rs.getDouble("media_notas")
                };
                resultados.add(linha);
            }
        }
        return resultados;
    }

    public static List<Object[]> avaliacoesRecentes(Connection conexao, Integer dias) throws SQLException {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT a.id_avaliacao, a.nota, j.nome AS jogo_nome, jg.nickname, a.data_avaliacao " +
                     "FROM avaliacoes a " +
                     "INNER JOIN jogos j ON a.fk_jogo = j.id_jogo " +
                     "INNER JOIN jogadores jg ON a.fk_jogador = jg.id_jogador " +
                     "WHERE a.data_avaliacao >= CURRENT_DATE - (? * INTERVAL '1 day') " +
                     "ORDER BY a.data_avaliacao DESC";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, dias);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] linha = new Object[]{
                        rs.getInt("id_avaliacao"),
                        rs.getInt("nota"),
                        rs.getString("jogo_nome"),
                        rs.getString("nickname"),
                        rs.getDate("data_avaliacao")
                    };
                    resultados.add(linha);
                }
            }
        }
        return resultados;
    }
}
