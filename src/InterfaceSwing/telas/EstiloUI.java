package InterfaceSwing.telas;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.util.function.Supplier;

public final class EstiloUI {
    public static final Color COR_FUNDO = new Color(18, 22, 28);
    public static final Color COR_CARD = new Color(30, 36, 44);
    public static final Color COR_TEXTO = new Color(233, 238, 245);
    public static final Color COR_TEXTO_SEC = new Color(162, 174, 189);
    public static final Color COR_BORDA = new Color(62, 74, 90);
    public static final Color COR_INPUT = new Color(24, 30, 37);
    public static final Font FONTE_TITULO = new Font("Trebuchet MS", Font.BOLD, 26);
    public static final Font FONTE_PADRAO = new Font("Trebuchet MS", Font.PLAIN, 14);
    public static final Font FONTE_BOTAO = new Font("Trebuchet MS", Font.BOLD, 14);

    private EstiloUI() {
    }

    public static void aplicarTemaJanela(JFrame frame) {
        frame.getContentPane().setBackground(COR_FUNDO);
        UIManager.put("Panel.background", COR_FUNDO);
        UIManager.put("Label.foreground", COR_TEXTO);
        UIManager.put("Label.font", FONTE_PADRAO);
        UIManager.put("OptionPane.messageFont", FONTE_PADRAO);
        UIManager.put("OptionPane.buttonFont", FONTE_PADRAO);
        UIManager.put("OptionPane.background", COR_CARD);
        UIManager.put("OptionPane.messageForeground", COR_TEXTO);
    }

    public static void estilizarBotao(JButton botao, Color cor) {
        botao.setFont(FONTE_BOTAO);
        botao.setForeground(Color.WHITE);
        botao.setBackground(cor);
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void estilizarTabela(JTable tabela) {
        tabela.setRowHeight(26);
        tabela.setFont(FONTE_PADRAO);
        tabela.setBackground(COR_CARD);
        tabela.setForeground(COR_TEXTO);
        tabela.setGridColor(new Color(52, 63, 78));
        tabela.setSelectionBackground(new Color(47, 97, 146));
        tabela.setSelectionForeground(Color.WHITE);
        tabela.getTableHeader().setFont(FONTE_BOTAO);
        tabela.getTableHeader().setBackground(new Color(38, 46, 57));
        tabela.getTableHeader().setForeground(COR_TEXTO);
    }

    public static void estilizarCampo(JTextField campo) {
        campo.setFont(FONTE_PADRAO);
        campo.setBackground(COR_INPUT);
        campo.setForeground(COR_TEXTO);
        campo.setCaretColor(COR_TEXTO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
    }

    public static void estilizarCombo(JComboBox<?> combo) {
        combo.setFont(FONTE_PADRAO);
        combo.setBackground(COR_INPUT);
        combo.setForeground(COR_TEXTO);
    }

    public static void estilizarArea(JTextArea area) {
        area.setFont(FONTE_PADRAO);
        area.setBackground(COR_INPUT);
        area.setForeground(COR_TEXTO);
        area.setCaretColor(COR_TEXTO);
        area.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
    }

    public static Border bordaSecao(String titulo) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                titulo
        );
        border.setTitleColor(COR_TEXTO);
        border.setTitleFont(FONTE_PADRAO);
        return border;
    }

    public static void estilizarLabelSecundaria(JLabel label) {
        label.setFont(FONTE_PADRAO);
        label.setForeground(COR_TEXTO_SEC);
    }

    public static void transicaoSuave(JFrame atual, Supplier<JFrame> proximaTela) {
        if (!atual.isUndecorated()) {
            Timer pausa = new Timer(120, e -> {
                ((Timer) e.getSource()).stop();
                atual.dispose();
                proximaTela.get();
            });
            pausa.setRepeats(false);
            pausa.start();
            return;
        }

        try {
            atual.setOpacity(1f);
            Timer timer = new Timer(15, null);
            timer.addActionListener(e -> {
                try {
                    float opacidade = atual.getOpacity() - 0.08f;
                    if (opacidade <= 0.08f) {
                        timer.stop();
                        atual.dispose();
                        JFrame proxima = proximaTela.get();
                        try {
                            proxima.setOpacity(0f);
                            Timer fadeIn = new Timer(15, null);
                            fadeIn.addActionListener(ev -> {
                                try {
                                    float op = proxima.getOpacity() + 0.1f;
                                    if (op >= 1f) {
                                        proxima.setOpacity(1f);
                                        fadeIn.stop();
                                    } else {
                                        proxima.setOpacity(op);
                                    }
                                } catch (Exception ex) {
                                    fadeIn.stop();
                                }
                            });
                            fadeIn.start();
                        } catch (Exception ignored) {
                        }
                    } else {
                        atual.setOpacity(opacidade);
                    }
                } catch (Exception ex) {
                    timer.stop();
                    atual.dispose();
                    proximaTela.get();
                }
            });
            timer.start();
        } catch (Exception e) {
            atual.dispose();
            proximaTela.get();
        }
    }
}
