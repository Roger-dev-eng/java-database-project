package InterfaceSwing.telas;

import app.model.Avaliacao;
import app.model.Jogador;
import app.model.Jogo;
import app.repository.AvaliacaoRepository;
import app.repository.JogadorRepository;
import app.repository.JogoRepository;
import app.validation.ValidationException;
import app.validation.Validator;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class TelaAvaliacoes extends JFrame {
    private JTable tabelaAvaliacoes;
    private DefaultTableModel modeloTabela;
    private JTextField txtNota, txtData;
    private JTextArea txtComentario;
    private JComboBox<String> comboJogador, comboJogo, comboStatus;
    private JButton btnNovo, btnSalvar, btnEditar, btnDeletar, btnBuscarTodos;
    private Connection conexao;
    private AvaliacaoRepository avaliacaoRepository;
    private JogadorRepository jogadorRepository;
    private JogoRepository jogoRepository;
    private List<Avaliacao> avaliacoesTabela;
    private JFrame telaAnterior;

    public TelaAvaliacoes() {
        this(null);
    }

    public TelaAvaliacoes(JFrame telaAnterior) {
        this.telaAnterior = telaAnterior;
        setTitle("Gerenciar Avaliações");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        EstiloUI.aplicarTemaJanela(this);
        if (telaAnterior != null) {
            addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    TelaAvaliacoes.this.telaAnterior.setVisible(true);
                }
            });
        }

        try {
            conexao = Conexao.conectar();
            avaliacaoRepository = new AvaliacaoRepository(conexao);
            jogadorRepository = new JogadorRepository(conexao);
            jogoRepository = new JogoRepository(conexao);
            avaliacoesTabela = new ArrayList<>();
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
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(EstiloUI.bordaSecao("Formulário de Avaliações"));
        painel.setBackground(EstiloUI.COR_CARD);

        JPanel linha1 = new JPanel(new GridLayout(1, 6, 10, 10));
        linha1.setBackground(EstiloUI.COR_CARD);

        linha1.add(new JLabel("Jogador:"));
        comboJogador = new JComboBox<>();
        EstiloUI.estilizarCombo(comboJogador);
        linha1.add(comboJogador);

        linha1.add(new JLabel("Jogo:"));
        comboJogo = new JComboBox<>();
        EstiloUI.estilizarCombo(comboJogo);
        linha1.add(comboJogo);

        linha1.add(new JLabel("Nota (0-10):"));
        txtNota = new JTextField();
        EstiloUI.estilizarCampo(txtNota);
        linha1.add(txtNota);

        JPanel linha2 = new JPanel(new GridLayout(1, 4, 10, 10));
        linha2.setBackground(EstiloUI.COR_CARD);

        linha2.add(new JLabel("Status:"));
        comboStatus = new JComboBox<>(new String[]{"Pendente", "Completa", "Revisada"});
        EstiloUI.estilizarCombo(comboStatus);
        linha2.add(comboStatus);

        linha2.add(new JLabel("Data (yyyy-MM-dd):"));
        txtData = new JTextField();
        EstiloUI.estilizarCampo(txtData);
        linha2.add(txtData);

        JPanel linha3 = new JPanel(new BorderLayout(0, 6));
        linha3.setBackground(EstiloUI.COR_CARD);

        JLabel lblComentario = new JLabel("Comentário:");
        txtComentario = new JTextArea(2, 30);
        EstiloUI.estilizarArea(txtComentario);
        txtComentario.setLineWrap(true);
        txtComentario.setWrapStyleWord(true);
        JScrollPane scrollComentario = new JScrollPane(txtComentario);
        scrollComentario.setPreferredSize(new Dimension(0, 70));

        linha3.add(lblComentario, BorderLayout.NORTH);
        linha3.add(scrollComentario, BorderLayout.CENTER);

        painel.add(linha1);
        painel.add(Box.createVerticalStrut(8));
        painel.add(linha2);
        painel.add(Box.createVerticalStrut(8));
        painel.add(linha3);

        return painel;
    }

    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(EstiloUI.bordaSecao("Lista de Avaliações"));
        painel.setBackground(EstiloUI.COR_CARD);

        String[] colunas = {"ID", "Nota", "Comentário", "Status", "Data", "Jogador (ID)", "Jogo (ID)"};
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaAvaliacoes = new JTable(modeloTabela);
        tabelaAvaliacoes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        EstiloUI.estilizarTabela(tabelaAvaliacoes);
        tabelaAvaliacoes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherFormularioComLinhaSelecionada();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabelaAvaliacoes);
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
        btnSalvar.addActionListener(e -> salvarAvaliacao());
        btnEditar.addActionListener(e -> editarAvaliacao());
        btnDeletar.addActionListener(e -> deletarAvaliacao());
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
            avaliacoesTabela = avaliacaoRepository.listarTodas();
            for (Avaliacao avaliacao : avaliacoesTabela) {
                modeloTabela.addRow(new Object[]{
                        avaliacao.getId(),
                        avaliacao.getNota(),
                        avaliacao.getComentario(),
                        avaliacao.getStatus(),
                        avaliacao.getDataAvaliacao(),
                        avaliacao.getIdJogador(),
                        avaliacao.getIdJogo()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar avaliações: " + e.getMessage());
        }
    }

    private void salvarAvaliacao() {
        try {
            Integer nota = Validator.requiredInt(txtNota.getText(), "Nota");
            Validator.rangeInclusive(nota, 0, 10, "Nota");
            String comentario = Validator.normalize(txtComentario.getText());
            String status = (String) comboStatus.getSelectedItem();
            Date dataAvaliacao = Validator.requiredDate(txtData.getText(), "Data");
            String jogadorSelecionado = (String) comboJogador.getSelectedItem();
            String jogoSelecionado = (String) comboJogo.getSelectedItem();

            if (status == null || status.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione um status!");
                return;
            }

            if (jogadorSelecionado == null || jogadorSelecionado.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione um jogador!");
                return;
            }

            if (jogoSelecionado == null || jogoSelecionado.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione um jogo!");
                return;
            }

            Integer idJogador = Integer.parseInt(jogadorSelecionado.split(" - ")[0]);
            Integer idJogo = Integer.parseInt(jogoSelecionado.split(" - ")[0]);

            boolean sucesso = avaliacaoRepository.inserir(new Avaliacao(null, nota, comentario, status, dataAvaliacao, idJogador, idJogo));
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Avaliação salva com sucesso!");
                limparFormulario();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao salvar avaliação!");
            }
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
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
            Avaliacao atual = avaliacoesTabela.get(linha);
            String notaStr = Validator.normalize(txtNota.getText());
            String comentario = Validator.normalize(txtComentario.getText());
            String status = (String) comboStatus.getSelectedItem();
            String data = Validator.normalize(txtData.getText());
            String jogadorSelecionado = (String) comboJogador.getSelectedItem();
            String jogoSelecionado = (String) comboJogo.getSelectedItem();

            if (jogadorSelecionado == null || jogadorSelecionado.trim().isEmpty()) {
                jogadorSelecionado = atual.getIdJogador() + "";
            }

            if (jogoSelecionado == null || jogoSelecionado.trim().isEmpty()) {
                jogoSelecionado = atual.getIdJogo() + "";
            }

            Integer nota = notaStr.isEmpty() ? atual.getNota() : Validator.requiredInt(notaStr, "Nota");
            Validator.rangeInclusive(nota, 0, 10, "Nota");
            Date dataAvaliacao = data.isEmpty() ? atual.getDataAvaliacao() : Validator.requiredDate(data, "Data");
            Integer idJogador = jogadorSelecionado.contains(" - ")
                    ? Integer.parseInt(jogadorSelecionado.split(" - ")[0])
                    : Integer.parseInt(jogadorSelecionado);
            Integer idJogo = jogoSelecionado.contains(" - ")
                    ? Integer.parseInt(jogoSelecionado.split(" - ")[0])
                    : Integer.parseInt(jogoSelecionado);

            Avaliacao avaliacao = new Avaliacao(
                    atual.getId(),
                    nota,
                    comentario.isEmpty() ? atual.getComentario() : comentario,
                    status == null || status.trim().isEmpty() ? atual.getStatus() : status,
                    dataAvaliacao,
                    idJogador,
                    idJogo
            );

            boolean sucesso = avaliacaoRepository.atualizar(avaliacao);
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Avaliação atualizada com sucesso!");
                limparFormulario();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar avaliação!");
            }
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
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
            Integer idAvaliacao = avaliacoesTabela.get(linha).getId();
            int opcao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja deletar?");

            if (opcao == JOptionPane.YES_OPTION) {
                boolean sucesso = avaliacaoRepository.deletar(idAvaliacao);
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

    private void preencherFormularioComLinhaSelecionada() {
        int linha = tabelaAvaliacoes.getSelectedRow();
        if (linha < 0) {
            return;
        }

        txtNota.setText(String.valueOf(modeloTabela.getValueAt(linha, 1)));
        txtComentario.setText(String.valueOf(modeloTabela.getValueAt(linha, 2)));
        comboStatus.setSelectedItem(String.valueOf(modeloTabela.getValueAt(linha, 3)));

        Object dataObj = modeloTabela.getValueAt(linha, 4);
        txtData.setText(dataObj == null ? "" : String.valueOf(dataObj));

        Object idJogadorObj = modeloTabela.getValueAt(linha, 5);
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

        Object idJogoObj = modeloTabela.getValueAt(linha, 6);
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
}
