package app.repository;

import app.model.Jogador;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class JogadorRepository {
    private final Connection conexao;

    public JogadorRepository(Connection conexao) {
        this.conexao = conexao;
    }

    public List<Jogador> listarTodos() throws Exception {
        List<Object[]> rows = dql.jogadores.Selects.listarTodos(conexao);
        List<Jogador> jogadores = new ArrayList<>();
        for (Object[] row : rows) {
            jogadores.add(new Jogador(
                    parseInt(row[0]),
                    asString(row[1]),
                    asString(row[2]),
                    parseInt(row[3])
            ));
        }
        return jogadores;
    }

    public boolean inserir(Jogador jogador) throws Exception {
        return dml.Insert.inserirJogador(conexao, jogador.getNickname(), jogador.getEmail(), jogador.getIdJogo());
    }

    public boolean atualizar(Jogador jogador) throws Exception {
        return dml.Update.atualizarJogador(conexao, jogador.getId(), jogador.getNickname(), jogador.getEmail(), jogador.getIdJogo());
    }

    public boolean deletar(Integer id) throws Exception {
        return dml.Delete.deletarJogador(conexao, id);
    }

    private Integer parseInt(Object value) {
        return value == null ? null : Integer.parseInt(String.valueOf(value));
    }

    private String asString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
