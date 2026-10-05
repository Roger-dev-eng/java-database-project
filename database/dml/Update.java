package dml;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;

public class Update {

    public static boolean atualizarJogo(Connection conexao, Integer idJogo, String nome, 
                                        Integer anoLancamento, String desenvolvedora, 
                                        String genero) throws SQLException {
        String sql = "UPDATE jogos SET nome = ?, ano_lancamento = ?, desenvolvedora = ?, genero = ? WHERE id_jogo = ?";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.setObject(2, anoLancamento);
            pstmt.setString(3, desenvolvedora);
            pstmt.setString(4, genero);
            pstmt.setInt(5, idJogo);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public static boolean atualizarJogador(Connection conexao, Integer idJogador, String nickname, 
                                           String email, Integer fkJogo) throws SQLException {
        String sql = "UPDATE jogadores SET nickname = ?, email = ?, fk_jogo = ? WHERE id_jogador = ?";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, nickname);
            pstmt.setString(2, email);
            pstmt.setObject(3, fkJogo);
            pstmt.setInt(4, idJogador);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public static boolean atualizarPlataforma(Connection conexao, Integer idPlataforma, String nome, 
                                              Integer horasJogadas, Timestamp ultimaSessao, 
                                              Integer fkJogador) throws SQLException {
        String sql = "UPDATE plataformas SET nome = ?, horas_jogadas = ?, ultima_sessao = ?, fk_jogador = ? WHERE id_plataforma = ?";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.setObject(2, horasJogadas);
            pstmt.setTimestamp(3, ultimaSessao);
            pstmt.setObject(4, fkJogador);
            pstmt.setInt(5, idPlataforma);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public static boolean atualizarAvaliacao(Connection conexao, Integer idAvaliacao, Integer nota, 
                                             String comentario, String status, Date dataAvaliacao, 
                                             Integer fkJogador, Integer fkJogo) throws SQLException {
        String sql = "UPDATE avaliacoes SET nota = ?, comentario = ?, status = ?, data_avaliacao = ?, " +
                     "fk_jogador = ?, fk_jogo = ? WHERE id_avaliacao = ?";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, nota);
            pstmt.setString(2, comentario);
            pstmt.setString(3, status);
            pstmt.setDate(4, dataAvaliacao);
            pstmt.setObject(5, fkJogador);
            pstmt.setObject(6, fkJogo);
            pstmt.setInt(7, idAvaliacao);
            
            return pstmt.executeUpdate() > 0;
        }
    }
}
