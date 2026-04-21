package dql.avaliacoes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Selects {

    public static List<Object[]> listarTodas(Connection conexao) throws SQLException {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT * FROM avaliacoes";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] linha = new Object[]{
                    rs.getInt("id_avaliacao"),
                    rs.getInt("nota"),
                    rs.getString("comentario"),
                    rs.getString("status"),
                    rs.getDate("data_avaliacao"),
                    rs.getInt("fk_jogador"),
                    rs.getInt("fk_jogo")
                };
                resultados.add(linha);
            }
        }
        return resultados;
    }

    public static Object[] buscarPorId(Connection conexao, Integer idAvaliacao) throws SQLException {
        String sql = "SELECT * FROM avaliacoes WHERE id_avaliacao = ?";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, idAvaliacao);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                        rs.getInt("id_avaliacao"),
                        rs.getInt("nota"),
                        rs.getString("comentario"),
                        rs.getString("status"),
                        rs.getDate("data_avaliacao"),
                        rs.getInt("fk_jogador"),
                        rs.getInt("fk_jogo")
                    };
                }
            }
        }
        return null;
    }

    public static List<Object[]> filtrarPorNota(Connection conexao, Integer nota) throws SQLException {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT * FROM avaliacoes WHERE nota = ?";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, nota);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] linha = new Object[]{
                        rs.getInt("id_avaliacao"),
                        rs.getInt("nota"),
                        rs.getString("comentario"),
                        rs.getString("status"),
                        rs.getDate("data_avaliacao"),
                        rs.getInt("fk_jogador"),
                        rs.getInt("fk_jogo")
                    };
                    resultados.add(linha);
                }
            }
        }
        return resultados;
    }

    public static List<Object[]> filtrarPorStatus(Connection conexao, String status) throws SQLException {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT * FROM avaliacoes WHERE status = ?";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] linha = new Object[]{
                        rs.getInt("id_avaliacao"),
                        rs.getInt("nota"),
                        rs.getString("comentario"),
                        rs.getString("status"),
                        rs.getDate("data_avaliacao"),
                        rs.getInt("fk_jogador"),
                        rs.getInt("fk_jogo")
                    };
                    resultados.add(linha);
                }
            }
        }
        return resultados;
    }
}
