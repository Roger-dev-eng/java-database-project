package app.model;

import java.sql.Timestamp;

public class Plataforma {
    private final Integer id;
    private final String nome;
    private final Integer horasJogadas;
    private final Timestamp ultimaSessao;
    private final Integer idJogador;

    public Plataforma(Integer id, String nome, Integer horasJogadas, Timestamp ultimaSessao, Integer idJogador) {
        this.id = id;
        this.nome = nome;
        this.horasJogadas = horasJogadas;
        this.ultimaSessao = ultimaSessao;
        this.idJogador = idJogador;
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Integer getHorasJogadas() {
        return horasJogadas;
    }

    public Timestamp getUltimaSessao() {
        return ultimaSessao;
    }

    public Integer getIdJogador() {
        return idJogador;
    }
}
