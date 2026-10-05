package ddl;

public class Tabelas {

    // SQL para criar a tabela jogos
    public static final String CREATE_JOGOS = "CREATE TABLE IF NOT EXISTS jogos (" +
            "    id_jogo SERIAL PRIMARY KEY," +
            "    nome VARCHAR(100) NOT NULL," +
            "    ano_lancamento INT," +
            "    desenvolvedora VARCHAR(100)," +
            "    genero VARCHAR(50)" +
            ");";

    // SQL para criar a tabela jogadores
    public static final String CREATE_JOGADORES = "CREATE TABLE IF NOT EXISTS jogadores (" +
            "    id_jogador SERIAL PRIMARY KEY," +
            "    nickname VARCHAR(50) NOT NULL," +
            "    email VARCHAR(100) UNIQUE," +
            "    fk_jogo INT," +
            "    FOREIGN KEY (fk_jogo) REFERENCES jogos(id_jogo)" +
            ");";

    // SQL para criar a tabela plataformas
    public static final String CREATE_PLATAFORMAS = "CREATE TABLE IF NOT EXISTS plataformas (" +
            "    id_plataforma SERIAL PRIMARY KEY," +
            "    nome VARCHAR(50)," +
            "    horas_jogadas INT," +
            "    ultima_sessao TIMESTAMP," +
            "    fk_jogador INT," +
            "    FOREIGN KEY (fk_jogador) REFERENCES jogadores(id_jogador)" +
            ");";

    // SQL para criar a tabela avaliacoes
    public static final String CREATE_AVALIACOES = "CREATE TABLE IF NOT EXISTS avaliacoes (" +
            "    id_avaliacao SERIAL PRIMARY KEY," +
            "    nota INT CHECK (nota >= 0 AND nota <= 10)," +
            "    comentario TEXT," +
            "    status VARCHAR(20)," +
            "    data_avaliacao DATE," +
            "    fk_jogador INT," +
            "    fk_jogo INT," +
            "    FOREIGN KEY (fk_jogador) REFERENCES jogadores(id_jogador)," +
            "    FOREIGN KEY (fk_jogo) REFERENCES jogos(id_jogo)" +
            ");";
}
