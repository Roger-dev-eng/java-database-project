package dql.avaliacoes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Agregacoes {

    public static Object[] contarTotal(Connection conexao) throws SQLException {
        String sql = ""; // TODO: Inserir SQL para contar total de avaliações
        
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
        String sql = ""; // TODO: Inserir SQL para calcular média de notas (AVG)
        
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
        String sql = ""; // TODO: Inserir SQL para média de notas por jogo (AVG, GROUP BY)
        
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
        String sql = ""; // TODO: Inserir SQL para média de notas por jogador (AVG, GROUP BY)
        
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
        String sql = ""; // TODO: Inserir SQL para distribuição de notas (COUNT, GROUP BY)
        
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
        String sql = ""; // TODO: Inserir SQL para listar jogos mais avaliados (COUNT, GROUP BY, HAVING, ORDER BY)
        
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
        String sql = ""; // TODO: Inserir SQL para listar avaliações dos últimos N dias
        
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
