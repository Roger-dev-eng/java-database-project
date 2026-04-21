package InterfaceSwing.telas;

import javax.swing.*;
import java.awt.*;

public class TelaLogin extends JFrame {
    private JTextField txtNome;
    private JButton btnEntrar;

    public TelaLogin() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 280);
        setLocationRelativeTo(null);
        setResizable(false);
        EstiloUI.aplicarTemaJanela(this);

        JPanel painelPrincipal = new JPanel(new BorderLayout(12, 12));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        painelPrincipal.setBackground(EstiloUI.COR_FUNDO);

        JLabel lblTitulo = new JLabel("Login", SwingConstants.CENTER);
        lblTitulo.setFont(EstiloUI.FONTE_TITULO);
        lblTitulo.setForeground(EstiloUI.COR_TEXTO);

        JPanel painelFormulario = new JPanel(new GridLayout(2, 1, 8, 8));
        painelFormulario.setBackground(EstiloUI.COR_FUNDO);
        painelFormulario.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        txtNome = new JTextField();
        EstiloUI.estilizarCampo(txtNome);
        txtNome.setToolTipText("Digite seu nome");

        btnEntrar = new JButton("Entrar");
        EstiloUI.estilizarBotao(btnEntrar, new Color(27, 115, 173));

        painelFormulario.add(txtNome);
        painelFormulario.add(btnEntrar);

        painelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        painelPrincipal.add(painelFormulario, BorderLayout.CENTER);

        btnEntrar.addActionListener(e -> continuar());
        txtNome.addActionListener(e -> continuar());

        add(painelPrincipal);
        setVisible(true);
    }

    private void continuar() {
        String nome = txtNome.getText() == null ? "" : txtNome.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite seu nome para continuar.");
            txtNome.requestFocus();
            return;
        }

        EstiloUI.transicaoSuave(this, () -> new TelaBoasVindas(nome));
    }
}
