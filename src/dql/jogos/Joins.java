package dql.jogos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Joins {

    // INNER JOIN com jogadores
    public static List<Object[]> jogaresComJogos(Connection conexao) throws SQLException {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT j.id_jogo, j.nome, j.genero, jg.id_jogador, jg.nickname FROM jogos j INNER JOIN jogadores jg ON j.id_jogo = jg.fk_jogo";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] linha = new Object[]{
                    rs.getInt("id_jogo"),
                    rs.getString("nome"),
                    rs.getString("genero"),
                    rs.getInt("id_jogador"),
                    rs.getString("nickname")
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
