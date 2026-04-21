package dql.jogadores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Agregacoes {

    public static Object[] contarTotal(Connection conexao) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM jogadores";
        
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

    public static List<Object[]> contarPorJogo(Connection conexao) throws SQLException {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT j.nome as jogo_nome, COUNT(jg.id_jogador) AS total_jogadores FROM jogos j INNER JOIN jogadores jg ON j.id_jogo = jg.fk_jogo GROUP BY j.id_jogo, j.nome";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] linha = new Object[]{
                    rs.getString("jogo_nome"),
                    rs.getInt("total_jogadores")
                };
                resultados.add(linha);
            }
        }
        return resultados;
    }
}
