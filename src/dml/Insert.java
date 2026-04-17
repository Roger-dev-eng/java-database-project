package dml;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;

public class Insert {

    public static boolean inserirJogo(Connection conexao, String nome, Integer anoLancamento, 
                                       String desenvolvedora, String genero) throws SQLException {
        String sql = "INSERT INTO jogos (nome, ano_lancamento, desenvolvedora, genero) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.setObject(2, anoLancamento);
            pstmt.setString(3, desenvolvedora);
            pstmt.setString(4, genero);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public static boolean inserirJogador(Connection conexao, String nickname, String email, 
                                         Integer fkJogo) throws SQLException {
        String sql = "INSERT INTO jogadores (nickname, email, fk_jogo) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, nickname);
            pstmt.setString(2, email);
            pstmt.setObject(3, fkJogo);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public static boolean inserirPlataforma(Connection conexao, String nome, Integer horasJogadas, 
                                            Timestamp ultimaSessao, Integer fkJogador) throws SQLException {
        String sql = "INSERT INTO plataformas (nome, horas_jogadas, ultima_sessao, fk_jogador) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.setObject(2, horasJogadas);
            pstmt.setTimestamp(3, ultimaSessao);
            pstmt.setObject(4, fkJogador);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public static boolean inserirAvaliacao(Connection conexao, Integer nota, String comentario, 
                                           String status, Date dataAvaliacao, Integer fkJogador, 
                                           Integer fkJogo) throws SQLException {
        String sql = "INSERT INTO avaliacoes (nota, comentario, status, data_avaliacao, fk_jogador, fk_jogo) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, nota);
            pstmt.setString(2, comentario);
            pstmt.setString(3, status);
            pstmt.setDate(4, dataAvaliacao);
            pstmt.setObject(5, fkJogador);
            pstmt.setObject(6, fkJogo);
            
            return pstmt.executeUpdate() > 0;
        }
    }
}
