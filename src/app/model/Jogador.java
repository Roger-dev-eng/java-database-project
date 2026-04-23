package app.model;

public class Jogador {
    private final Integer id;
    private final String nickname;
    private final String email;
    private final Integer idJogo;

    public Jogador(Integer id, String nickname, String email, Integer idJogo) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.idJogo = idJogo;
    }

    public Integer getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public Integer getIdJogo() {
        return idJogo;
    }
}
