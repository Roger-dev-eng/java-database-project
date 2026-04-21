package interfacesswing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.util.List;

public class TelaAvaliacoes extends JFrame {
    private JTable tabelaAvaliacoes;
    private DefaultTableModel modeloTabela;
    private JTextField txtNota, txtData;
    private JTextArea txtComentario;
    private JComboBox<String> comboJogador, comboJogo, comboStatus;
    private JButton btnNovo, btnSalvar, btnEditar, btnDeletar, btnBuscarTodos;
    private Connection conexao;

    public TelaAvaliacoes() {
        setTitle("Gerenciar Avaliações");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 650);
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
        carregarJogadores();
        carregarJogos();
        carregarTabela();
        setVisible(true);
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new GridLayout(3, 4, 10, 10));
        painel.setBorder(BorderFactory.createTitledBorder("Formulário de Avaliações"));

        painel.add(new JLabel("Jogador:"));
        comboJogador = new JComboBox<>();
        painel.add(comboJogador);

        painel.add(new JLabel("Jogo:"));
        comboJogo = new JComboBox<>();
        painel.add(comboJogo);

        painel.add(new JLabel("Nota (0-10):"));
        txtNota = new JTextField();
        painel.add(txtNota);

        painel.add(new JLabel("Status:"));
        comboStatus = new JComboBox<>(new String[]{"Pendente", "Completa", "Revisada"});
        painel.add(comboStatus);

        painel.add(new JLabel("Data (yyyy-MM-dd):"));
        txtData = new JTextField();
        painel.add(txtData);

        painel.add(new JLabel("")); // Espaço em branco

        painel.add(new JLabel("Comentário:"));
        txtComentario = new JTextArea(2, 30);
        txtComentario.setLineWrap(true);
        txtComentario.setWrapStyleWord(true);
        JScrollPane scrollComentario = new JScrollPane(txtComentario);
        painel.add(scrollComentario);

        return painel;
    }

    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Lista de Avaliações"));

        String[] colunas = {"ID", "Nota", "Comentário", "Status", "Data", "Jogador (ID)", "Jogo (ID)"};
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaAvaliacoes = new JTable(modeloTabela);
        tabelaAvaliacoes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tabelaAvaliacoes);
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

        painel.add(btnNovo);
        painel.add(btnSalvar);
        painel.add(btnEditar);
        painel.add(btnDeletar);
        painel.add(btnBuscarTodos);

        // Listeners
        btnNovo.addActionListener(e -> limparFormulario());
        btnSalvar.addActionListener(e -> salvarAvaliacao());
        btnEditar.addActionListener(e -> editarAvaliacao());
        btnDeletar.addActionListener(e -> deletarAvaliacao());
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
            List<Object[]> avaliacoes = dql.avaliacoes.Selects.listarTodos(conexao);
            for (Object[] avaliacao : avaliacoes) {
                modeloTabela.addRow(avaliacao);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar avaliações: " + e.getMessage());
        }
    }

    private void salvarAvaliacao() {
        try {
            String notaStr = txtNota.getText();
            String comentario = txtComentario.getText();
            String status = (String) comboStatus.getSelectedItem();
            String data = txtData.getText();
            String jogadorSelecionado = (String) comboJogador.getSelectedItem();
            String jogoSelecionado = (String) comboJogo.getSelectedItem();

            Integer nota = Integer.parseInt(notaStr);
            Integer idJogador = Integer.parseInt(jogadorSelecionado.split(" - ")[0]);
            Integer idJogo = Integer.parseInt(jogoSelecionado.split(" - ")[0]);

            if (notaStr.isEmpty() || status == null || data.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios!");
                return;
            }

            if (nota < 0 || nota > 10) {
                JOptionPane.showMessageDialog(this, "Nota deve estar entre 0 e 10!");
                return;
            }

            boolean sucesso = ddl.Insert.inserirAvaliacao(conexao, nota, comentario, status, data, idJogador, idJogo);
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Avaliação salva com sucesso!");
                limparFormulario();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao salvar avaliação!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nota deve ser um número entre 0 e 10!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void editarAvaliacao() {
        int linha = tabelaAvaliacoes.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma avaliação para editar!");
            return;
        }

        try {
            Integer idAvaliacao = (Integer) modeloTabela.getValueAt(linha, 0);
            String notaStr = txtNota.getText();
            String comentario = txtComentario.getText();
            String status = (String) comboStatus.getSelectedItem();
            String data = txtData.getText();
            String jogadorSelecionado = (String) comboJogador.getSelectedItem();
            String jogoSelecionado = (String) comboJogo.getSelectedItem();

            Integer nota = Integer.parseInt(notaStr);
            Integer idJogador = Integer.parseInt(jogadorSelecionado.split(" - ")[0]);
            Integer idJogo = Integer.parseInt(jogoSelecionado.split(" - ")[0]);

            if (nota < 0 || nota > 10) {
                JOptionPane.showMessageDialog(this, "Nota deve estar entre 0 e 10!");
                return;
            }

            boolean sucesso = dml.Update.atualizarAvaliacao(conexao, idAvaliacao, nota, comentario, status, data, idJogador, idJogo);
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Avaliação atualizada com sucesso!");
                limparFormulario();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar avaliação!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void deletarAvaliacao() {
        int linha = tabelaAvaliacoes.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma avaliação para deletar!");
            return;
        }

        try {
            Integer idAvaliacao = (Integer) modeloTabela.getValueAt(linha, 0);
            int opcao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja deletar?");

            if (opcao == JOptionPane.YES_OPTION) {
                boolean sucesso = dml.Delete.deletarAvaliacao(conexao, idAvaliacao);
                if (sucesso) {
                    JOptionPane.showMessageDialog(this, "Avaliação deletada com sucesso!");
                    carregarTabela();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao deletar avaliação!");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void limparFormulario() {
        txtNota.setText("");
        txtComentario.setText("");
        txtData.setText("");
        if (comboJogador.getItemCount() > 0) comboJogador.setSelectedIndex(0);
        if (comboJogo.getItemCount() > 0) comboJogo.setSelectedIndex(0);
        if (comboStatus.getItemCount() > 0) comboStatus.setSelectedIndex(0);
    }
}
