package dql.jogadores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Selects {

    public static List<Object[]> listarTodos(Connection conexao) throws SQLException {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT * FROM jogadores";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] linha = new Object[]{
                    rs.getInt("id_jogador"),
                    rs.getString("nickname"),
                    rs.getString("email"),
                    rs.getInt("fk_jogo")
                };
                resultados.add(linha);
            }
        }
        return resultados;
    }

    public static Object[] buscarPorId(Connection conexao, Integer idJogador) throws SQLException {
        String sql = "SELECT * FROM jogadores WHERE id_jogador = ?";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, idJogador);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                        rs.getInt("id_jogador"),
                        rs.getString("nickname"),
                        rs.getString("email"),
                        rs.getInt("fk_jogo")
                    };
                }
            }
        }
        return null;
    }

    public static List<Object[]> buscarPorNickname(Connection conexao, String nickname) throws SQLException {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT * FROM jogadores WHERE nickname LIKE ?";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, "%" + nickname + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] linha = new Object[]{
                        rs.getInt("id_jogador"),
                        rs.getString("nickname"),
                        rs.getString("email"),
                        rs.getInt("fk_jogo")
                    };
                    resultados.add(linha);
                }
            }
        }
        return resultados;
    }
}
