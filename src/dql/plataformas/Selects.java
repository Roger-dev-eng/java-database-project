package dql.plataformas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Selects {

    public static List<Object[]> listarTodas(Connection conexao) throws SQLException {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT * FROM plataformas";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] linha = new Object[]{
                    rs.getInt("id_plataforma"),
                    rs.getString("nome"),
                    rs.getInt("horas_jogadas"),
                    rs.getTimestamp("ultima_sessao"),
                    rs.getInt("fk_jogador")
                };
                resultados.add(linha);
            }
        }
        return resultados;
    }

    public static Object[] buscarPorId(Connection conexao, Integer idPlataforma) throws SQLException {
        String sql = "SELECT * FROM plataformas WHERE id_plataforma = ?";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, idPlataforma);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                        rs.getInt("id_plataforma"),
                        rs.getString("nome"),
                        rs.getInt("horas_jogadas"),
                        rs.getTimestamp("ultima_sessao"),
                        rs.getInt("fk_jogador")
                    };
                }
            }
        }
        return null;
    }

    public static List<Object[]> filtrarPorNome(Connection conexao, String nome) throws SQLException {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT * FROM plataforma WHERE nome LIKE '%?%'";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, "%" + nome + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] linha = new Object[]{
                        rs.getInt("id_plataforma"),
                        rs.getString("nome"),
                        rs.getInt("horas_jogadas"),
                        rs.getTimestamp("ultima_sessao"),
                        rs.getInt("fk_jogador")
                    };
                    resultados.add(linha);
                }
            }
        }
        return resultados;
    }
}
