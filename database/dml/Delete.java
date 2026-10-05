package dml;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Delete {

    public static boolean deletarJogo(Connection conexao, Integer idJogo) throws SQLException {
        String sql = "DELETE FROM jogos WHERE id_jogo = ?";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, idJogo);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public static boolean deletarJogador(Connection conexao, Integer idJogador) throws SQLException {
        String sql = "DELETE FROM jogadores WHERE id_jogador = ?";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, idJogador);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public static boolean deletarPlataforma(Connection conexao, Integer idPlataforma) throws SQLException {
        String sql = "DELETE FROM plataformas WHERE id_plataforma = ?";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, idPlataforma);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public static boolean deletarAvaliacao(Connection conexao, Integer idAvaliacao) throws SQLException {
        String sql = "DELETE FROM avaliacoes WHERE id_avaliacao = ?";
        
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, idAvaliacao);
            
            return pstmt.executeUpdate() > 0;
        }
    }
}
