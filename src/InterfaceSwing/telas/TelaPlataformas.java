package InterfaceSwing.telas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.util.List;

public class TelaPlataformas extends JFrame {
    private JTable tabelaPlataformas;
    private DefaultTableModel modeloTabela;
    private JTextField txtNome, txtHoras;
    private JComboBox<String> comboJogador;
    private JButton btnNovo, btnSalvar, btnEditar, btnDeletar, btnBuscarTodos;
    private Connection conexao;

    public TelaPlataformas() {
        setTitle("Gerenciar Plataformas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        EstiloUI.aplicarTemaJanela(this);

        try {
            conexao = Conexao.conectar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco: " + e.getMessage());
            dispose();
            return;
        }

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painelPrincipal.setBackground(EstiloUI.COR_FUNDO);

        JPanel painelFormulario = criarPainelFormulario();

        JPanel painelTabela = criarPainelTabela();

        JPanel painelBotoes = criarPainelBotoes();

        painelPrincipal.add(painelFormulario, BorderLayout.NORTH);
        painelPrincipal.add(painelTabela, BorderLayout.CENTER);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        add(painelPrincipal);
        carregarJogadores();
        carregarTabela();
        setVisible(true);
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
            List<Object[]> jogadores = dql.jogadores.Selects.listarTodos(conexao);
            for (Object[] jogador : jogadores) {
                comboJogador.addItem(jogador[0] + " - " + jogador[1]);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar jogadores: " + e.getMessage());
        }
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        try {
            List<Object[]> plataformas = dql.plataformas.Selects.listarTodas(conexao);
            for (Object[] plataforma : plataformas) {
                modeloTabela.addRow(plataforma);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar plataformas: " + e.getMessage());
        }
    }

    private void salvarPlataforma() {
        try {
            String nome = txtNome.getText();
            Integer horas = Integer.parseInt(txtHoras.getText());
            String jogadorSelecionado = (String) comboJogador.getSelectedItem();
            Integer idJogador = Integer.parseInt(jogadorSelecionado.split(" - ")[0]);

            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
                return;
            }

            boolean sucesso = dml.Insert.inserirPlataforma(conexao, nome, horas, null, idJogador);
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Plataforma salva com sucesso!");
                limparFormulario();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao salvar plataforma!");
            }
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
            Integer idPlataforma = (Integer) modeloTabela.getValueAt(linha, 0);
            String nome = txtNome.getText();
            Integer horas = Integer.parseInt(txtHoras.getText());
            String jogadorSelecionado = (String) comboJogador.getSelectedItem();
            Integer idJogador = Integer.parseInt(jogadorSelecionado.split(" - ")[0]);

            boolean sucesso = dml.Update.atualizarPlataforma(conexao, idPlataforma, nome, horas, null, idJogador);
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Plataforma atualizada com sucesso!");
                limparFormulario();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar plataforma!");
            }
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
            Integer idPlataforma = (Integer) modeloTabela.getValueAt(linha, 0);
            int opcao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja deletar?");

            if (opcao == JOptionPane.YES_OPTION) {
                boolean sucesso = dml.Delete.deletarPlataforma(conexao, idPlataforma);
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
}
