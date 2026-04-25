package InterfaceSwing.telas;

import app.service.ConsultaResultado;
import app.service.ConsultaService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TelaVerTabelas extends JFrame {
    private JComboBox<String> comboModo;
    private JComboBox<String> comboTabelas;
    private JComboBox<String> comboConsulta;
    private JTable tabelaDados;
    private DefaultTableModel modeloTabela;
    private Connection conexao;
    private ConsultaService consultaService;
    private JFrame telaAnterior;

    public TelaVerTabelas() {
        this(null);
    }

    public TelaVerTabelas(JFrame telaAnterior) {
        this.telaAnterior = telaAnterior;
        setTitle("Ver Tabelas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(980, 620);
        setLocationRelativeTo(null);
        EstiloUI.aplicarTemaJanela(this);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                fecharConexao();
                if (TelaVerTabelas.this.telaAnterior != null) {
                    TelaVerTabelas.this.telaAnterior.setVisible(true);
                }
            }
        });

        try {
            conexao = Conexao.conectar();
            consultaService = new ConsultaService(conexao);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco: " + e.getMessage());
            dispose();
            return;
        }

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painelPrincipal.setBackground(EstiloUI.COR_FUNDO);

        JPanel painelTopo = new JPanel(new BorderLayout(10, 0));
        painelTopo.setBorder(EstiloUI.bordaSecao("Escolha a tabela"));
        painelTopo.setBackground(EstiloUI.COR_CARD);

        JPanel painelEsquerda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelEsquerda.setBackground(EstiloUI.COR_CARD);
        JPanel painelDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        painelDireita.setBackground(EstiloUI.COR_CARD);

        comboTabelas = new JComboBox<>(new String[] {"Jogos", "Jogadores", "Plataformas", "Avaliacoes"});
        EstiloUI.estilizarCombo(comboTabelas);

        comboModo = new JComboBox<>(new String[] {"Simples", "Filtros", "Joins", "Agregacoes"});
        EstiloUI.estilizarCombo(comboModo);

        comboConsulta = new JComboBox<>();
        EstiloUI.estilizarCombo(comboConsulta);

        JButton btnExecutar = new JButton("Executar");
        EstiloUI.estilizarBotao(btnExecutar, new Color(32, 113, 207));

        painelEsquerda.add(new JLabel("Tabela:"));
        painelEsquerda.add(comboTabelas);
        painelEsquerda.add(new JLabel("Modo:"));
        painelEsquerda.add(comboModo);
        painelEsquerda.add(new JLabel("Consulta:"));
        painelEsquerda.add(comboConsulta);
        painelEsquerda.add(btnExecutar);

        if (this.telaAnterior != null) {
            JButton btnVoltar = new JButton("<- Voltar");
            btnVoltar.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
            btnVoltar.setForeground(new Color(190, 205, 220));
            btnVoltar.setBackground(new Color(47, 55, 66));
            btnVoltar.setFocusPainted(false);
            btnVoltar.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            btnVoltar.addActionListener(e -> voltar());
            painelDireita.add(btnVoltar);
        }

        painelTopo.add(painelEsquerda, BorderLayout.WEST);
        painelTopo.add(painelDireita, BorderLayout.EAST);

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

        btnExecutar.addActionListener(e -> executarConsultaSelecionada());
        comboTabelas.addActionListener(e -> atualizarConsultasDisponiveis());
        comboModo.addActionListener(e -> atualizarConsultasDisponiveis());

        add(painelPrincipal);
        atualizarConsultasDisponiveis();
        executarConsultaSelecionada();
        setVisible(true);
    }

    private void voltar() {
        dispose();
        if (telaAnterior != null) {
            telaAnterior.setVisible(true);
        }
    }

    private void atualizarConsultasDisponiveis() {
        comboConsulta.removeAllItems();

        String tabela = (String) comboTabelas.getSelectedItem();
        String modo = (String) comboModo.getSelectedItem();
        if (tabela == null || modo == null) {
            return;
        }

        if ("Simples".equals(modo)) {
            comboConsulta.addItem("Listar todos");
            comboConsulta.addItem("Buscar por ID");
            return;
        }

        if ("Filtros".equals(modo)) {
            if ("Jogos".equals(tabela)) {
                comboConsulta.addItem("Filtrar por genero");
                comboConsulta.addItem("Filtrar por ano");
            } else if ("Jogadores".equals(tabela)) {
                comboConsulta.addItem("Buscar por nickname");
            } else if ("Plataformas".equals(tabela)) {
                comboConsulta.addItem("Filtrar por nome");
            } else {
                comboConsulta.addItem("Filtrar por nota");
                comboConsulta.addItem("Filtrar por status");
            }
            return;
        }

        if ("Joins".equals(modo)) {
            if ("Jogos".equals(tabela)) {
                comboConsulta.addItem("Jogos com jogadores");
            } else if ("Jogadores".equals(tabela)) {
                comboConsulta.addItem("Jogadores com jogos");
            } else if ("Plataformas".equals(tabela)) {
                comboConsulta.addItem("Plataformas com jogadores e jogos");
            } else {
                comboConsulta.addItem("Avaliacoes com jogadores e jogos");
            }
            return;
        }

        if ("Agregacoes".equals(modo)) {
            if ("Jogos".equals(tabela)) {
                comboConsulta.addItem("Contar total");
                comboConsulta.addItem("Agrupar por genero");
                comboConsulta.addItem("Jogos mais avaliados");
            } else if ("Jogadores".equals(tabela)) {
                comboConsulta.addItem("Contar total");
                comboConsulta.addItem("Contar por jogo");
            } else if ("Plataformas".equals(tabela)) {
                comboConsulta.addItem("Contar total");
                comboConsulta.addItem("Media horas jogadas");
                comboConsulta.addItem("Total horas por jogador");
            } else {
                comboConsulta.addItem("Contar total");
                comboConsulta.addItem("Media notas gerais");
                comboConsulta.addItem("Media notas por jogo");
                comboConsulta.addItem("Distribuicao de notas");
                comboConsulta.addItem("Jogos mais avaliados");
                comboConsulta.addItem("Avaliacoes recentes");
            }
        }
    }

    private void executarConsultaSelecionada() {
        String tabela = (String) comboTabelas.getSelectedItem();
        String modo = (String) comboModo.getSelectedItem();
        String consulta = (String) comboConsulta.getSelectedItem();

        if (tabela == null || modo == null || consulta == null) {
            return;
        }

        try {
            String parametro = obterParametro(tabela, modo, consulta);
            if (parametro == null && consultaExigeParametro(tabela, modo, consulta)) {
                return;
            }
            ConsultaResultado resultado = consultaService.executar(tabela, modo, consulta, parametro);
            preencherTabela(resultado.getColunas(), resultado.getLinhas());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + e.getMessage());
        }
    }

    private boolean consultaExigeParametro(String tabela, String modo, String consulta) {
        if ("Simples".equals(modo) && "Buscar por ID".equals(consulta)) {
            return true;
        }
        if ("Filtros".equals(modo)) {
            return true;
        }
        return "Agregacoes".equals(modo) && "Avaliacoes".equals(tabela) && "Avaliacoes recentes".equals(consulta);
    }

    private String obterParametro(String tabela, String modo, String consulta) {
        if (!consultaExigeParametro(tabela, modo, consulta)) {
            return "";
        }

        if ("Simples".equals(modo)) {
            return solicitarTexto("Digite o ID:");
        }

        if ("Filtros".equals(modo)) {
            if ("Jogos".equals(tabela) && "Filtrar por ano".equals(consulta)) {
                return solicitarTexto("Digite o ano:");
            }
            if ("Avaliacoes".equals(tabela) && "Filtrar por nota".equals(consulta)) {
                return solicitarTexto("Digite a nota:");
            }
            if ("Jogos".equals(tabela)) {
                return solicitarTexto("Digite o genero:");
            }
            if ("Jogadores".equals(tabela)) {
                return solicitarTexto("Digite o nickname (ou parte):");
            }
            if ("Plataformas".equals(tabela)) {
                return solicitarTexto("Digite o nome da plataforma:");
            }
            return solicitarTexto("Digite o status:");
        }

        return solicitarTexto("Ultimos quantos dias?");
    }

    private String solicitarTexto(String mensagem) {
        String valor = JOptionPane.showInputDialog(this, mensagem);
        if (valor == null) {
            return null;
        }
        valor = valor.trim();
        if (valor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Valor não pode ser vazio!");
            return null;
        }
        return valor;
    }

    private void preencherTabela(String[] colunas, List<Object[]> dados) {
        modeloTabela.setDataVector(new Object[0][0], colunas);
        for (Object[] linha : dados) {
            modeloTabela.addRow(linha);
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
            System.err.println("Erro ao fechar conexao de consultas: " + e.getMessage());
        }
    }

}
