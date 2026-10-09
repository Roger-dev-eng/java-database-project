package app.web;

import app.db.Database;
import app.model.Avaliacao;
import app.model.Jogador;
import app.model.Jogo;
import app.model.Plataforma;
import app.repository.AvaliacaoRepository;
import app.repository.JogadorRepository;
import app.repository.JogoRepository;
import app.repository.PlataformaRepository;
import app.validation.Validator;
import app.validation.ValidationException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class WebCrudService {
    public List<Map<String, Object>> listar(String recurso) throws Exception {
        try (Connection connection = Database.conectar()) {
            return switch (recurso) {
                case "jogos" -> jogos(new JogoRepository(connection).listarTodos());
                case "jogadores" -> jogadores(new JogadorRepository(connection).listarTodos());
                case "plataformas" -> plataformas(new PlataformaRepository(connection).listarTodas());
                case "avaliacoes" -> avaliacoes(new AvaliacaoRepository(connection).listarTodas());
                default -> throw new IllegalArgumentException("Recurso não encontrado.");
            };
        }
    }

    public void criar(String recurso, Map<String, String> dados) throws Exception {
        try (Connection connection = Database.conectar()) {
            switch (recurso) {
                case "jogos" -> new JogoRepository(connection).inserir(jogo(null, dados));
                case "jogadores" -> new JogadorRepository(connection).inserir(jogador(null, dados));
                case "plataformas" -> {
                    validarJogador(connection, Validator.requiredInt(dados.get("fk_jogador"), "Jogador"));
                    new PlataformaRepository(connection).inserir(plataforma(null, dados));
                }
                case "avaliacoes" -> {
                    validarJogador(connection, Validator.requiredInt(dados.get("fk_jogador"), "Jogador"));
                    validarJogo(connection, Validator.requiredInt(dados.get("fk_jogo"), "Jogo"));
                    new AvaliacaoRepository(connection).inserir(avaliacao(null, dados));
                }
                default -> throw new IllegalArgumentException("Recurso não encontrado.");
            }
        }
    }

    public void atualizar(String recurso, int id, Map<String, String> dados) throws Exception {
        try (Connection connection = Database.conectar()) {
            switch (recurso) {
                case "jogos" -> new JogoRepository(connection).atualizar(jogo(id, dados));
                case "jogadores" -> new JogadorRepository(connection).atualizar(jogador(id, dados));
                case "plataformas" -> {
                    validarJogador(connection, Validator.requiredInt(dados.get("fk_jogador"), "Jogador"));
                    new PlataformaRepository(connection).atualizar(plataforma(id, dados));
                }
                case "avaliacoes" -> atualizarAvaliacao(connection, id, dados);
                default -> throw new IllegalArgumentException("Recurso não encontrado.");
            }
        }
    }

    private void atualizarAvaliacao(Connection connection, int id, Map<String, String> dados) throws Exception {
        validarJogador(connection, Validator.requiredInt(dados.get("fk_jogador"), "Jogador"));
        validarJogo(connection, Validator.requiredInt(dados.get("fk_jogo"), "Jogo"));
        new AvaliacaoRepository(connection).atualizar(avaliacao(id, dados));
        try (PreparedStatement statement = connection.prepareStatement("CALL pr_atualizar_status_avaliacao(?, ?)")) {
            statement.setInt(1, id);
            statement.setString(2, Validator.requiredText(dados.get("status"), "Status"));
            statement.execute();
        }
    }

    public void deletar(String recurso, int id) throws Exception {
        try (Connection connection = Database.conectar()) {
            boolean removido = switch (recurso) {
                case "jogos" -> new JogoRepository(connection).deletar(id);
                case "jogadores" -> new JogadorRepository(connection).deletar(id);
                case "plataformas" -> new PlataformaRepository(connection).deletar(id);
                case "avaliacoes" -> new AvaliacaoRepository(connection).deletar(id);
                default -> throw new IllegalArgumentException("Recurso não encontrado.");
            };
            if (!removido) throw new IllegalArgumentException("Registro não encontrado.");
        }
    }

    private Jogo jogo(Integer id, Map<String, String> data) {
        Integer ano = Validator.requiredInt(data.get("ano_lancamento"), "Ano");
        Validator.rangeInclusive(ano, 0, Integer.MAX_VALUE, "Ano");
        return new Jogo(id, Validator.requiredText(data.get("nome"), "Nome"), ano, Validator.requiredText(data.get("desenvolvedora"), "Desenvolvedora"), Validator.requiredText(data.get("genero"), "Gênero"));
    }

    private Jogador jogador(Integer id, Map<String, String> data) {
        return new Jogador(id, Validator.requiredText(data.get("nickname"), "Nickname"), Validator.requiredText(data.get("email"), "E-mail"), Validator.requiredInt(data.get("fk_jogo"), "Jogo"));
    }

    private Plataforma plataforma(Integer id, Map<String, String> data) {
        Integer horas = Validator.requiredInt(data.get("horas_jogadas"), "Horas jogadas");
        Validator.rangeInclusive(horas, 0, Integer.MAX_VALUE, "Horas jogadas");
        return new Plataforma(id, Validator.requiredText(data.get("nome"), "Nome"), horas, null, Validator.requiredInt(data.get("fk_jogador"), "Jogador"));
    }

    private Avaliacao avaliacao(Integer id, Map<String, String> data) {
        Integer nota = Validator.requiredInt(data.get("nota"), "Nota");
        Validator.rangeInclusive(nota, 0, 10, "Nota");
        Date dataAvaliacao = Validator.requiredDate(data.get("data_avaliacao"), "Data");
        return new Avaliacao(id, nota, Validator.normalize(data.get("comentario")), Validator.requiredText(data.get("status"), "Status"), dataAvaliacao, Validator.requiredInt(data.get("fk_jogador"), "Jogador"), Validator.requiredInt(data.get("fk_jogo"), "Jogo"));
    }

    private List<Map<String, Object>> jogos(List<Jogo> values) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Jogo value : values) result.add(mapOf("id_jogo", value.getId(), "nome", value.getNome(), "ano_lancamento", value.getAnoLancamento(), "desenvolvedora", value.getDesenvolvedora(), "genero", value.getGenero()));
        return result;
    }

    private List<Map<String, Object>> jogadores(List<Jogador> values) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Jogador value : values) result.add(mapOf("id_jogador", value.getId(), "nickname", value.getNickname(), "email", value.getEmail(), "fk_jogo", value.getIdJogo()));
        return result;
    }

    private List<Map<String, Object>> plataformas(List<Plataforma> values) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Plataforma value : values) result.add(mapOf("id_plataforma", value.getId(), "nome", value.getNome(), "horas_jogadas", value.getHorasJogadas(), "ultima_sessao", value.getUltimaSessao(), "fk_jogador", value.getIdJogador()));
        return result;
    }

    private List<Map<String, Object>> avaliacoes(List<Avaliacao> values) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Avaliacao value : values) result.add(mapOf("id_avaliacao", value.getId(), "nota", value.getNota(), "comentario", value.getComentario(), "status", value.getStatus(), "data_avaliacao", value.getDataAvaliacao(), "fk_jogador", value.getIdJogador(), "fk_jogo", value.getIdJogo()));
        return result;
    }

    private Map<String, Object> mapOf(Object... values) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int index = 0; index < values.length; index += 2) result.put(String.valueOf(values[index]), values[index + 1]);
        return result;
    }

    private void validarJogador(Connection connection, int id) throws Exception {
        validarReferencia(connection, "jogadores", "id_jogador", id, "Jogador");
    }

    private void validarJogo(Connection connection, int id) throws Exception {
        validarReferencia(connection, "jogos", "id_jogo", id, "Jogo");
    }

    private void validarReferencia(Connection connection, String tabela, String colunaId, int id, String entidade) throws Exception {
        String sql = "SELECT 1 FROM " + tabela + " WHERE " + colunaId + " = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (var result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new ValidationException(entidade + " com ID " + id + " não encontrado. Confira o ID e cadastre esse registro antes de continuar.");
                }
            }
        }
    }
}
