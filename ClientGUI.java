import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ClientGUI extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JTextField usernameField;
    private JTextField hostField;
    private JTextField portField;
    private JButton connectBtn;
    private JButton sendBtn;

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public ClientGUI() {
        super("Chat Client GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(chatArea);

        inputField = new JTextField();
        sendBtn = new JButton("Send");

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(inputField, BorderLayout.CENTER);
        bottom.add(sendBtn, BorderLayout.EAST);

        usernameField = new JTextField(10);
        hostField = new JTextField("localhost", 10);
        portField = new JTextField("1234", 5);
        connectBtn = new JButton("Connect");

        JPanel top = new JPanel();
        top.add(new JLabel("Name:"));
        top.add(usernameField);
        top.add(new JLabel("Host:"));
        top.add(hostField);
        top.add(new JLabel("Port:"));
        top.add(portField);
        top.add(connectBtn);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        connectBtn.addActionListener(e -> connect());
        sendBtn.addActionListener(e -> send());
        inputField.addActionListener(e -> send());
    }

    private void connect() {
        try {
            socket = new Socket(hostField.getText(), Integer.parseInt(portField.getText()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            writer.write(usernameField.getText() + "\n");
            writer.flush();

            connectBtn.setEnabled(false);

            chatArea.append("Connected.\n");

            new Thread(() -> {
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String msg = line;
                        SwingUtilities.invokeLater(() -> chatArea.append(msg + "\n"));
                    }
                } catch (Exception ignored) {}
            }).start();

        } catch (Exception ex) {
            chatArea.append("Failed to connect: " + ex.getMessage() + "\n");
        }
    }

    private void send() {
        try {
            String text = inputField.getText();
            writer.write(text + "\n");
            writer.flush();
            inputField.setText("");
        } catch (Exception ex) {
            chatArea.append("Send failed.\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientGUI().setVisible(true));
    }
}

