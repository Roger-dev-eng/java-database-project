package InterfaceSwing.telas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.util.List;

public class TelaVerTabelas extends JFrame {
    private JComboBox<String> comboTabelas;
    private JTable tabelaDados;
    private DefaultTableModel modeloTabela;
    private Connection conexao;

    public TelaVerTabelas() {
        setTitle("Ver Tabelas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(980, 620);
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

        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelTopo.setBorder(EstiloUI.bordaSecao("Escolha a tabela"));
        painelTopo.setBackground(EstiloUI.COR_CARD);

        comboTabelas = new JComboBox<>(new String[] {"Jogos", "Jogadores", "Plataformas", "Avaliacoes"});
        EstiloUI.estilizarCombo(comboTabelas);

        JButton btnCarregar = new JButton("Carregar");
        EstiloUI.estilizarBotao(btnCarregar, new Color(32, 113, 207));

        painelTopo.add(new JLabel("Tabela:"));
        painelTopo.add(comboTabelas);
        painelTopo.add(btnCarregar);

        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setBorder(EstiloUI.bordaSecao("Dados"));
        painelTabela.setBackground(EstiloUI.COR_CARD);

        modeloTabela = new DefaultTableModel();
        tabelaDados = new JTable(modeloTabela);
        EstiloUI.estilizarTabela(tabelaDados);

        JScrollPane scrollPane = new JScrollPane(tabelaDados);
        painelTabela.add(scrollPane, BorderLayout.CENTER);

        painelPrincipal.add(painelTopo, BorderLayout.NORTH);
        painelPrincipal.add(painelTabela, BorderLayout.CENTER);

        btnCarregar.addActionListener(e -> carregarTabelaSelecionada());
        comboTabelas.addActionListener(e -> carregarTabelaSelecionada());

        add(painelPrincipal);
        carregarTabelaSelecionada();
        setVisible(true);
    }

    private void carregarTabelaSelecionada() {
        String tabela = (String) comboTabelas.getSelectedItem();
        if (tabela == null) {
            return;
        }

        try {
            if ("Jogos".equals(tabela)) {
                preencherTabela(new String[] {"ID", "Nome", "Ano", "Desenvolvedora", "Genero"},
                        dql.jogos.Selects.listarTodos(conexao));
            } else if ("Jogadores".equals(tabela)) {
                preencherTabela(new String[] {"ID", "Nickname", "Email", "Jogo (ID)"},
                        dql.jogadores.Selects.listarTodos(conexao));
            } else if ("Plataformas".equals(tabela)) {
                preencherTabela(new String[] {"ID", "Nome", "Horas", "Ultima Sessao", "Jogador (ID)"},
                        dql.plataformas.Selects.listarTodas(conexao));
            } else {
                preencherTabela(new String[] {"ID", "Nota", "Comentario", "Status", "Data", "Jogador (ID)", "Jogo (ID)"},
                        dql.avaliacoes.Selects.listarTodas(conexao));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + e.getMessage());
        }
    }

    private void preencherTabela(String[] colunas, List<Object[]> dados) {
        modeloTabela.setDataVector(new Object[0][0], colunas);
        for (Object[] linha : dados) {
            modeloTabela.addRow(linha);
        }
    }
}
