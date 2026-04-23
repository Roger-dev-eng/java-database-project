package app.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConsultaService {
    private final Connection conexao;

    public ConsultaService(Connection conexao) {
        this.conexao = conexao;
    }

    public ConsultaResultado executar(String tabela, String modo, String consulta, String parametro) throws Exception {
        if ("Simples".equals(modo)) {
            return executarSimples(tabela, consulta, parametro);
        }
        if ("Filtros".equals(modo)) {
            return executarFiltros(tabela, consulta, parametro);
        }
        if ("Joins".equals(modo)) {
            return executarJoins(tabela);
        }
        return executarAgregacoes(tabela, consulta, parametro);
    }

    private ConsultaResultado executarSimples(String tabela, String consulta, String parametro) throws Exception {
        if ("Jogos".equals(tabela)) {
            if ("Buscar por ID".equals(consulta)) {
                Object[] linha = dql.jogos.Selects.buscarPorId(conexao, Integer.parseInt(parametro));
                return linhaUnica(new String[]{"ID", "Nome", "Ano", "Desenvolvedora", "Genero"}, linha);
            }
            return resultado(new String[]{"ID", "Nome", "Ano", "Desenvolvedora", "Genero"}, dql.jogos.Selects.listarTodos(conexao));
        }
        if ("Jogadores".equals(tabela)) {
            if ("Buscar por ID".equals(consulta)) {
                Object[] linha = dql.jogadores.Selects.buscarPorId(conexao, Integer.parseInt(parametro));
                return linhaUnica(new String[]{"ID", "Nickname", "Email", "Jogo (ID)"}, linha);
            }
            return resultado(new String[]{"ID", "Nickname", "Email", "Jogo (ID)"}, dql.jogadores.Selects.listarTodos(conexao));
        }
        if ("Plataformas".equals(tabela)) {
            if ("Buscar por ID".equals(consulta)) {
                Object[] linha = dql.plataformas.Selects.buscarPorId(conexao, Integer.parseInt(parametro));
                return linhaUnica(new String[]{"ID", "Nome", "Horas", "Ultima Sessao", "Jogador (ID)"}, linha);
            }
            return resultado(new String[]{"ID", "Nome", "Horas", "Ultima Sessao", "Jogador (ID)"}, dql.plataformas.Selects.listarTodas(conexao));
        }
        if ("Buscar por ID".equals(consulta)) {
            Object[] linha = dql.avaliacoes.Selects.buscarPorId(conexao, Integer.parseInt(parametro));
            return linhaUnica(new String[]{"ID", "Nota", "Comentario", "Status", "Data", "Jogador (ID)", "Jogo (ID)"}, linha);
        }
        return resultado(new String[]{"ID", "Nota", "Comentario", "Status", "Data", "Jogador (ID)", "Jogo (ID)"}, dql.avaliacoes.Selects.listarTodas(conexao));
    }

    private ConsultaResultado executarFiltros(String tabela, String consulta, String parametro) throws Exception {
        if ("Jogos".equals(tabela)) {
            if ("Filtrar por ano".equals(consulta)) {
                return resultado(new String[]{"ID", "Nome", "Ano", "Desenvolvedora", "Genero"}, dql.jogos.Selects.filtrarPorAno(conexao, Integer.parseInt(parametro)));
            }
            return resultado(new String[]{"ID", "Nome", "Ano", "Desenvolvedora", "Genero"}, dql.jogos.Selects.filtrarPorGenero(conexao, parametro));
        }
        if ("Jogadores".equals(tabela)) {
            return resultado(new String[]{"ID", "Nickname", "Email", "Jogo (ID)"}, dql.jogadores.Selects.buscarPorNickname(conexao, parametro));
        }
        if ("Plataformas".equals(tabela)) {
            return resultado(new String[]{"ID", "Nome", "Horas", "Ultima Sessao", "Jogador (ID)"}, dql.plataformas.Selects.filtrarPorNome(conexao, parametro));
        }
        if ("Filtrar por nota".equals(consulta)) {
            return resultado(new String[]{"ID", "Nota", "Comentario", "Status", "Data", "Jogador (ID)", "Jogo (ID)"}, dql.avaliacoes.Selects.filtrarPorNota(conexao, Integer.parseInt(parametro)));
        }
        return resultado(new String[]{"ID", "Nota", "Comentario", "Status", "Data", "Jogador (ID)", "Jogo (ID)"}, dql.avaliacoes.Selects.filtrarPorStatus(conexao, parametro));
    }

    private ConsultaResultado executarJoins(String tabela) throws Exception {
        if ("Jogos".equals(tabela)) {
            return resultado(new String[]{"ID Jogo", "Nome", "Genero", "ID Jogador", "Nickname"}, dql.jogos.Joins.jogadoresComJogos(conexao));
        }
        if ("Jogadores".equals(tabela)) {
            String sql = "SELECT jg.id_jogador, jg.nickname, jg.email, j.nome AS jogo_nome FROM jogadores jg INNER JOIN jogos j ON jg.fk_jogo = j.id_jogo";
            return resultado(new String[]{"ID", "Nickname", "Email", "Jogo"}, dql.jogadores.Joins.joinCustomizado(conexao, sql));
        }
        if ("Plataformas".equals(tabela)) {
            return resultado(new String[]{"ID Plataforma", "Plataforma", "Horas", "Jogador", "Jogo", "Genero"}, dql.plataformas.Joins.plataformasComJogadoresJogos(conexao));
        }
        return resultado(new String[]{"ID", "Nota", "Comentario", "Status", "Jogador", "Jogo", "Genero"}, dql.avaliacoes.Joins.avaliacoesComJogadoresJogos(conexao));
    }

    private ConsultaResultado executarAgregacoes(String tabela, String consulta, String parametro) throws Exception {
        if ("Jogos".equals(tabela)) {
            if ("Contar total".equals(consulta)) {
                return linhaUnica(new String[]{"Total Jogos"}, dql.jogos.Agregacoes.contarTotal(conexao));
            }
            if ("Agrupar por genero".equals(consulta)) {
                return resultado(new String[]{"Genero", "Total"}, dql.jogos.Agregacoes.agruparPorGenero(conexao));
            }
            return resultado(new String[]{"Jogo", "Total Avaliacoes", "Media Notas"}, dql.jogos.Agregacoes.jogosMaisAvaliados(conexao));
        }
        if ("Jogadores".equals(tabela)) {
            if ("Contar total".equals(consulta)) {
                return linhaUnica(new String[]{"Total Jogadores"}, dql.jogadores.Agregacoes.contarTotal(conexao));
            }
            return resultado(new String[]{"Jogo", "Total Jogadores"}, dql.jogadores.Agregacoes.contarPorJogo(conexao));
        }
        if ("Plataformas".equals(tabela)) {
            if ("Contar total".equals(consulta)) {
                return linhaUnica(new String[]{"Total Plataformas"}, dql.plataformas.Agregacoes.contarTotal(conexao));
            }
            if ("Media horas jogadas".equals(consulta)) {
                return linhaUnica(new String[]{"Media Horas"}, dql.plataformas.Agregacoes.mediaHorasJogadas(conexao));
            }
            return resultado(new String[]{"Jogador", "Total Horas"}, dql.plataformas.Agregacoes.totalHorasPorJogador(conexao));
        }
        if ("Contar total".equals(consulta)) {
            return linhaUnica(new String[]{"Total Avaliacoes"}, dql.avaliacoes.Agregacoes.contarTotal(conexao));
        }
        if ("Media notas gerais".equals(consulta)) {
            return linhaUnica(new String[]{"Media Notas"}, dql.avaliacoes.Agregacoes.mediaNotasGerais(conexao));
        }
        if ("Media notas por jogo".equals(consulta)) {
            return resultado(new String[]{"Jogo", "Media", "Total"}, dql.avaliacoes.Agregacoes.mediaNotasPorJogo(conexao));
        }
        if ("Distribuicao de notas".equals(consulta)) {
            return resultado(new String[]{"Nota", "Quantidade"}, dql.avaliacoes.Agregacoes.distribuicaoNotas(conexao));
        }
        if ("Jogos mais avaliados".equals(consulta)) {
            return resultado(new String[]{"Jogo", "Total Avaliacoes", "Media"}, dql.avaliacoes.Agregacoes.jogosMaisAvaliados(conexao));
        }
        return resultado(new String[]{"ID", "Nota", "Jogo", "Jogador", "Data"}, dql.avaliacoes.Agregacoes.avaliacoesRecentes(conexao, Integer.parseInt(parametro)));
    }

    private ConsultaResultado resultado(String[] colunas, List<Object[]> linhas) {
        return new ConsultaResultado(colunas, linhas);
    }

    private ConsultaResultado linhaUnica(String[] colunas, Object[] linha) {
        if (linha == null) {
            return new ConsultaResultado(colunas, Collections.emptyList());
        }
        List<Object[]> linhas = new ArrayList<>();
        linhas.add(linha);
        return new ConsultaResultado(colunas, linhas);
    }
}
