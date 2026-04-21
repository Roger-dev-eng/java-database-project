package dql.avaliacoes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Joins {

    // INNER JOIN com jogadores e jogos
    public static List<Object[]> avaliacoesComJogadoresJogos(Connection conexao) throws SQLException {
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT a.id_avaliacao, a.nota, a.comentario, a.status, jg.nickname, j.nome AS jogo_nome, j.genero " +
                     "FROM avaliacoes a " +
                     "INNER JOIN jogadores jg ON a.fk_jogador = jg.id_jogador " +
                     "INNER JOIN jogos j ON a.fk_jogo = j.id_jogo";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] linha = new Object[]{
                    rs.getInt("id_avaliacao"),
                    rs.getInt("nota"),
                    rs.getString("comentario"),
                    rs.getString("status"),
                    rs.getString("nickname"),
                    rs.getString("jogo_nome"),
                    rs.getString("genero")
                };
                resultados.add(linha);
            }
        }
        return resultados;
    }

    // JOIN genérico - retorna as tabelas que o usuário escolher
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
