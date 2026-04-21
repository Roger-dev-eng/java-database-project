package InterfaceSwing.telas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.util.List;

public class TelaJogadores extends JFrame {
    private JTable tabelaJogadores;
    private DefaultTableModel modeloTabela;
    private JTextField txtNickname, txtEmail;
    private JComboBox<String> comboJogo;
    private JButton btnNovo, btnSalvar, btnEditar, btnDeletar, btnBuscarTodos;
    private Connection conexao;

    public TelaJogadores() {
        setTitle("Gerenciar Jogadores");
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
        carregarJogos();
        carregarTabela();
        setVisible(true);
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
            List<Object[]> jogos = dql.jogos.Selects.listarTodos(conexao);
            for (Object[] jogo : jogos) {
                comboJogo.addItem(jogo[0] + " - " + jogo[1]);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar jogos: " + e.getMessage());
        }
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        try {
            List<Object[]> jogadores = dql.jogadores.Selects.listarTodos(conexao);
            for (Object[] jogador : jogadores) {
                modeloTabela.addRow(jogador);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar jogadores: " + e.getMessage());
        }
    }

    private void salvarJogador() {
        try {
            String nickname = txtNickname.getText();
            String email = txtEmail.getText();
            String jogoSelecionado = (String) comboJogo.getSelectedItem();
            Integer idJogo = Integer.parseInt(jogoSelecionado.split(" - ")[0]);

            if (nickname.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
                return;
            }

            boolean sucesso = dml.Insert.inserirJogador(conexao, nickname, email, idJogo);
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Jogador salvo com sucesso!");
                limparFormulario();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao salvar jogador!");
            }
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
            Integer idJogador = (Integer) modeloTabela.getValueAt(linha, 0);
            String nickname = txtNickname.getText();
            String email = txtEmail.getText();
            String jogoSelecionado = (String) comboJogo.getSelectedItem();
            Integer idJogo = Integer.parseInt(jogoSelecionado.split(" - ")[0]);

            boolean sucesso = dml.Update.atualizarJogador(conexao, idJogador, nickname, email, idJogo);
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
            Integer idJogador = (Integer) modeloTabela.getValueAt(linha, 0);
            int opcao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja deletar?");

            if (opcao == JOptionPane.YES_OPTION) {
                boolean sucesso = dml.Delete.deletarJogador(conexao, idJogador);
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
}
