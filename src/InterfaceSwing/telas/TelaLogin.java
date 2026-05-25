package InterfaceSwing.telas;

import InterfaceSwing.MenuPrincipal;
import javax.swing.*;
import java.awt.*;

public class TelaLogin extends JFrame {
    private JTextField txtNome;
    private JButton btnEntrar;

    public TelaLogin() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 720);
        setLocationRelativeTo(null);
        setResizable(false);
        EstiloUI.aplicarTemaJanela(this);

        JPanel painelPrincipal = new JPanel(new GridBagLayout());
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        painelPrincipal.setBackground(EstiloUI.COR_FUNDO);

        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setBackground(EstiloUI.COR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstiloUI.COR_BORDA),
                BorderFactory.createEmptyBorder(22, 26, 22, 26)
        ));

        JLabel lblTitulo = new JLabel("Login", SwingConstants.CENTER);
        lblTitulo.setFont(EstiloUI.FONTE_TITULO);
        lblTitulo.setForeground(EstiloUI.COR_TEXTO);

        JLabel lblSubtitulo = new JLabel("Digite seu nome para continuar.", SwingConstants.CENTER);
        EstiloUI.estilizarLabelSecundaria(lblSubtitulo);

        JPanel painelTopo = new JPanel(new GridLayout(2, 1, 6, 6));
        painelTopo.setBackground(EstiloUI.COR_CARD);
        painelTopo.add(lblTitulo);
        painelTopo.add(lblSubtitulo);

        JPanel painelFormulario = new JPanel(new GridLayout(2, 1, 10, 10));
        painelFormulario.setBackground(EstiloUI.COR_CARD);

        txtNome = new JTextField();
        EstiloUI.estilizarCampo(txtNome);
        txtNome.setToolTipText("Digite seu nome");

        btnEntrar = new JButton("Entrar");
        EstiloUI.estilizarBotao(btnEntrar, new Color(27, 115, 173));

        painelFormulario.add(txtNome);
        painelFormulario.add(btnEntrar);

        card.add(painelTopo, BorderLayout.NORTH);
        card.add(painelFormulario, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        painelPrincipal.add(card, gbc);

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

        EstiloUI.transicaoSuave(this, () -> new MenuPrincipal(nome));
    }
}
