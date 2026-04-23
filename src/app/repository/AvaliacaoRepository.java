package app.repository;

import app.model.Avaliacao;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class AvaliacaoRepository {
    private final Connection conexao;

    public AvaliacaoRepository(Connection conexao) {
        this.conexao = conexao;
    }

    public List<Avaliacao> listarTodas() throws Exception {
        List<Object[]> rows = dql.avaliacoes.Selects.listarTodas(conexao);
        List<Avaliacao> avaliacoes = new ArrayList<>();
        for (Object[] row : rows) {
            avaliacoes.add(new Avaliacao(
                    parseInt(row[0]),
                    parseInt(row[1]),
                    asString(row[2]),
                    asString(row[3]),
                    (Date) row[4],
                    parseInt(row[5]),
                    parseInt(row[6])
            ));
        }
        return avaliacoes;
    }

    public boolean inserir(Avaliacao avaliacao) throws Exception {
        return dml.Insert.inserirAvaliacao(conexao, avaliacao.getNota(), avaliacao.getComentario(), avaliacao.getStatus(), avaliacao.getDataAvaliacao(), avaliacao.getIdJogador(), avaliacao.getIdJogo());
    }

    public boolean atualizar(Avaliacao avaliacao) throws Exception {
        return dml.Update.atualizarAvaliacao(conexao, avaliacao.getId(), avaliacao.getNota(), avaliacao.getComentario(), avaliacao.getStatus(), avaliacao.getDataAvaliacao(), avaliacao.getIdJogador(), avaliacao.getIdJogo());
    }

    public boolean deletar(Integer id) throws Exception {
        return dml.Delete.deletarAvaliacao(conexao, id);
    }

    private Integer parseInt(Object value) {
        return value == null ? null : Integer.parseInt(String.valueOf(value));
    }

    private String asString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
