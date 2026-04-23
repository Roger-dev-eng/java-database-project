package app.service;

import java.util.List;

public class ConsultaResultado {
    private final String[] colunas;
    private final List<Object[]> linhas;

    public ConsultaResultado(String[] colunas, List<Object[]> linhas) {
        this.colunas = colunas;
        this.linhas = linhas;
    }

    public String[] getColunas() {
        return colunas;
    }

    public List<Object[]> getLinhas() {
        return linhas;
    }
}
