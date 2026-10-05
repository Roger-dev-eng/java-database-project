package dql.plataformas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Joins {

    // INNER JOIN com jogadores, jogos e plataforma
    public static List<Object[]> plataformasComJogadoresJogos(Connection conexao) throws SQLException {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT p.id_plataforma, p.nome AS plataforma_nome, p.horas_jogadas, jg.nickname, j.nome AS jogo_nome, j.genero " +
                     "FROM plataformas p " +
                     "INNER JOIN jogadores jg ON p.fk_jogador = jg.id_jogador " +
                     "INNER JOIN jogos j ON jg.fk_jogo = j.id_jogo";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] linha = new Object[]{
                    rs.getInt("id_plataforma"),
                    rs.getString("plataforma_nome"),
                    rs.getInt("horas_jogadas"),
                    rs.getString("nickname"),
                    rs.getString("jogo_nome"),
                    rs.getString("genero")
                };
                resultados.add(linha);
            }
        }
        return resultados;
    }

    // JOIN - retorna as tabelas que o usuário escolher
    public static List<Object[]> joinCustomizado(Connection conexao, String sql) throws SQLException {
        List<Object[]> resultados = new ArrayList<>();
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            int colunas = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                Object[] linha = new Object[colunas];
                for (int i = 0; i < colunas; i++) {
                    linha[i] = rs.getObject(i + 1);
                }
                resultados.add(linha);
            }
        }
        return resultados;
    }
}
