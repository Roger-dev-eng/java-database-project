package InterfaceSwing.telas;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;

public final class DashboardLauncher {
    private static final int DASHBOARD_PORT = 8501;
    private static final String DASHBOARD_URL = "http://localhost:" + DASHBOARD_PORT;
    private static final String[] DASHBOARD_APPS = {"dashboard/dashboard.py", "dashboard/app.py"};

    private DashboardLauncher() {
    }

    public static void abrirDashboard(Component parent) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (!dashboardEstaAtivo()) {
                    iniciarDashboard();
                    aguardarInicializacao();
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    abrirNoNavegador();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            parent,
                            "Nao foi possivel abrir o dashboard.\n" +
                                    "Verifique se Python, Streamlit e as dependencias do dashboard estao instalados.\n\n" +
                                    "Detalhes: " + e.getMessage(),
                            "Erro ao abrir dashboard",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    private static boolean dashboardEstaAtivo() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("127.0.0.1", DASHBOARD_PORT), 500);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static void iniciarDashboard() throws IOException {
        File raizProjeto = new File(System.getProperty("user.dir"));
        String caminhoDashboard = encontrarArquivoDashboard(raizProjeto);

        IOException ultimoErro = null;
        String[][] comandos = new String[][]{
                {"py", "-m", "streamlit", "run", caminhoDashboard, "--server.headless", "true", "--server.port", String.valueOf(DASHBOARD_PORT)},
                {"python", "-m", "streamlit", "run", caminhoDashboard, "--server.headless", "true", "--server.port", String.valueOf(DASHBOARD_PORT)}
        };

        for (String[] comando : comandos) {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(comando);
                processBuilder.directory(raizProjeto);
                processBuilder.redirectErrorStream(true);
                processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(new File(raizProjeto, "dashboard.log")));
                processBuilder.start();
                return;
            } catch (IOException e) {
                ultimoErro = e;
            }
        }

        throw ultimoErro == null ? new IOException("Nao foi possivel iniciar o Streamlit.") : ultimoErro;
    }

    private static String encontrarArquivoDashboard(File raizProjeto) throws IOException {
        for (String caminho : DASHBOARD_APPS) {
            File arquivo = new File(raizProjeto, caminho);
            if (arquivo.isFile()) {
                return caminho;
            }
        }
        throw new IOException("Nenhum arquivo do dashboard foi encontrado.");
    }

    private static void aguardarInicializacao() throws IOException {
        int tentativas = 20;
        while (tentativas-- > 0) {
            if (dashboardEstaAtivo()) {
                return;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Inicializacao do dashboard interrompida.", e);
            }
        }
        throw new IOException("O dashboard nao respondeu na porta " + DASHBOARD_PORT + ".");
    }

    private static void abrirNoNavegador() throws IOException {
        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            throw new IOException("Abertura automatica do navegador nao esta disponivel neste ambiente.");
        }
        Desktop.getDesktop().browse(URI.create(DASHBOARD_URL));
    }
}
