import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ServerGUI extends JFrame {
    private JTextArea logArea;
    private JList<String> clientsList;
    private DefaultListModel<String> clientsModel;
    private JButton startBtn;
    private JButton stopBtn;

    private ServerSocket serverSocket;
    private Thread serverThread;
    private Server serverInstance;

    public ServerGUI() {
        super("Chat Server GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);

        clientsModel = new DefaultListModel<>();
        clientsList = new JList<>(clientsModel);
        JScrollPane clientsScroll = new JScrollPane(clientsList);
        clientsScroll.setPreferredSize(new Dimension(200, 0));

        startBtn = new JButton("Start Server (1234)");
        stopBtn = new JButton("Stop Server");
        stopBtn.setEnabled(false);

        JPanel topPanel = new JPanel();
        topPanel.add(startBtn);
        topPanel.add(stopBtn);

        add(topPanel, BorderLayout.NORTH);
        add(logScroll, BorderLayout.CENTER);
        add(clientsScroll, BorderLayout.EAST);

        redirectSystemOut();

        startBtn.addActionListener(e -> startServer());
        stopBtn.addActionListener(e -> stopServer());

        new javax.swing.Timer(1000, e -> refreshClientsList()).start();
    }

    private void redirectSystemOut() {
        OutputStream out = new OutputStream() {
            private StringBuilder buffer = new StringBuilder();

            @Override
            public void write(int b) {
                if (b == '\n') {
                    SwingUtilities.invokeLater(() -> logArea.append(buffer.toString() + "\n"));
                    buffer.setLength(0);
                } else {
                    buffer.append((char) b);
                }
            }
        };

        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(1234);
            serverInstance = new Server(serverSocket);

            serverThread = new Thread(serverInstance::startServer);
            serverThread.start();

            logArea.append("Server started on port 1234\n");
            startBtn.setEnabled(false);
            stopBtn.setEnabled(true);

        } catch (IOException ex) {
            logArea.append("Failed to start: " + ex.getMessage() + "\n");
        }
    }

    private void stopServer() {
        try {
            serverSocket.close();
            logArea.append("Server stopped.\n");
        } catch (Exception ignored) {}

        startBtn.setEnabled(true);
        stopBtn.setEnabled(false);
    }

    private void refreshClientsList() {
        try {
            Class<?> chClass = Class.forName("ClientHandler");
            Field listField = chClass.getField("clientHandlers");
            Object listObj = listField.get(null);

            clientsModel.clear();

            if (listObj instanceof ArrayList) {
                for (Object handler : (ArrayList<?>) listObj) {
                    Field usernameField = handler.getClass().getDeclaredField("clientUsername");
                    usernameField.setAccessible(true);

                    Object name = usernameField.get(handler);
                    clientsModel.addElement(name.toString());
                }
            }
        } catch (Exception ignored) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ServerGUI().setVisible(true));
    }
}

