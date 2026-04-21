package dql.jogadores;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Joins {

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
