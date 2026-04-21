package interfacesswing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPrincipal extends JFrame {
    private JPanel painelPrincipal;
    private JButton btnJogos;
    private JButton btnJogadores;
    private JButton btnPlataformas;
    private JButton btnAvaliacoes;
    private JButton btnSair;

    public MenuPrincipal() {
        setTitle("Sistema de Gerenciamento de Jogos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        // Painel principal
        painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        painelPrincipal.setBackground(new Color(240, 240, 240));

        // Painel de título
        JPanel painelTitulo = new JPanel();
        painelTitulo.setBackground(new Color(240, 240, 240));
        JLabel lblTitulo = new JLabel("Sistema de Gerenciamento de Jogos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        painelTitulo.add(lblTitulo);

        // Painel de botões
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new GridLayout(2, 2, 15, 15));
        painelBotoes.setBackground(new Color(240, 240, 240));

        btnJogos = criarBotao("Gerenciar Jogos", new Color(52, 152, 219));
        btnJogadores = criarBotao("Gerenciar Jogadores", new Color(46, 204, 113));
        btnPlataformas = criarBotao("Gerenciar Plataformas", new Color(155, 89, 182));
        btnAvaliacoes = criarBotao("Gerenciar Avaliações", new Color(230, 126, 34));

        painelBotoes.add(btnJogos);
        painelBotoes.add(btnJogadores);
        painelBotoes.add(btnPlataformas);
        painelBotoes.add(btnAvaliacoes);

        // Painel de rodapé
        JPanel painelRodape = new JPanel();
        painelRodape.setBackground(new Color(240, 240, 240));
        btnSair = new JButton("Sair");
        btnSair.setFont(new Font("Arial", Font.PLAIN, 12));
        btnSair.setPreferredSize(new Dimension(100, 40));
        btnSair.addActionListener(e -> System.exit(0));
        painelRodape.add(btnSair);

        // Adicionar painéis
        painelPrincipal.add(painelTitulo, BorderLayout.NORTH);
        painelPrincipal.add(painelBotoes, BorderLayout.CENTER);
        painelPrincipal.add(painelRodape, BorderLayout.SOUTH);

        add(painelPrincipal);

        // Listeners dos botões
        btnJogos.addActionListener(e -> abrirTelaJogos());
        btnJogadores.addActionListener(e -> abrirTelaJogadores());
        btnPlataformas.addActionListener(e -> abrirTelaPlataformas());
        btnAvaliacoes.addActionListener(e -> abrirTelaAvaliacoes());

        setVisible(true);
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Arial", Font.BOLD, 14));
        botao.setForeground(Color.WHITE);
        botao.setBackground(cor);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createRaisedBevelBorder());
        return botao;
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuPrincipal());
    }
}
