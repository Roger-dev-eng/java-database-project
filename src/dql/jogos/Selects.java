package dql.jogos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Selects {

    public static List<Object[]> listarTodos(Connection conexao) throws SQLException {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT * FROM jogos";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] linha = new Object[]{
                    rs.getInt("id_jogo"),
                    rs.getString("nome"),
                    rs.getInt("ano_lancamento"),
                    rs.getString("desenvolvedora"),
                    rs.getString("genero")
                };
                resultados.add(linha);
            }
        }
        return resultados;
    }

    public static Object[] buscarPorId(Connection conexao, Integer idJogo) throws SQLException {
        String sql = "SELECT * FROM jogos WHERE id_jogo = ?";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, idJogo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                        rs.getInt("id_jogo"),
                        rs.getString("nome"),
                        rs.getInt("ano_lancamento"),
                        rs.getString("desenvolvedora"),
                        rs.getString("genero")
                    };
                }
            }
        }
        return null;
    }

    public static List<Object[]> filtrarPorGenero(Connection conexao, String genero) throws SQLException {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT * FROM jogos WHERE genero = ?";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, genero);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] linha = new Object[]{
                        rs.getInt("id_jogo"),
                        rs.getString("nome"),
                        rs.getInt("ano_lancamento"),
                        rs.getString("desenvolvedora"),
                        rs.getString("genero")
                    };
                    resultados.add(linha);
                }
            }
        }
        return resultados;
    }

    public static List<Object[]> filtrarPorAno(Connection conexao, Integer ano) throws SQLException {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT * FROM jogos WHERE ano_lancamento = ?";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, ano);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] linha = new Object[]{
                        rs.getInt("id_jogo"),
                        rs.getString("nome"),
                        rs.getInt("ano_lancamento"),
                        rs.getString("desenvolvedora"),
                        rs.getString("genero")
                    };
                    resultados.add(linha);
                }
            }
        }
        return resultados;
    }

}
