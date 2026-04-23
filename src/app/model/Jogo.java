package app.model;

public class Jogo {
    private final Integer id;
    private final String nome;
    private final Integer anoLancamento;
    private final String desenvolvedora;
    private final String genero;

    public Jogo(Integer id, String nome, Integer anoLancamento, String desenvolvedora, String genero) {
        this.id = id;
        this.nome = nome;
        this.anoLancamento = anoLancamento;
        this.desenvolvedora = desenvolvedora;
        this.genero = genero;
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Integer getAnoLancamento() {
        return anoLancamento;
    }

    public String getDesenvolvedora() {
        return desenvolvedora;
    }

    public String getGenero() {
        return genero;
    }
}
