package InterfaceSwing;

import javax.swing.*;
import InterfaceSwing.telas.TelaAvaliacoes;
import InterfaceSwing.telas.EstiloUI;
import InterfaceSwing.telas.TelaLogin;
import InterfaceSwing.telas.TelaJogadores;
import InterfaceSwing.telas.TelaJogos;
import InterfaceSwing.telas.TelaPlataformas;
import InterfaceSwing.telas.TelaVerTabelas;

import java.awt.*;

public class MenuPrincipal extends JFrame {
    private static final Dimension TAM_BOTAO_MENU = new Dimension(150, 38);
    private static final Dimension TAM_BOTAO_SAIR = new Dimension(80, 32);
    private JPanel painelPrincipal;
    private JButton btnJogos;
    private JButton btnJogadores;
    private JButton btnPlataformas;
    private JButton btnAvaliacoes;
    private JButton btnVerTabelas;
    private JButton btnSair;

    public MenuPrincipal() {
        setTitle("Sistema de Gerenciamento de Jogos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 430);
        setLocationRelativeTo(null);
        setResizable(false);
        EstiloUI.aplicarTemaJanela(this);

        painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BorderLayout(8, 8));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        painelPrincipal.setBackground(EstiloUI.COR_FUNDO);

        JPanel painelTitulo = new JPanel();
        painelTitulo.setBackground(EstiloUI.COR_FUNDO);
        JLabel lblTitulo = new JLabel("Sistema de Gerenciamento de Jogos");
        lblTitulo.setForeground(EstiloUI.COR_TEXTO);
        lblTitulo.setFont(EstiloUI.FONTE_TITULO);
        painelTitulo.add(lblTitulo);

        JPanel painelBotoes = new JPanel(new BorderLayout(0, 8));
        painelBotoes.setBackground(EstiloUI.COR_FUNDO);

        JPanel painelGrade = new JPanel(new GridLayout(2, 2, 8, 8));
        painelGrade.setBackground(EstiloUI.COR_FUNDO);

        JPanel painelCentralizado = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        painelCentralizado.setBackground(EstiloUI.COR_FUNDO);

        btnJogos = criarBotao("Jogos", new Color(24, 91, 168));
        btnJogadores = criarBotao("Jogadores", new Color(30, 101, 183));
        btnPlataformas = criarBotao("Plataformas", new Color(37, 113, 196));
        btnAvaliacoes = criarBotao("Avaliacoes", new Color(45, 124, 204));
        btnVerTabelas = criarBotao("Ver Tabelas", new Color(20, 82, 153));

        painelGrade.add(criarPainelBotao(btnJogos));
        painelGrade.add(criarPainelBotao(btnJogadores));
        painelGrade.add(criarPainelBotao(btnPlataformas));
        painelGrade.add(criarPainelBotao(btnAvaliacoes));
        painelCentralizado.add(btnVerTabelas);

        painelBotoes.add(painelGrade, BorderLayout.CENTER);
        painelBotoes.add(painelCentralizado, BorderLayout.SOUTH);

        JPanel painelRodape = new JPanel();
        painelRodape.setBackground(EstiloUI.COR_FUNDO);
        btnSair = new JButton("Sair");
        btnSair.setFont(EstiloUI.FONTE_BOTAO);
        btnSair.setPreferredSize(TAM_BOTAO_SAIR);
        btnSair.setMinimumSize(TAM_BOTAO_SAIR);
        btnSair.setMaximumSize(TAM_BOTAO_SAIR);
        EstiloUI.estilizarBotao(btnSair, new Color(106, 125, 150));
        btnSair.addActionListener(e -> System.exit(0));
        painelRodape.add(btnSair);

        painelPrincipal.add(painelTitulo, BorderLayout.NORTH);
        painelPrincipal.add(painelBotoes, BorderLayout.CENTER);
        painelPrincipal.add(painelRodape, BorderLayout.SOUTH);

        add(painelPrincipal);

        btnJogos.addActionListener(e -> abrirTelaJogos());
        btnJogadores.addActionListener(e -> abrirTelaJogadores());
        btnPlataformas.addActionListener(e -> abrirTelaPlataformas());
        btnAvaliacoes.addActionListener(e -> abrirTelaAvaliacoes());
        btnVerTabelas.addActionListener(e -> abrirTelaVerTabelas());

        setVisible(true);
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton botao = new JButton(texto);
        EstiloUI.estilizarBotao(botao, cor);
        botao.setPreferredSize(TAM_BOTAO_MENU);
        botao.setMinimumSize(TAM_BOTAO_MENU);
        botao.setMaximumSize(TAM_BOTAO_MENU);
        return botao;
    }

    private JPanel criarPainelBotao(JButton botao) {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        painel.setBackground(EstiloUI.COR_FUNDO);
        painel.add(botao);
        return painel;
    }

    private void abrirTelaJogos() {
        new TelaJogos();
    }

    private void abrirTelaJogadores() {
        new TelaJogadores();
    }

    private void abrirTelaPlataformas() {
        new TelaPlataformas();
    }

    private void abrirTelaAvaliacoes() {
        new TelaAvaliacoes();
    }

    private void abrirTelaVerTabelas() {
        new TelaVerTabelas();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaLogin::new);
    }
}
