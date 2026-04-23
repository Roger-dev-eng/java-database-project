package app.repository;

import app.model.Plataforma;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PlataformaRepository {
    private final Connection conexao;

    public PlataformaRepository(Connection conexao) {
        this.conexao = conexao;
    }

    public List<Plataforma> listarTodas() throws Exception {
        List<Object[]> rows = dql.plataformas.Selects.listarTodas(conexao);
        List<Plataforma> plataformas = new ArrayList<>();
        for (Object[] row : rows) {
            plataformas.add(new Plataforma(
                    parseInt(row[0]),
                    asString(row[1]),
                    parseInt(row[2]),
                    (Timestamp) row[3],
                    parseInt(row[4])
            ));
        }
        return plataformas;
    }

    public boolean inserir(Plataforma plataforma) throws Exception {
        return dml.Insert.inserirPlataforma(conexao, plataforma.getNome(), plataforma.getHorasJogadas(), plataforma.getUltimaSessao(), plataforma.getIdJogador());
    }

    public boolean atualizar(Plataforma plataforma) throws Exception {
        return dml.Update.atualizarPlataforma(conexao, plataforma.getId(), plataforma.getNome(), plataforma.getHorasJogadas(), plataforma.getUltimaSessao(), plataforma.getIdJogador());
    }

    public boolean deletar(Integer id) throws Exception {
        return dml.Delete.deletarPlataforma(conexao, id);
    }

    private Integer parseInt(Object value) {
        return value == null ? null : Integer.parseInt(String.valueOf(value));
    }

    private String asString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
