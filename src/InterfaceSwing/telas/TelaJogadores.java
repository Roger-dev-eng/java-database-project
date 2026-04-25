package InterfaceSwing.telas;

import app.model.Jogador;
import app.model.Jogo;
import app.repository.JogadorRepository;
import app.repository.JogoRepository;
import app.validation.ValidationException;
import app.validation.Validator;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TelaJogadores extends JFrame {
    private JTable tabelaJogadores;
    private DefaultTableModel modeloTabela;
    private JTextField txtNickname, txtEmail;
    private JComboBox<String> comboJogo;
    private JButton btnNovo, btnSalvar, btnEditar, btnDeletar, btnBuscarTodos;
    private Connection conexao;
    private JogadorRepository jogadorRepository;
    private JogoRepository jogoRepository;
    private List<Jogador> jogadoresTabela;
    private JFrame telaAnterior;

    public TelaJogadores() {
        this(null);
    }

    public TelaJogadores(JFrame telaAnterior) {
        this.telaAnterior = telaAnterior;
        setTitle("Gerenciar Jogadores");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        EstiloUI.aplicarTemaJanela(this);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                fecharConexao();
                if (TelaJogadores.this.telaAnterior != null) {
                    TelaJogadores.this.telaAnterior.setVisible(true);
                }
            }
        });

        try {
            conexao = Conexao.conectar();
            jogadorRepository = new JogadorRepository(conexao);
            jogoRepository = new JogoRepository(conexao);
            jogadoresTabela = new ArrayList<>();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco: " + e.getMessage());
            dispose();
            return;
        }

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painelPrincipal.setBackground(EstiloUI.COR_FUNDO);

        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        painelTopo.setBackground(EstiloUI.COR_FUNDO);
        if (this.telaAnterior != null) {
            JButton btnVoltar = new JButton("<- Voltar");
            btnVoltar.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
            btnVoltar.setForeground(new Color(190, 205, 220));
            btnVoltar.setBackground(new Color(47, 55, 66));
            btnVoltar.setFocusPainted(false);
            btnVoltar.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            btnVoltar.addActionListener(e -> voltar());
            painelTopo.add(btnVoltar);
        }

        JPanel painelFormulario = criarPainelFormulario();

        JPanel painelTabela = criarPainelTabela();

        JPanel painelBotoes = criarPainelBotoes();

        JPanel painelNorte = new JPanel(new BorderLayout(0, 8));
        painelNorte.setBackground(EstiloUI.COR_FUNDO);
        painelNorte.add(painelTopo, BorderLayout.NORTH);
        painelNorte.add(painelFormulario, BorderLayout.CENTER);

        painelPrincipal.add(painelNorte, BorderLayout.NORTH);
        painelPrincipal.add(painelTabela, BorderLayout.CENTER);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        add(painelPrincipal);
        carregarJogos();
        carregarTabela();
        setVisible(true);
    }

    private void voltar() {
        dispose();
        if (telaAnterior != null) {
            telaAnterior.setVisible(true);
        }
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new GridLayout(1, 6, 10, 10));
        painel.setBorder(EstiloUI.bordaSecao("Formulário de Jogadores"));
        painel.setBackground(EstiloUI.COR_CARD);

        painel.add(new JLabel("Nickname:"));
        txtNickname = new JTextField();
        EstiloUI.estilizarCampo(txtNickname);
        painel.add(txtNickname);

        painel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        EstiloUI.estilizarCampo(txtEmail);
        painel.add(txtEmail);

        painel.add(new JLabel("Jogo:"));
        comboJogo = new JComboBox<>();
        EstiloUI.estilizarCombo(comboJogo);
        painel.add(comboJogo);

        return painel;
    }

    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(EstiloUI.bordaSecao("Lista de Jogadores"));
        painel.setBackground(EstiloUI.COR_CARD);

        String[] colunas = {"ID", "Nickname", "Email", "Jogo (ID)"};
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaJogadores = new JTable(modeloTabela);
        tabelaJogadores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        EstiloUI.estilizarTabela(tabelaJogadores);
        tabelaJogadores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherFormularioComLinhaSelecionada();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabelaJogadores);
        painel.add(scrollPane, BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarPainelBotoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        painel.setBackground(EstiloUI.COR_FUNDO);

        btnNovo = new JButton("Novo");
        btnSalvar = new JButton("Salvar");
        btnEditar = new JButton("Editar");
        btnDeletar = new JButton("Deletar");
        btnBuscarTodos = new JButton("Buscar Todos");

        EstiloUI.estilizarBotao(btnNovo, new Color(90, 103, 117));
        EstiloUI.estilizarBotao(btnSalvar, new Color(43, 142, 95));
        EstiloUI.estilizarBotao(btnEditar, new Color(27, 115, 173));
        EstiloUI.estilizarBotao(btnDeletar, new Color(188, 72, 72));
        EstiloUI.estilizarBotao(btnBuscarTodos, new Color(120, 96, 173));

        painel.add(btnNovo);
        painel.add(btnSalvar);
        painel.add(btnEditar);
        painel.add(btnDeletar);
        painel.add(btnBuscarTodos);

        btnNovo.addActionListener(e -> limparFormulario());
        btnSalvar.addActionListener(e -> salvarJogador());
        btnEditar.addActionListener(e -> editarJogador());
        btnDeletar.addActionListener(e -> deletarJogador());
        btnBuscarTodos.addActionListener(e -> carregarTabela());

        return painel;
    }

    private void carregarJogos() {
        try {
            comboJogo.removeAllItems();
            List<Jogo> jogos = jogoRepository.listarTodos();
            for (Jogo jogo : jogos) {
                comboJogo.addItem(jogo.getId() + " - " + jogo.getNome());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar jogos: " + e.getMessage());
        }
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        try {
            jogadoresTabela = jogadorRepository.listarTodos();
            for (Jogador jogador : jogadoresTabela) {
                modeloTabela.addRow(new Object[]{
                        jogador.getId(),
                        jogador.getNickname(),
                        jogador.getEmail(),
                        jogador.getIdJogo()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar jogadores: " + e.getMessage());
        }
    }

    private void salvarJogador() {
        try {
            String nickname = Validator.requiredText(txtNickname.getText(), "Nickname");
            String email = Validator.requiredText(txtEmail.getText(), "Email");
            String jogoSelecionado = (String) comboJogo.getSelectedItem();

            if (jogoSelecionado == null || jogoSelecionado.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione um jogo!");
                return;
            }

            Integer idJogo = Integer.parseInt(jogoSelecionado.split(" - ")[0]);

            boolean sucesso = jogadorRepository.inserir(new Jogador(null, nickname, email, idJogo));
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Jogador salvo com sucesso!");
                limparFormulario();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao salvar jogador!");
            }
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void editarJogador() {
        int linha = tabelaJogadores.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um jogador para editar!");
            return;
        }

        try {
            Jogador atual = jogadoresTabela.get(linha);
            String nickname = Validator.normalize(txtNickname.getText());
            String email = Validator.normalize(txtEmail.getText());
            String jogoSelecionado = (String) comboJogo.getSelectedItem();

            Integer idJogo;
            if (jogoSelecionado == null || jogoSelecionado.trim().isEmpty()) {
                idJogo = atual.getIdJogo();
            } else {
                idJogo = Integer.parseInt(jogoSelecionado.split(" - ")[0]);
            }

            Jogador jogador = new Jogador(
                    atual.getId(),
                    nickname.isEmpty() ? atual.getNickname() : nickname,
                    email.isEmpty() ? atual.getEmail() : email,
                    idJogo
            );

            boolean sucesso = jogadorRepository.atualizar(jogador);
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Jogador atualizado com sucesso!");
                limparFormulario();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar jogador!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void deletarJogador() {
        int linha = tabelaJogadores.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um jogador para deletar!");
            return;
        }

        try {
            Integer idJogador = jogadoresTabela.get(linha).getId();
            int opcao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja deletar?");

            if (opcao == JOptionPane.YES_OPTION) {
                boolean sucesso = jogadorRepository.deletar(idJogador);
                if (sucesso) {
                    JOptionPane.showMessageDialog(this, "Jogador deletado com sucesso!");
                    carregarTabela();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao deletar jogador!");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void limparFormulario() {
        txtNickname.setText("");
        txtEmail.setText("");
    }

    private void preencherFormularioComLinhaSelecionada() {
        int linha = tabelaJogadores.getSelectedRow();
        if (linha < 0) {
            return;
        }

        txtNickname.setText(String.valueOf(modeloTabela.getValueAt(linha, 1)));
        txtEmail.setText(String.valueOf(modeloTabela.getValueAt(linha, 2)));

        Object idJogoObj = modeloTabela.getValueAt(linha, 3);
        if (idJogoObj != null) {
            String idJogo = String.valueOf(idJogoObj);
            for (int i = 0; i < comboJogo.getItemCount(); i++) {
                String item = comboJogo.getItemAt(i);
                if (item.startsWith(idJogo + " - ")) {
                    comboJogo.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void fecharConexao() {
        if (conexao == null) {
            return;
        }
        try {
            if (!conexao.isClosed()) {
                conexao.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexao de jogadores: " + e.getMessage());
        }
    }
}
