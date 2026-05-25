import InterfaceSwing.telas.TelaLogin;
import javax.swing.SwingUtilities;

public class MenuPrincipal {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaLogin::new);
    }
}
