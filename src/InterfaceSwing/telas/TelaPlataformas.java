package InterfaceSwing.telas;

import app.model.Jogador;
import app.model.Plataforma;
import app.repository.JogadorRepository;
import app.repository.PlataformaRepository;
import app.validation.ValidationException;
import app.validation.Validator;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TelaPlataformas extends JFrame {
    private JTable tabelaPlataformas;
    private DefaultTableModel modeloTabela;
    private JTextField txtNome, txtHoras;
    private JComboBox<String> comboJogador;
    private JButton btnNovo, btnSalvar, btnEditar, btnDeletar, btnBuscarTodos;
    private Connection conexao;
    private PlataformaRepository plataformaRepository;
    private JogadorRepository jogadorRepository;
    private List<Plataforma> plataformasTabela;
    private JFrame telaAnterior;

    public TelaPlataformas() {
        this(null);
    }

    public TelaPlataformas(JFrame telaAnterior) {
        this.telaAnterior = telaAnterior;
        setTitle("Gerenciar Plataformas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        EstiloUI.aplicarTemaJanela(this);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                fecharConexao();
                if (TelaPlataformas.this.telaAnterior != null) {
                    TelaPlataformas.this.telaAnterior.setVisible(true);
                }
            }
        });

        try {
            conexao = Conexao.conectar();
            plataformaRepository = new PlataformaRepository(conexao);
            jogadorRepository = new JogadorRepository(conexao);
            plataformasTabela = new ArrayList<>();
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
        carregarJogadores();
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
        painel.setBorder(EstiloUI.bordaSecao("Formulário de Plataformas"));
        painel.setBackground(EstiloUI.COR_CARD);

        painel.add(new JLabel("Nome:"));
        txtNome = new JTextField();
        EstiloUI.estilizarCampo(txtNome);
        painel.add(txtNome);

        painel.add(new JLabel("Horas Jogadas:"));
        txtHoras = new JTextField();
        EstiloUI.estilizarCampo(txtHoras);
        painel.add(txtHoras);

        painel.add(new JLabel("Jogador:"));
        comboJogador = new JComboBox<>();
        EstiloUI.estilizarCombo(comboJogador);
        painel.add(comboJogador);

        return painel;
    }

    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(EstiloUI.bordaSecao("Lista de Plataformas"));
        painel.setBackground(EstiloUI.COR_CARD);

        String[] colunas = {"ID", "Nome", "Horas Jogadas", "Última Sessão", "Jogador (ID)"};
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaPlataformas = new JTable(modeloTabela);
        tabelaPlataformas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        EstiloUI.estilizarTabela(tabelaPlataformas);
        tabelaPlataformas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherFormularioComLinhaSelecionada();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabelaPlataformas);
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
        btnSalvar.addActionListener(e -> salvarPlataforma());
        btnEditar.addActionListener(e -> editarPlataforma());
        btnDeletar.addActionListener(e -> deletarPlataforma());
        btnBuscarTodos.addActionListener(e -> carregarTabela());

        return painel;
    }

    private void carregarJogadores() {
        try {
            comboJogador.removeAllItems();
            List<Jogador> jogadores = jogadorRepository.listarTodos();
            for (Jogador jogador : jogadores) {
                comboJogador.addItem(jogador.getId() + " - " + jogador.getNickname());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar jogadores: " + e.getMessage());
        }
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        try {
            plataformasTabela = plataformaRepository.listarTodas();
            for (Plataforma plataforma : plataformasTabela) {
                modeloTabela.addRow(new Object[]{
                        plataforma.getId(),
                        plataforma.getNome(),
                        plataforma.getHorasJogadas(),
                        plataforma.getUltimaSessao(),
                        plataforma.getIdJogador()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar plataformas: " + e.getMessage());
        }
    }

    private void salvarPlataforma() {
        try {
            String nome = Validator.requiredText(txtNome.getText(), "Nome");
            Integer horas = Validator.requiredInt(txtHoras.getText(), "Horas Jogadas");
            String jogadorSelecionado = (String) comboJogador.getSelectedItem();

            if (jogadorSelecionado == null || jogadorSelecionado.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione um jogador!");
                return;
            }

            Integer idJogador = Integer.parseInt(jogadorSelecionado.split(" - ")[0]);

            boolean sucesso = plataformaRepository.inserir(new Plataforma(null, nome, horas, null, idJogador));
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Plataforma salva com sucesso!");
                limparFormulario();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao salvar plataforma!");
            }
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void editarPlataforma() {
        int linha = tabelaPlataformas.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma plataforma para editar!");
            return;
        }

        try {
            Plataforma atual = plataformasTabela.get(linha);
            String nome = Validator.normalize(txtNome.getText());
            String horasTexto = Validator.normalize(txtHoras.getText());
            String jogadorSelecionado = (String) comboJogador.getSelectedItem();

            Integer horas = horasTexto.isEmpty() ? atual.getHorasJogadas() : Validator.requiredInt(horasTexto, "Horas Jogadas");
            Integer idJogador = jogadorSelecionado == null || jogadorSelecionado.trim().isEmpty()
                    ? atual.getIdJogador()
                    : Integer.parseInt(jogadorSelecionado.split(" - ")[0]);

            Plataforma plataforma = new Plataforma(
                    atual.getId(),
                    nome.isEmpty() ? atual.getNome() : nome,
                    horas,
                    atual.getUltimaSessao(),
                    idJogador
            );

            boolean sucesso = plataformaRepository.atualizar(plataforma);
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Plataforma atualizada com sucesso!");
                limparFormulario();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar plataforma!");
            }
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void deletarPlataforma() {
        int linha = tabelaPlataformas.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma plataforma para deletar!");
            return;
        }

        try {
            Integer idPlataforma = plataformasTabela.get(linha).getId();
            int opcao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja deletar?");

            if (opcao == JOptionPane.YES_OPTION) {
                boolean sucesso = plataformaRepository.deletar(idPlataforma);
                if (sucesso) {
                    JOptionPane.showMessageDialog(this, "Plataforma deletada com sucesso!");
                    carregarTabela();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao deletar plataforma!");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void limparFormulario() {
        txtNome.setText("");
        txtHoras.setText("");
    }

    private void preencherFormularioComLinhaSelecionada() {
        int linha = tabelaPlataformas.getSelectedRow();
        if (linha < 0) {
            return;
        }

        txtNome.setText(String.valueOf(modeloTabela.getValueAt(linha, 1)));
        txtHoras.setText(String.valueOf(modeloTabela.getValueAt(linha, 2)));

        Object idJogadorObj = modeloTabela.getValueAt(linha, 4);
        if (idJogadorObj != null) {
            String idJogador = String.valueOf(idJogadorObj);
            for (int i = 0; i < comboJogador.getItemCount(); i++) {
                String item = comboJogador.getItemAt(i);
                if (item.startsWith(idJogador + " - ")) {
                    comboJogador.setSelectedIndex(i);
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
            System.err.println("Erro ao fechar conexao de plataformas: " + e.getMessage());
        }
    }
}
