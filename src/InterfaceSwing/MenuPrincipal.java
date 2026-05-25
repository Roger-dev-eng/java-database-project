package InterfaceSwing;

import InterfaceSwing.telas.DashboardLauncher;
import InterfaceSwing.telas.EstiloUI;
import InterfaceSwing.telas.TelaAvaliacoes;
import InterfaceSwing.telas.TelaJogadores;
import InterfaceSwing.telas.TelaJogos;
import InterfaceSwing.telas.TelaPlataformas;
import InterfaceSwing.telas.TelaVerTabelas;
import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class MenuPrincipal extends JFrame {
    private static final Dimension TAM_BOTAO_MENU = new Dimension(185, 42);
    private static final Color COR_SIDEBAR = new Color(24, 30, 37);
    private static final Color COR_SIDEBAR_ITEM = new Color(34, 42, 52);
    private static final Color COR_SIDEBAR_ATIVO = new Color(24, 91, 168);
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel painelConteudo = new JPanel(cardLayout);
    private final java.util.List<InterfaceSwing.telas.ConexaoFechavel> conexoes = new java.util.ArrayList<>();
    private final Map<String, JButton> botoesMenu = new LinkedHashMap<>();

    private final String nomeUsuario;

    public MenuPrincipal() {
        this("Usuario");
    }

    public MenuPrincipal(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario == null || nomeUsuario.trim().isEmpty()
                ? "Usuario"
                : nomeUsuario.trim();
        setTitle("Gerenciamento de Jogos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                fecharConexoes();
            }
        });
        setSize(1180, 720);
        setLocationRelativeTo(null);
        setResizable(false);
        EstiloUI.aplicarTemaJanela(this);

        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(EstiloUI.COR_FUNDO);

        JPanel sidebar = criarSidebar();
        JPanel conteudo = criarConteudo();

        painelPrincipal.add(sidebar, BorderLayout.WEST);
        painelPrincipal.add(conteudo, BorderLayout.CENTER);
        add(painelPrincipal);
        selecionarModulo("boasvindas");
        setVisible(true);
    }

    private JPanel criarSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(COR_SIDEBAR);

        JPanel painelLogo = new JPanel();
        painelLogo.setLayout(new BoxLayout(painelLogo, BoxLayout.Y_AXIS));
        painelLogo.setBackground(COR_SIDEBAR);
        painelLogo.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel lblTitulo = new JLabel("Gerenciamento de Jogos");
        lblTitulo.setForeground(EstiloUI.COR_TEXTO);
        lblTitulo.setFont(EstiloUI.FONTE_BOTAO);

        painelLogo.add(lblTitulo);

        JPanel painelMenu = new JPanel();
        painelMenu.setLayout(new BoxLayout(painelMenu, BoxLayout.Y_AXIS));
        painelMenu.setBackground(COR_SIDEBAR);
        painelMenu.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        adicionarItemMenu(painelMenu, "Jogos", "jogos");
        adicionarItemMenu(painelMenu, "Jogadores", "jogadores");
        adicionarItemMenu(painelMenu, "Plataformas", "plataformas");
        adicionarItemMenu(painelMenu, "Avaliacoes", "avaliacoes");
        adicionarItemMenu(painelMenu, "Analises", "analises");
        adicionarItemMenu(painelMenu, "Dashboard", "dashboard");

        JPanel painelRodape = new JPanel(new BorderLayout());
        painelRodape.setBackground(COR_SIDEBAR);
        painelRodape.setBorder(BorderFactory.createEmptyBorder(12, 12, 18, 12));

        JButton btnSair = new JButton("Sair");
        EstiloUI.estilizarBotao(btnSair, new Color(86, 98, 112));
        btnSair.addActionListener(e -> sair());
        painelRodape.add(btnSair, BorderLayout.CENTER);

        sidebar.add(painelLogo, BorderLayout.NORTH);
        sidebar.add(painelMenu, BorderLayout.CENTER);
        sidebar.add(painelRodape, BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel criarConteudo() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(EstiloUI.COR_FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        painelConteudo.setBackground(EstiloUI.COR_FUNDO);
        painelConteudo.add(criarBoasVindas(), "boasvindas");
        painelConteudo.add(registrarConexao(new TelaJogos()), "jogos");
        painelConteudo.add(registrarConexao(new TelaJogadores()), "jogadores");
        painelConteudo.add(registrarConexao(new TelaPlataformas()), "plataformas");
        painelConteudo.add(registrarConexao(new TelaAvaliacoes()), "avaliacoes");
        painelConteudo.add(registrarConexao(new TelaVerTabelas()), "analises");
        painelConteudo.add(criarPainelDashboard(), "dashboard");

        painel.add(painelConteudo, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarBoasVindas() {
        JPanel painel = new JPanel(new BorderLayout(0, 10));
        painel.setBackground(EstiloUI.COR_FUNDO);

        JLabel titulo = new JLabel("Bem-vindo, " + nomeUsuario, SwingConstants.CENTER);
        titulo.setFont(EstiloUI.FONTE_TITULO);
        titulo.setForeground(EstiloUI.COR_TEXTO);

        JLabel subtitulo = new JLabel("Selecione um modulo no menu ao lado para continuar.", SwingConstants.CENTER);
        EstiloUI.estilizarLabelSecundaria(subtitulo);

        JPanel centro = new JPanel(new GridLayout(2, 1, 8, 8));
        centro.setBackground(EstiloUI.COR_FUNDO);
        centro.add(titulo);
        centro.add(subtitulo);

        painel.add(centro, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarPainelDashboard() {
        JPanel painel = new JPanel(new BorderLayout(0, 12));
        painel.setBackground(EstiloUI.COR_FUNDO);

        JLabel titulo = new JLabel("Dashboard");
        titulo.setFont(EstiloUI.FONTE_TITULO);
        titulo.setForeground(EstiloUI.COR_TEXTO);

        JLabel subtitulo = new JLabel("Visualize o painel completo no navegador.");
        EstiloUI.estilizarLabelSecundaria(subtitulo);

        JButton btnAbrir = new JButton("Abrir Dashboard");
        EstiloUI.estilizarBotao(btnAbrir, new Color(70, 124, 58));
        btnAbrir.addActionListener(e -> DashboardLauncher.abrirDashboard(this));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(EstiloUI.COR_FUNDO);
        topo.add(titulo, BorderLayout.NORTH);
        topo.add(subtitulo, BorderLayout.CENTER);

        JPanel corpo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        corpo.setBackground(EstiloUI.COR_FUNDO);
        corpo.add(btnAbrir);

        painel.add(topo, BorderLayout.NORTH);
        painel.add(corpo, BorderLayout.CENTER);
        return painel;
    }

    private void adicionarItemMenu(JPanel painelMenu, String texto, String chave) {
        JButton botao = new JButton(texto);
        botao.setPreferredSize(TAM_BOTAO_MENU);
        botao.setMaximumSize(TAM_BOTAO_MENU);
        botao.setMinimumSize(TAM_BOTAO_MENU);
        botao.setHorizontalAlignment(SwingConstants.LEFT);
        botao.setFont(EstiloUI.FONTE_BOTAO);
        botao.setForeground(EstiloUI.COR_TEXTO);
        botao.setBackground(COR_SIDEBAR_ITEM);
        botao.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.addActionListener(e -> selecionarModulo(chave));

        painelMenu.add(botao);
        painelMenu.add(Box.createVerticalStrut(6));
        botoesMenu.put(chave, botao);
    }

    private void selecionarModulo(String chave) {
        cardLayout.show(painelConteudo, chave);
        for (Map.Entry<String, JButton> entry : botoesMenu.entrySet()) {
            boolean ativo = entry.getKey().equals(chave);
            JButton botao = entry.getValue();
            botao.setBackground(ativo ? COR_SIDEBAR_ATIVO : COR_SIDEBAR_ITEM);
            botao.setForeground(EstiloUI.COR_TEXTO);
        }
    }

    private <T extends JPanel & InterfaceSwing.telas.ConexaoFechavel> T registrarConexao(T painel) {
        conexoes.add(painel);
        return painel;
    }

    private void fecharConexoes() {
        for (InterfaceSwing.telas.ConexaoFechavel conexao : conexoes) {
            try {
                conexao.fecharConexao();
            } catch (Exception e) {
                System.err.println("Erro ao fechar conexao: " + e.getMessage());
            }
        }
    }

    private void sair() {
        fecharConexoes();
        dispose();
        System.exit(0);
    }
}
