package client;

import server.Server;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.*;

@SuppressWarnings("serial")
public class ClientMainFrame extends JFrame {

    private static final String FRAME_TITLE = "Клиент мгновенных сообщений";

    private static InetSocketAddress serverAddress;
    private DataInputStream in;
    private DataOutputStream out;
    private Thread MessagesListenerThread;
    private String login;
    private String password;


    private static final int FRAME_MINIMUM_WIDTH = 500;
    private static final int FRAME_MINIMUM_HEIGHT = 500;

    private static final int TO_FIELD_DEFAULT_COLUMNS = 20;

    private static final int INCOMING_AREA_DEFAULT_ROWS = 10;
    private static final int OUTGOING_AREA_DEFAULT_ROWS = 5;

    private static final int SMALL_GAP = 5;
    private static final int MEDIUM_GAP = 10;
    private static final int LARGE_GAP = 15;

    private final JTextField textFieldTo;

    private JTextField textFieldLogin;
    private JPasswordField textFieldPassword;

    private final JTextArea textAreaIncoming;
    private final JTextArea textAreaOutgoing;

    public ClientMainFrame(){
        super(FRAME_TITLE);
        setMinimumSize(new Dimension(FRAME_MINIMUM_WIDTH, FRAME_MINIMUM_HEIGHT));
        final Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - getWidth()) / 2,
                (kit.getScreenSize().height - getHeight()) / 2);

        textAreaIncoming = new JTextArea(INCOMING_AREA_DEFAULT_ROWS, 0);
        textAreaIncoming.setEditable(false);

        final JScrollPane scrollPaneIncoming =
                new JScrollPane(textAreaIncoming);

        final JLabel labelTo = new JLabel("Получатель");

        textFieldTo = new JTextField(TO_FIELD_DEFAULT_COLUMNS);

        textAreaOutgoing = new JTextArea(OUTGOING_AREA_DEFAULT_ROWS, 0);

        final JScrollPane scrollPaneOutgoing = new JScrollPane(textAreaOutgoing);

        final JPanel messagePanel = new JPanel();
        messagePanel.setBorder(BorderFactory.createTitledBorder("Сообщение"));

        final JButton sendButton = new JButton("Отправить");
        sendButton.addActionListener(e -> sendMessage());

        final JButton loginButton = new JButton("Войти");
        loginButton.addActionListener(e -> logIn());

        final GroupLayout layout2 = new GroupLayout(messagePanel);
        messagePanel.setLayout(layout2);

        layout2.setHorizontalGroup(layout2.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout2.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addGroup(layout2.createSequentialGroup()
                                .addGap(LARGE_GAP)
                                .addComponent(labelTo)
                                .addGap(SMALL_GAP)
                                .addComponent(textFieldTo))
                        .addComponent(scrollPaneOutgoing)
                        .addComponent(sendButton))
                .addContainerGap());
        layout2.setVerticalGroup(layout2.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout2.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelTo)
                        .addComponent(textFieldTo))
                .addGap(MEDIUM_GAP)
                .addComponent(scrollPaneOutgoing)
                .addGap(MEDIUM_GAP)
                .addComponent(sendButton)
                .addContainerGap());

        final JPanel accountPanel = new JPanel();
        accountPanel.setBorder(BorderFactory.createTitledBorder("Аккаунт"));

        final GroupLayout layout3 = new GroupLayout(accountPanel);
        accountPanel.setLayout(layout3);

        JLabel loginLabel = new JLabel("Логин");
        JLabel passwordLabel = new JLabel("Пароль");

        textFieldLogin = new JTextField();
        textFieldPassword = new JPasswordField();

        layout3.setHorizontalGroup(layout3.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout3.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addGroup(layout3.createSequentialGroup()
                                .addComponent(loginLabel)
                                .addGap(SMALL_GAP)
                                .addComponent(textFieldLogin)
                                .addGap(LARGE_GAP)
                                .addComponent(passwordLabel)
                                .addGap(SMALL_GAP)
                                .addComponent(textFieldPassword)
                                .addGap(SMALL_GAP)
                                .addComponent(loginButton)
                        ))
                .addContainerGap()
                );
        layout3.setVerticalGroup(layout3.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout3.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(loginLabel)
                        .addComponent(textFieldLogin)
                        .addComponent(passwordLabel)
                        .addComponent(textFieldPassword)
                        .addComponent(loginButton)
                )
                .addContainerGap());

        final GroupLayout layout1 = new GroupLayout(getContentPane());
        setLayout(layout1);

        layout1.setHorizontalGroup(layout1.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout1.createParallelGroup()
                        .addComponent(scrollPaneIncoming)
                        .addComponent(messagePanel)
                        .addComponent(accountPanel))

                .addContainerGap());
        layout1.setVerticalGroup(layout1.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneIncoming)
                .addGap(MEDIUM_GAP)
                .addComponent(accountPanel)
                .addGap(MEDIUM_GAP)
                .addComponent(messagePanel)
                .addContainerGap());

    }

    private void logIn(){
        if(MessagesListenerThread != null) MessagesListenerThread.interrupt();

        try {
            Socket socket = new Socket(serverAddress.getHostName(), serverAddress.getPort());
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            login = textFieldLogin.getText();
            password = textFieldPassword.getText();
            out.writeUTF(login);
            out.writeUTF(password);
        }
        catch (Exception e){
            login = password = null;
            JOptionPane.showMessageDialog(ClientMainFrame.this, "Ошибка в работе сервера", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }

        MessagesListenerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!Thread.interrupted()) {
                        String senderName = in.readUTF();
                        String message = in.readUTF();
                        SwingUtilities.invokeLater(() -> textAreaIncoming.append(senderName + ":" + message + "\n"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ClientMainFrame.this, "Ошибка в работе сервера", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        MessagesListenerThread.start();
    }

    private void sendMessage() {
        try {

            String receiver = textFieldTo.getText();
            String message = textAreaOutgoing.getText();
            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(ClientMainFrame.this,
                        "Введите текст сообщения", "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            out.writeUTF(login);
            out.writeUTF(password);
            out.writeUTF(receiver);
            out.writeUTF(message);
            textAreaOutgoing.setText("");

        }
        catch (NullPointerException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ClientMainFrame.this,
                    "Войдите в аккаунт",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ClientMainFrame.this,
                    "Не удалось отправить сообщение: сервер не найден",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
        catch (java.net.SocketException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ClientMainFrame.this,
                    "Не удалось отправить сообщение: узел-адресат не найден",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ClientMainFrame.this,
                    "Не удалось отправить сообщение", "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        serverAddress = new InetSocketAddress("127.0.0.1", Server.PORT);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ClientMainFrame frame = new ClientMainFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}