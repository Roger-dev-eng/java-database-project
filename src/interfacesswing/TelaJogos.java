package interfacesswing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.util.List;

public class TelaJogos extends JFrame {
    private JTable tabelaJogos;
    private DefaultTableModel modeloTabela;
    private JTextField txtNome, txtAno, txtDesenvolvedora, txtGenero;
    private JButton btnNovo, btnSalvar, btnEditar, btnDeletar, btnBuscarTodos, btnFiltrarGenero;
    private Connection conexao;

    public TelaJogos() {
        setTitle("Gerenciar Jogos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        try {
            conexao = Conexao.conectar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco: " + e.getMessage());
            dispose();
            return;
        }

        // Painel principal
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel de formulário
        JPanel painelFormulario = criarPainelFormulario();

        // Painel de tabela
        JPanel painelTabela = criarPainelTabela();

        // Painel de botões
        JPanel painelBotoes = criarPainelBotoes();

        // Adicionar ao painel principal
        painelPrincipal.add(painelFormulario, BorderLayout.NORTH);
        painelPrincipal.add(painelTabela, BorderLayout.CENTER);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        add(painelPrincipal);
        carregarTabela();
        setVisible(true);
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new GridLayout(2, 4, 10, 10));
        painel.setBorder(BorderFactory.createTitledBorder("Formulário de Jogos"));

        painel.add(new JLabel("Nome:"));
        txtNome = new JTextField();
        painel.add(txtNome);

        painel.add(new JLabel("Ano Lançamento:"));
        txtAno = new JTextField();
        painel.add(txtAno);

        painel.add(new JLabel("Desenvolvedora:"));
        txtDesenvolvedora = new JTextField();
        painel.add(txtDesenvolvedora);

        painel.add(new JLabel("Gênero:"));
        txtGenero = new JTextField();
        painel.add(txtGenero);

        return painel;
    }

    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Lista de Jogos"));

        String[] colunas = {"ID", "Nome", "Ano", "Desenvolvedora", "Gênero"};
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaJogos = new JTable(modeloTabela);
        tabelaJogos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tabelaJogos);
        painel.add(scrollPane, BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarPainelBotoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        btnNovo = new JButton("Novo");
        btnSalvar = new JButton("Salvar");
        btnEditar = new JButton("Editar");
        btnDeletar = new JButton("Deletar");
        btnBuscarTodos = new JButton("Buscar Todos");
        btnFiltrarGenero = new JButton("Filtrar por Gênero");

        painel.add(btnNovo);
        painel.add(btnSalvar);
        painel.add(btnEditar);
        painel.add(btnDeletar);
        painel.add(btnBuscarTodos);
        painel.add(btnFiltrarGenero);

        // Listeners
        btnNovo.addActionListener(e -> limparFormulario());
        btnSalvar.addActionListener(e -> salvarJogo());
        btnEditar.addActionListener(e -> editarJogo());
        btnDeletar.addActionListener(e -> deletarJogo());
        btnBuscarTodos.addActionListener(e -> carregarTabela());
        btnFiltrarGenero.addActionListener(e -> filtrarPorGenero());

        return painel;
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        try {
            List<Object[]> jogos = dql.jogos.Selects.listarTodos(conexao);
            for (Object[] jogo : jogos) {
                modeloTabela.addRow(jogo);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar jogos: " + e.getMessage());
        }
    }

    private void salvarJogo() {
        try {
            String nome = txtNome.getText();
            Integer ano = Integer.parseInt(txtAno.getText());
            String desenvolvedora = txtDesenvolvedora.getText();
            String genero = txtGenero.getText();

            if (nome.isEmpty() || desenvolvedora.isEmpty() || genero.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
                return;
            }

            boolean sucesso = ddl.Insert.inserirJogo(conexao, nome, ano, desenvolvedora, genero);
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Jogo salvo com sucesso!");
                limparFormulario();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao salvar jogo!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ano deve ser um número!");
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
            Integer idJogo = (Integer) modeloTabela.getValueAt(linha, 0);
            String nome = txtNome.getText();
            Integer ano = Integer.parseInt(txtAno.getText());
            String desenvolvedora = txtDesenvolvedora.getText();
            String genero = txtGenero.getText();

            boolean sucesso = dml.Update.atualizarJogo(conexao, idJogo, nome, ano, desenvolvedora, genero);
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Jogo atualizado com sucesso!");
                limparFormulario();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar jogo!");
            }
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
            Integer idJogo = (Integer) modeloTabela.getValueAt(linha, 0);
            int opcao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja deletar?");

            if (opcao == JOptionPane.YES_OPTION) {
                boolean sucesso = dml.Delete.deletarJogo(conexao, idJogo);
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

    private void filtrarPorGenero() {
        String genero = JOptionPane.showInputDialog(this, "Digite o gênero para filtrar:");
        if (genero != null && !genero.isEmpty()) {
            try {
                modeloTabela.setRowCount(0);
                List<Object[]> jogos = dql.jogos.Selects.filtrarPorGenero(conexao, genero);
                for (Object[] jogo : jogos) {
                    modeloTabela.addRow(jogo);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao filtrar: " + e.getMessage());
            }
        }
    }

    private void limparFormulario() {
        txtNome.setText("");
        txtAno.setText("");
        txtDesenvolvedora.setText("");
        txtGenero.setText("");
    }
}
