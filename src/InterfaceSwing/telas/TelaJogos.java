package InterfaceSwing.telas;

import app.model.Jogo;
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

public class TelaJogos extends JFrame {
    private JTable tabelaJogos;
    private DefaultTableModel modeloTabela;
    private JTextField txtNome, txtAno, txtDesenvolvedora, txtGenero;
    private JButton btnNovo, btnSalvar, btnEditar, btnDeletar;
    private Connection conexao;
    private JogoRepository jogoRepository;
    private List<Jogo> jogosTabela;
    private JFrame telaAnterior;

    public TelaJogos() {
        this(null);
    }

    public TelaJogos(JFrame telaAnterior) {
        this.telaAnterior = telaAnterior;
        setTitle("Gerenciar Jogos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        EstiloUI.aplicarTemaJanela(this);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                fecharConexao();
                if (TelaJogos.this.telaAnterior != null) {
                    TelaJogos.this.telaAnterior.setVisible(true);
                }
            }
        });

        try {
            conexao = Conexao.conectar();
            jogoRepository = new JogoRepository(conexao);
            jogosTabela = new ArrayList<>();
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
        JPanel painel = new JPanel(new GridLayout(2, 4, 10, 10));
        painel.setBorder(EstiloUI.bordaSecao("Formulário de Jogos"));
        painel.setBackground(EstiloUI.COR_CARD);

        painel.add(new JLabel("Nome:"));
        txtNome = new JTextField();
        EstiloUI.estilizarCampo(txtNome);
        painel.add(txtNome);

        painel.add(new JLabel("Ano Lançamento:"));
        txtAno = new JTextField();
        EstiloUI.estilizarCampo(txtAno);
        painel.add(txtAno);

        painel.add(new JLabel("Desenvolvedora:"));
        txtDesenvolvedora = new JTextField();
        EstiloUI.estilizarCampo(txtDesenvolvedora);
        painel.add(txtDesenvolvedora);

        painel.add(new JLabel("Gênero:"));
        txtGenero = new JTextField();
        EstiloUI.estilizarCampo(txtGenero);
        painel.add(txtGenero);

        return painel;
    }

    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(EstiloUI.bordaSecao("Lista de Jogos"));
        painel.setBackground(EstiloUI.COR_CARD);

        String[] colunas = {"ID", "Nome", "Ano", "Desenvolvedora", "Gênero"};
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaJogos = new JTable(modeloTabela);
        tabelaJogos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        EstiloUI.estilizarTabela(tabelaJogos);
        tabelaJogos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherFormularioComLinhaSelecionada();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabelaJogos);
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

        EstiloUI.estilizarBotao(btnNovo, new Color(90, 103, 117));
        EstiloUI.estilizarBotao(btnSalvar, new Color(43, 142, 95));
        EstiloUI.estilizarBotao(btnEditar, new Color(27, 115, 173));
        EstiloUI.estilizarBotao(btnDeletar, new Color(188, 72, 72));

        painel.add(btnNovo);
        painel.add(btnSalvar);
        painel.add(btnEditar);
        painel.add(btnDeletar);

        btnNovo.addActionListener(e -> limparFormulario());
        btnSalvar.addActionListener(e -> salvarJogo());
        btnEditar.addActionListener(e -> editarJogo());
        btnDeletar.addActionListener(e -> deletarJogo());

        return painel;
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        try {
            jogosTabela = jogoRepository.listarTodos();
            for (Jogo jogo : jogosTabela) {
                modeloTabela.addRow(new Object[]{
                        jogo.getId(),
                        jogo.getNome(),
                        jogo.getAnoLancamento(),
                        jogo.getDesenvolvedora(),
                        jogo.getGenero()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar jogos: " + e.getMessage());
        }
    }

    private void salvarJogo() {
        try {
            Jogo jogo = new Jogo(
                    null,
                    Validator.requiredText(txtNome.getText(), "Nome"),
                    Validator.requiredInt(txtAno.getText(), "Ano Lançamento"),
                    Validator.requiredText(txtDesenvolvedora.getText(), "Desenvolvedora"),
                    Validator.requiredText(txtGenero.getText(), "Gênero")
            );

            boolean sucesso = jogoRepository.inserir(jogo);
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Jogo salvo com sucesso!");
                limparFormulario();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao salvar jogo!");
            }
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void editarJogo() {
        int linha = tabelaJogos.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um jogo para editar!");
            return;
        }

        try {
            Jogo atual = jogosTabela.get(linha);
            String nome = Validator.normalize(txtNome.getText());
            String anoTexto = Validator.normalize(txtAno.getText());
            String desenvolvedora = Validator.normalize(txtDesenvolvedora.getText());
            String genero = Validator.normalize(txtGenero.getText());

            Jogo jogo = new Jogo(
                    atual.getId(),
                    nome.isEmpty() ? atual.getNome() : nome,
                    anoTexto.isEmpty() ? atual.getAnoLancamento() : Validator.requiredInt(anoTexto, "Ano Lançamento"),
                    desenvolvedora.isEmpty() ? atual.getDesenvolvedora() : desenvolvedora,
                    genero.isEmpty() ? atual.getGenero() : genero
            );

            boolean sucesso = jogoRepository.atualizar(jogo);
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Jogo atualizado com sucesso!");
                limparFormulario();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar jogo!");
            }
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void deletarJogo() {
        int linha = tabelaJogos.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um jogo para deletar!");
            return;
        }

        try {
            Integer idJogo = jogosTabela.get(linha).getId();
            int opcao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja deletar?");

            if (opcao == JOptionPane.YES_OPTION) {
                boolean sucesso = jogoRepository.deletar(idJogo);
                if (sucesso) {
                    JOptionPane.showMessageDialog(this, "Jogo deletado com sucesso!");
                    carregarTabela();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao deletar jogo!");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void limparFormulario() {
        txtNome.setText("");
        txtAno.setText("");
        txtDesenvolvedora.setText("");
        txtGenero.setText("");
    }

    private void preencherFormularioComLinhaSelecionada() {
        int linha = tabelaJogos.getSelectedRow();
        if (linha < 0) {
            return;
        }

        txtNome.setText(String.valueOf(modeloTabela.getValueAt(linha, 1)));
        txtAno.setText(String.valueOf(modeloTabela.getValueAt(linha, 2)));
        txtDesenvolvedora.setText(String.valueOf(modeloTabela.getValueAt(linha, 3)));
        txtGenero.setText(String.valueOf(modeloTabela.getValueAt(linha, 4)));
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
            System.err.println("Erro ao fechar conexao de jogos: " + e.getMessage());
        }
    }
}
