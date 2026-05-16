from __future__ import annotations

import os
from typing import Dict, Iterable, Tuple

import pandas as pd
import plotly.express as px
import psycopg
import streamlit as st
from psycopg import OperationalError


st.set_page_config(
    page_title="Dashboard de Jogos",
    page_icon="🎮",
    layout="wide",
)


def obter_configuracao() -> Dict[str, str]:
    config = {
        "host": os.getenv("DB_HOST", "localhost"),
        "port": os.getenv("DB_PORT", "5432"),
        "dbname": os.getenv("DB_NAME", ""),
        "user": os.getenv("DB_USER", ""),
        "password": os.getenv("DB_PASSWORD", ""),
    }

    db_url = os.getenv("DB_URL", "").strip()
    if db_url.startswith("jdbc:postgresql://"):
        sem_prefixo = db_url.replace("jdbc:postgresql://", "", 1)
        host_porta, _, banco = sem_prefixo.partition("/")
        host, _, porta = host_porta.partition(":")
        config["host"] = host or config["host"]
        config["port"] = porta or config["port"]
        config["dbname"] = banco or config["dbname"]

    return config


def validar_configuracao(config: Dict[str, str]) -> None:
    faltando = [chave for chave in ("dbname", "user", "password") if not config.get(chave)]
    if faltando:
        raise RuntimeError(
            "Defina as variaveis de ambiente do banco antes de abrir o dashboard. "
            "Campos ausentes: " + ", ".join(faltando)
        )


def criar_conexao():
    config = obter_configuracao()
    validar_configuracao(config)
    return psycopg.connect(**config)


@st.cache_data(ttl=60, show_spinner=False)
def carregar_dataframe(sql: str, params: Tuple | None = None) -> pd.DataFrame:
    try:
        with criar_conexao() as conexao:
            return pd.read_sql_query(sql, conexao, params=params)
    except OperationalError:
        with criar_conexao() as conexao:
            return pd.read_sql_query(sql, conexao, params=params)


def clausulas_filtro(
    generos: Iterable[str],
    status: Iterable[str],
    intervalo_anos: Tuple[int, int],
) -> Tuple[str, list]:
    clausulas = []
    params: list = []

    genero_lista = list(generos)
    if genero_lista:
        clausulas.append("j.genero = ANY(%s)")
        params.append(genero_lista)

    status_lista = list(status)
    if status_lista:
        clausulas.append("a.status = ANY(%s)")
        params.append(status_lista)

    ano_inicial, ano_final = intervalo_anos
    clausulas.append("j.ano_lancamento BETWEEN %s AND %s")
    params.extend([ano_inicial, ano_final])

    return " AND ".join(clausulas), params


@st.cache_data(ttl=300, show_spinner=False)
def opcoes_genero() -> list[str]:
    # Esta consulta busca todos os generos distintos cadastrados em jogos, remove nulos,
    # agrupa os valores unicos com DISTINCT e ordena alfabeticamente em ordem crescente.
    sql = """
        SELECT DISTINCT genero
        FROM jogos
        WHERE genero IS NOT NULL AND genero <> ''
        ORDER BY genero ASC
    """
    df = carregar_dataframe(sql)
    return df["genero"].tolist()


@st.cache_data(ttl=300, show_spinner=False)
def opcoes_status() -> list[str]:
    # Esta consulta busca todos os status distintos cadastrados em avaliacoes, remove nulos,
    # agrupa os valores unicos com DISTINCT e ordena alfabeticamente em ordem crescente.
    sql = """
        SELECT DISTINCT status
        FROM avaliacoes
        WHERE status IS NOT NULL AND status <> ''
        ORDER BY status ASC
    """
    df = carregar_dataframe(sql)
    return df["status"].tolist()


@st.cache_data(ttl=300, show_spinner=False)
def intervalo_anos_disponivel() -> Tuple[int, int]:
    # Esta consulta calcula o menor e o maior ano de lancamento cadastrados em jogos,
    # usando agregacoes MIN e MAX para montar o intervalo de filtro dinamico.
    sql = """
        SELECT
            COALESCE(MIN(ano_lancamento), EXTRACT(YEAR FROM CURRENT_DATE)::int) AS ano_min,
            COALESCE(MAX(ano_lancamento), EXTRACT(YEAR FROM CURRENT_DATE)::int) AS ano_max
        FROM jogos
        WHERE ano_lancamento IS NOT NULL
    """
    df = carregar_dataframe(sql)
    return int(df.iloc[0]["ano_min"]), int(df.iloc[0]["ano_max"])


def metricas_principais(generos: Iterable[str], status: Iterable[str], intervalo_anos: Tuple[int, int]) -> Dict[str, object]:
    filtro_sql, params = clausulas_filtro(generos, status, intervalo_anos)

    # Esta consulta aplica filtros dinamicos por genero, status e faixa de ano com WHERE,
    # calcula contagens e media com COUNT e AVG, identifica a plataforma mais usada pela soma
    # de horas com SUM, e retorna os indicadores principais do dashboard.
    sql = f"""
        WITH base_filtrada AS (
            SELECT
                j.id_jogo,
                j.nome AS jogo_nome,
                j.genero,
                j.ano_lancamento,
                jg.id_jogador,
                a.id_avaliacao,
                a.nota,
                a.status,
                p.nome AS plataforma_nome,
                p.horas_jogadas
            FROM jogos j
            LEFT JOIN jogadores jg ON jg.fk_jogo = j.id_jogo
            LEFT JOIN avaliacoes a ON a.fk_jogo = j.id_jogo
            LEFT JOIN plataformas p ON p.fk_jogador = jg.id_jogador
            WHERE {filtro_sql}
        ),
        plataforma_mais_usada AS (
            SELECT
                COALESCE(plataforma_nome, 'Sem plataforma') AS plataforma,
                COALESCE(SUM(horas_jogadas), 0) AS horas_totais
            FROM base_filtrada
            GROUP BY COALESCE(plataforma_nome, 'Sem plataforma')
            ORDER BY horas_totais DESC, plataforma ASC
            LIMIT 1
        )
        SELECT
            COUNT(DISTINCT id_jogo) AS total_jogos,
            COUNT(DISTINCT id_jogador) AS total_jogadores,
            COALESCE(AVG(nota), 0) AS media_notas,
            COALESCE((SELECT plataforma FROM plataforma_mais_usada), 'Sem dados') AS plataforma_mais_usada,
            COALESCE((SELECT horas_totais FROM plataforma_mais_usada), 0) AS horas_plataforma_top
        FROM base_filtrada
    """
    df = carregar_dataframe(sql, tuple(params))
    return df.iloc[0].to_dict()


def grafico_generos(generos: Iterable[str], status: Iterable[str], intervalo_anos: Tuple[int, int]) -> pd.DataFrame:
    filtro_sql, params = clausulas_filtro(generos, status, intervalo_anos)

    # Esta consulta filtra os jogos e avaliacoes com WHERE, conta quantos jogos aparecem
    # por genero com COUNT, agrupa os resultados com GROUP BY e ordena do maior para o menor.
    sql = f"""
        SELECT
            j.genero,
            COUNT(DISTINCT j.id_jogo) AS total_jogos
        FROM jogos j
        LEFT JOIN avaliacoes a ON a.fk_jogo = j.id_jogo
        WHERE {filtro_sql}
        GROUP BY j.genero
        ORDER BY total_jogos DESC, j.genero ASC
    """
    return carregar_dataframe(sql, tuple(params))


def grafico_notas(generos: Iterable[str], status: Iterable[str], intervalo_anos: Tuple[int, int]) -> pd.DataFrame:
    filtro_sql, params = clausulas_filtro(generos, status, intervalo_anos)

    # Esta consulta filtra os registros com WHERE, conta quantas avaliacoes existem por nota
    # com COUNT, agrupa por nota com GROUP BY e ordena a distribuicao em ordem crescente.
    sql = f"""
        SELECT
            a.nota,
            COUNT(*) AS quantidade
        FROM avaliacoes a
        INNER JOIN jogos j ON j.id_jogo = a.fk_jogo
        WHERE {filtro_sql}
        GROUP BY a.nota
        ORDER BY a.nota ASC
    """
    return carregar_dataframe(sql, tuple(params))


def grafico_horas_jogador(generos: Iterable[str], status: Iterable[str], intervalo_anos: Tuple[int, int]) -> pd.DataFrame:
    filtro_sql, params = clausulas_filtro(generos, status, intervalo_anos)

    # Esta consulta filtra os dados com WHERE, soma as horas jogadas por jogador com SUM,
    # agrupa por nickname com GROUP BY e ordena do maior total para o menor.
    sql = f"""
        SELECT
            jg.nickname AS jogador,
            COALESCE(SUM(p.horas_jogadas), 0) AS total_horas
        FROM jogadores jg
        INNER JOIN jogos j ON j.id_jogo = jg.fk_jogo
        LEFT JOIN plataformas p ON p.fk_jogador = jg.id_jogador
        LEFT JOIN avaliacoes a ON a.fk_jogador = jg.id_jogador AND a.fk_jogo = j.id_jogo
        WHERE {filtro_sql}
        GROUP BY jg.id_jogador, jg.nickname
        ORDER BY total_horas DESC, jogador ASC
        LIMIT 10
    """
    return carregar_dataframe(sql, tuple(params))


def grafico_pizza_jogos_avaliados(generos: Iterable[str], status: Iterable[str], intervalo_anos: Tuple[int, int]) -> pd.DataFrame:
    filtro_sql, params = clausulas_filtro(generos, status, intervalo_anos)

    # Esta consulta filtra jogos e avaliacoes com WHERE, conta quantas avaliacoes cada jogo recebeu
    # com COUNT, agrupa por jogo com GROUP BY e ordena do mais avaliado para o menos avaliado.
    sql = f"""
        SELECT
            j.nome AS jogo,
            COUNT(a.id_avaliacao) AS total_avaliacoes
        FROM jogos j
        LEFT JOIN avaliacoes a ON a.fk_jogo = j.id_jogo
        WHERE {filtro_sql}
        GROUP BY j.id_jogo, j.nome
        HAVING COUNT(a.id_avaliacao) > 0
        ORDER BY total_avaliacoes DESC, jogo ASC
        LIMIT 8
    """
    return carregar_dataframe(sql, tuple(params))


def grafico_media_por_ano(generos: Iterable[str], status: Iterable[str], intervalo_anos: Tuple[int, int]) -> pd.DataFrame:
    filtro_sql, params = clausulas_filtro(generos, status, intervalo_anos)

    # Esta consulta filtra os jogos e avaliacoes com WHERE, calcula a media das notas por ano
    # de lancamento com AVG, agrupa os resultados com GROUP BY e ordena os anos em ordem crescente.
    sql = f"""
        SELECT
            j.ano_lancamento,
            ROUND(COALESCE(AVG(a.nota), 0)::numeric, 2) AS media_notas
        FROM jogos j
        LEFT JOIN avaliacoes a ON a.fk_jogo = j.id_jogo
        WHERE {filtro_sql}
        GROUP BY j.ano_lancamento
        ORDER BY j.ano_lancamento ASC
    """
    return carregar_dataframe(sql, tuple(params))


def grafico_dispersao_jogos(generos: Iterable[str], status: Iterable[str], intervalo_anos: Tuple[int, int]) -> pd.DataFrame:
    filtro_sql, params = clausulas_filtro(generos, status, intervalo_anos)

    # Esta consulta filtra os registros com WHERE, calcula total de avaliacoes, media, menor e maior nota
    # por jogo com COUNT, AVG, MIN e MAX, agrupa por jogo com GROUP BY e ordena pelos jogos mais avaliados.
    sql = f"""
        SELECT
            j.nome AS jogo,
            COUNT(a.id_avaliacao) AS total_avaliacoes,
            ROUND(COALESCE(AVG(a.nota), 0)::numeric, 2) AS media_notas,
            COALESCE(MIN(a.nota), 0) AS menor_nota,
            COALESCE(MAX(a.nota), 0) AS maior_nota
        FROM jogos j
        LEFT JOIN avaliacoes a ON a.fk_jogo = j.id_jogo
        WHERE {filtro_sql}
        GROUP BY j.id_jogo, j.nome
        ORDER BY total_avaliacoes DESC, media_notas DESC, jogo ASC
    """
    return carregar_dataframe(sql, tuple(params))


def tabela_top_jogos(generos: Iterable[str], status: Iterable[str], intervalo_anos: Tuple[int, int]) -> pd.DataFrame:
    filtro_sql, params = clausulas_filtro(generos, status, intervalo_anos)

    # Esta consulta filtra jogos e avaliacoes com WHERE, calcula total de avaliacoes e media de notas
    # com COUNT e AVG, agrupa por jogo com GROUP BY e ordena em ordem decrescente de relevancia.
    sql = f"""
        SELECT
            j.nome AS jogo,
            COUNT(a.id_avaliacao) AS total_avaliacoes,
            ROUND(COALESCE(AVG(a.nota), 0)::numeric, 2) AS media_notas
        FROM jogos j
        LEFT JOIN avaliacoes a ON a.fk_jogo = j.id_jogo
        WHERE {filtro_sql}
        GROUP BY j.id_jogo, j.nome
        ORDER BY total_avaliacoes DESC, media_notas DESC, jogo ASC
        LIMIT 10
    """
    return carregar_dataframe(sql, tuple(params))


def renderizar_filtros() -> Tuple[list[str], list[str], Tuple[int, int]]:
    st.sidebar.header("Filtros")

    generos = opcoes_genero()
    status = opcoes_status()
    ano_min, ano_max = intervalo_anos_disponivel()

    filtro_generos = st.sidebar.multiselect(
        "Genero",
        options=generos,
        default=generos,
    )

    filtro_status = st.sidebar.multiselect(
        "Status da avaliacao",
        options=status,
        default=status,
    )

    filtro_anos = st.sidebar.slider(
        "Faixa de ano de lancamento",
        min_value=ano_min,
        max_value=ano_max,
        value=(ano_min, ano_max),
    )

    return filtro_generos, filtro_status, filtro_anos


def renderizar_dashboard() -> None:
    st.title("Dashboard do Sistema de Jogos")
    st.caption("Visao analitica dos dados operacionais do sistema desktop.")

    filtro_generos, filtro_status, filtro_anos = renderizar_filtros()
    metricas = metricas_principais(filtro_generos, filtro_status, filtro_anos)

    col1, col2, col3, col4 = st.columns(4)
    col1.metric("Quantidade de jogos", int(metricas["total_jogos"]))
    col2.metric("Jogadores cadastrados", int(metricas["total_jogadores"]))
    col3.metric("Media geral das notas", f'{metricas["media_notas"]:.2f}')
    col4.metric("Plataforma mais usada", str(metricas["plataforma_mais_usada"]))

    col5, col6 = st.columns(2)
    with col5:
        st.subheader("Jogos por genero")
        df_generos = grafico_generos(filtro_generos, filtro_status, filtro_anos)
        if df_generos.empty:
            st.info("Nao ha dados suficientes para exibir este grafico.")
        else:
            fig_generos = px.bar(
                df_generos,
                x="genero",
                y="total_jogos",
                color="total_jogos",
                color_continuous_scale="blues",
            )
            fig_generos.update_layout(showlegend=False, xaxis_title="", yaxis_title="Jogos")
            st.plotly_chart(fig_generos, use_container_width=True)

    with col6:
        st.subheader("Jogos com mais avaliacoes")
        df_pizza = grafico_pizza_jogos_avaliados(filtro_generos, filtro_status, filtro_anos)
        if df_pizza.empty:
            st.info("Nao ha avaliacoes para montar o grafico de pizza.")
        else:
            fig_pizza = px.pie(
                df_pizza,
                names="jogo",
                values="total_avaliacoes",
                hole=0.35,
            )
            fig_pizza.update_traces(textposition="inside", textinfo="percent+label")
            st.plotly_chart(fig_pizza, use_container_width=True)

    col7, col8 = st.columns(2)
    with col7:
        st.subheader("Distribuicao das notas")
        df_notas = grafico_notas(filtro_generos, filtro_status, filtro_anos)
        if df_notas.empty:
            st.info("Nao ha notas para exibir.")
        else:
            fig_notas = px.bar(
                df_notas,
                x="nota",
                y="quantidade",
                color="quantidade",
                color_continuous_scale="tealgrn",
            )
            fig_notas.update_layout(showlegend=False, xaxis_title="Nota", yaxis_title="Quantidade")
            st.plotly_chart(fig_notas, use_container_width=True)

    with col8:
        st.subheader("Top 10 jogadores por horas")
        df_horas = grafico_horas_jogador(filtro_generos, filtro_status, filtro_anos)
        if df_horas.empty:
            st.info("Nao ha horas registradas para exibir.")
        else:
            fig_horas = px.bar(
                df_horas,
                x="total_horas",
                y="jogador",
                orientation="h",
                color="total_horas",
                color_continuous_scale="sunset",
            )
            fig_horas.update_layout(showlegend=False, xaxis_title="Horas", yaxis_title="")
            fig_horas.update_yaxes(categoryorder="total ascending")
            st.plotly_chart(fig_horas, use_container_width=True)

    col9, col10 = st.columns(2)
    with col9:
        st.subheader("Media de notas por ano de lancamento")
        df_media_ano = grafico_media_por_ano(filtro_generos, filtro_status, filtro_anos)
        if df_media_ano.empty:
            st.info("Nao ha dados suficientes para exibir a media por ano.")
        else:
            fig_media_ano = px.area(
                df_media_ano,
                x="ano_lancamento",
                y="media_notas",
                markers=True,
            )
            fig_media_ano.update_layout(xaxis_title="Ano", yaxis_title="Media")
            st.plotly_chart(fig_media_ano, use_container_width=True)

    with col10:
        st.subheader("Relacao entre media e volume de avaliacoes")
        df_dispersao = grafico_dispersao_jogos(filtro_generos, filtro_status, filtro_anos)
        df_dispersao = df_dispersao[df_dispersao["total_avaliacoes"] > 0]
        if df_dispersao.empty:
            st.info("Nao ha dados suficientes para exibir a dispersao.")
        else:
            fig_dispersao = px.scatter(
                df_dispersao,
                x="total_avaliacoes",
                y="media_notas",
                size="maior_nota",
                color="menor_nota",
                hover_name="jogo",
                color_continuous_scale="viridis",
            )
            fig_dispersao.update_layout(xaxis_title="Total de avaliacoes", yaxis_title="Media das notas")
            st.plotly_chart(fig_dispersao, use_container_width=True)

    st.subheader("Ranking de jogos por volume de avaliacoes")
    st.dataframe(
        tabela_top_jogos(filtro_generos, filtro_status, filtro_anos),
        use_container_width=True,
        hide_index=True,
    )

    st.caption(
        "Filtros ativos: "
        f"{len(filtro_generos)} generos selecionados, "
        f"{len(filtro_status)} status selecionados, "
        f"anos entre {filtro_anos[0]} e {filtro_anos[1]}."
    )


def main() -> None:
    try:
        renderizar_dashboard()
    except Exception as exc:
        st.error("Nao foi possivel carregar o dashboard.")
        st.exception(exc)
        st.info(
            "Confirme se o PostgreSQL esta ativo e se as variaveis de ambiente "
            "DB_URL, DB_USER e DB_PASSWORD estao configuradas."
        )


if __name__ == "__main__":
    main()
