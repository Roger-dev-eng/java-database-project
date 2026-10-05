package dql.plataformas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Agregacoes {

    public static Object[] contarTotal(Connection conexao) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM plataformas";
        
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

    public static Object[] mediaHorasJogadas(Connection conexao) throws SQLException {
        String sql = "SELECT AVG(horas_jogadas) AS media_horas_jogadas FROM plataformas";
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

    public static List<Object[]> totalHorasPorJogador(Connection conexao) throws SQLException {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT jg.id_jogador, jg.nickname, SUM(horas_jogadas) AS total_horas FROM plataformas p INNER JOIN jogadores jg ON p.fk_jogador = jg.id_jogador GROUP BY jg.id_jogador, jg.nickname";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] linha = new Object[]{
                    rs.getString("nickname"),
                    rs.getInt("total_horas")
                };
                resultados.add(linha);
            }
        }
        return resultados;
    }

}
