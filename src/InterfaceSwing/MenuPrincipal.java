package InterfaceSwing;

import javax.swing.*;
import InterfaceSwing.telas.TelaAvaliacoes;
import InterfaceSwing.telas.EstiloUI;
import InterfaceSwing.telas.TelaLogin;
import InterfaceSwing.telas.TelaJogadores;
import InterfaceSwing.telas.TelaJogos;
import InterfaceSwing.telas.TelaPlataformas;
import InterfaceSwing.telas.TelaVerTabelas;
import InterfaceSwing.telas.DashboardLauncher;

import java.awt.*;

public class MenuPrincipal extends JFrame {
    private static final Dimension TAM_BOTAO_MENU = new Dimension(175, 44);
    private static final Dimension TAM_BOTAO_SAIR = new Dimension(80, 32);
    private JPanel painelPrincipal;
    private JButton btnJogos;
    private JButton btnJogadores;
    private JButton btnPlataformas;
    private JButton btnAvaliacoes;
    private JButton btnVerTabelas;
    private JButton btnDashboard;
    private JButton btnSair;

    public MenuPrincipal() {
        setTitle("Sistema de Gerenciamento de Jogos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 430);
        setLocationRelativeTo(null);
        setResizable(false);
        EstiloUI.aplicarTemaJanela(this);

        painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BorderLayout(0, 18));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        painelPrincipal.setBackground(EstiloUI.COR_FUNDO);

        JPanel painelTitulo = new JPanel();
        painelTitulo.setLayout(new BoxLayout(painelTitulo, BoxLayout.Y_AXIS));
        painelTitulo.setBackground(EstiloUI.COR_FUNDO);

        JLabel lblTitulo = new JLabel("Sistema de Gerenciamento de Jogos");
        lblTitulo.setForeground(EstiloUI.COR_TEXTO);
        lblTitulo.setFont(EstiloUI.FONTE_TITULO);

        JLabel lblSubtitulo = new JLabel("Escolha um módulo para consultar ou gerenciar os dados.");
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        EstiloUI.estilizarLabelSecundaria(lblSubtitulo);

        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelTitulo.add(lblTitulo);
        painelTitulo.add(Box.createVerticalStrut(6));
        painelTitulo.add(lblSubtitulo);

        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new GridLayout(2, 3, 12, 12));
        painelBotoes.setBackground(EstiloUI.COR_CARD);
        painelBotoes.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstiloUI.COR_BORDA),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        btnJogos = criarBotao("Jogos", new Color(24, 91, 168));
        btnJogadores = criarBotao("Jogadores", new Color(30, 101, 183));
        btnPlataformas = criarBotao("Plataformas", new Color(37, 113, 196));
        btnAvaliacoes = criarBotao("Avaliacoes", new Color(45, 124, 204));
        btnVerTabelas = criarBotao("Ver Tabelas", new Color(20, 82, 153));
        btnDashboard = criarBotao("Dashboard", new Color(70, 124, 58));

        painelBotoes.add(btnJogos);
        painelBotoes.add(btnJogadores);
        painelBotoes.add(btnPlataformas);
        painelBotoes.add(btnAvaliacoes);
        painelBotoes.add(btnVerTabelas);
        painelBotoes.add(btnDashboard);

        JPanel painelCentro = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        painelCentro.setBackground(EstiloUI.COR_FUNDO);
        painelCentro.add(painelBotoes);

        JPanel painelRodape = new JPanel(new BorderLayout());
        painelRodape.setBackground(EstiloUI.COR_FUNDO);

        JPanel painelAcao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        painelAcao.setBackground(EstiloUI.COR_FUNDO);
        btnSair = new JButton("Sair");
        btnSair.setFont(EstiloUI.FONTE_BOTAO);
        btnSair.setPreferredSize(TAM_BOTAO_SAIR);
        btnSair.setMinimumSize(TAM_BOTAO_SAIR);
        btnSair.setMaximumSize(TAM_BOTAO_SAIR);
        EstiloUI.estilizarBotao(btnSair, new Color(106, 125, 150));
        btnSair.addActionListener(e -> System.exit(0));
        painelAcao.add(btnSair);

        painelRodape.add(painelAcao, BorderLayout.EAST);

        painelPrincipal.add(painelTitulo, BorderLayout.NORTH);
        painelPrincipal.add(painelCentro, BorderLayout.CENTER);
        painelPrincipal.add(painelRodape, BorderLayout.SOUTH);

        add(painelPrincipal);

        btnJogos.addActionListener(e -> abrirTelaJogos());
        btnJogadores.addActionListener(e -> abrirTelaJogadores());
        btnPlataformas.addActionListener(e -> abrirTelaPlataformas());
        btnAvaliacoes.addActionListener(e -> abrirTelaAvaliacoes());
        btnVerTabelas.addActionListener(e -> abrirTelaVerTabelas());
        btnDashboard.addActionListener(e -> abrirDashboard());

        setVisible(true);
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton botao = new JButton(texto);
        EstiloUI.estilizarBotao(botao, cor);
        botao.setPreferredSize(TAM_BOTAO_MENU);
        botao.setMinimumSize(TAM_BOTAO_MENU);
        botao.setMaximumSize(TAM_BOTAO_MENU);
        botao.setHorizontalAlignment(SwingConstants.CENTER);
        return botao;
    }

    private void abrirTelaJogos() {
        setVisible(false);
        new TelaJogos(this);
    }

    private void abrirTelaJogadores() {
        setVisible(false);
        new TelaJogadores(this);
    }

    private void abrirTelaPlataformas() {
        setVisible(false);
        new TelaPlataformas(this);
    }

    private void abrirTelaAvaliacoes() {
        setVisible(false);
        new TelaAvaliacoes(this);
    }

    private void abrirTelaVerTabelas() {
        setVisible(false);
        new TelaVerTabelas(this);
    }

    private void abrirDashboard() {
        DashboardLauncher.abrirDashboard(this);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaLogin::new);
    }
}
