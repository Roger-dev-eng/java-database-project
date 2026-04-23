package app.model;

import java.sql.Date;

public class Avaliacao {
    private final Integer id;
    private final Integer nota;
    private final String comentario;
    private final String status;
    private final Date dataAvaliacao;
    private final Integer idJogador;
    private final Integer idJogo;

    public Avaliacao(Integer id, Integer nota, String comentario, String status, Date dataAvaliacao, Integer idJogador, Integer idJogo) {
        this.id = id;
        this.nota = nota;
        this.comentario = comentario;
        this.status = status;
        this.dataAvaliacao = dataAvaliacao;
        this.idJogador = idJogador;
        this.idJogo = idJogo;
    }

    public Integer getId() {
        return id;
    }

    public Integer getNota() {
        return nota;
    }

    public String getComentario() {
        return comentario;
    }

    public String getStatus() {
        return status;
    }

    public Date getDataAvaliacao() {
        return dataAvaliacao;
    }

    public Integer getIdJogador() {
        return idJogador;
    }

    public Integer getIdJogo() {
        return idJogo;
    }
}
