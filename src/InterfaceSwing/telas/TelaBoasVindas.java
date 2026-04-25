package InterfaceSwing.telas;

import InterfaceSwing.MenuPrincipal;
import javax.swing.*;
import java.awt.*;

public class TelaBoasVindas extends JFrame {
    public TelaBoasVindas(String nomeUsuario) {
        setTitle("Bem-vindo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        EstiloUI.aplicarTemaJanela(this);

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        painelPrincipal.setBackground(EstiloUI.COR_FUNDO);

        JLabel lblBoasVindas = new JLabel("Bem-vindo, " + nomeUsuario + "!", SwingConstants.CENTER);
        lblBoasVindas.setFont(EstiloUI.FONTE_TITULO);
        lblBoasVindas.setForeground(EstiloUI.COR_TEXTO);

        JLabel lblDescricao = new JLabel("Clique para acessar o menu principal.", SwingConstants.CENTER);
        EstiloUI.estilizarLabelSecundaria(lblDescricao);

        JButton btnIrMenu = new JButton("Ir para o menu");
        EstiloUI.estilizarBotao(btnIrMenu, new Color(43, 142, 95));
        btnIrMenu.addActionListener(e -> abrirMenu());

        JPanel painelCentro = new JPanel(new GridLayout(2, 1, 8, 8));
        painelCentro.setBackground(EstiloUI.COR_FUNDO);
        painelCentro.add(lblBoasVindas);
        painelCentro.add(lblDescricao);

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotao.setBackground(EstiloUI.COR_FUNDO);
        painelBotao.add(btnIrMenu);

        painelPrincipal.add(painelCentro, BorderLayout.CENTER);
        painelPrincipal.add(painelBotao, BorderLayout.SOUTH);

        add(painelPrincipal);
        setVisible(true);
    }

    private void abrirMenu() {
        EstiloUI.transicaoSuave(this, MenuPrincipal::new);
    }
}
