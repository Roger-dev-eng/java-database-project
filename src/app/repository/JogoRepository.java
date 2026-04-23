package app.repository;

import app.model.Jogo;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class JogoRepository {
    private final Connection conexao;

    public JogoRepository(Connection conexao) {
        this.conexao = conexao;
    }

    public List<Jogo> listarTodos() throws Exception {
        List<Object[]> rows = dql.jogos.Selects.listarTodos(conexao);
        List<Jogo> jogos = new ArrayList<>();
        for (Object[] row : rows) {
            jogos.add(new Jogo(
                    parseInt(row[0]),
                    asString(row[1]),
                    parseInt(row[2]),
                    asString(row[3]),
                    asString(row[4])
            ));
        }
        return jogos;
    }

    public boolean inserir(Jogo jogo) throws Exception {
        return dml.Insert.inserirJogo(conexao, jogo.getNome(), jogo.getAnoLancamento(), jogo.getDesenvolvedora(), jogo.getGenero());
    }

    public boolean atualizar(Jogo jogo) throws Exception {
        return dml.Update.atualizarJogo(conexao, jogo.getId(), jogo.getNome(), jogo.getAnoLancamento(), jogo.getDesenvolvedora(), jogo.getGenero());
    }

    public boolean deletar(Integer id) throws Exception {
        return dml.Delete.deletarJogo(conexao, id);
    }

    private Integer parseInt(Object value) {
        return value == null ? null : Integer.parseInt(String.valueOf(value));
    }

    private String asString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
