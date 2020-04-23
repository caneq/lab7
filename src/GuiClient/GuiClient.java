package GuiClient;

import ServerAPI.Excepsions.*;
import ServerAPI.Message;
import ServerAPI.MessengerClient;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;

public class GuiClient extends JFrame {
    private Action signInAction;
    private Action signUpAction;
    private Action logOutAction;
    private Action connectToServerAction;
    private Box usersOnline;
    private JLabel myNameLabel;

    private HashMap<String, PrivateMessageFrame> openedPrivateFrames;
    private boolean logged;
    private MessengerClient client;

    public GuiClient(int width, int height){
        super("Socket Chat");

        logged = false;

        openedPrivateFrames = new HashMap();
        client  = null;

        FontUIResource f = new FontUIResource(new Font("Verdana", 0, 12));

        ArrayList<Object> gradients = new ArrayList();
        gradients.add(0.3);
        gradients.add(0.0);
        gradients.add(Color.WHITE);
        gradients.add(Color.WHITE);
        gradients.add(Color.WHITE);
        UIManager.put("RadioButton.background", Color.lightGray);
        UIManager.put("Button.gradient", gradients);

        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                FontUIResource orig = (FontUIResource) value;
                Font font = new Font(f.getFontName(), orig.getStyle(), f.getSize());
                UIManager.put(key, new FontUIResource(font));
            }
        }

        setSize(width, height);
        Toolkit kit = Toolkit.getDefaultToolkit();

        setLocation((kit.getScreenSize().width - width) / 2, (kit.getScreenSize().height - height) / 2);

        signInAction = new AbstractAction("Sign in") {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        };

        signUpAction = new AbstractAction("Sign up") {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        };

        logOutAction = new AbstractAction("Log out") {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        };

        connectToServerAction = new AbstractAction("Connect to server") {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServer();
            }
        };

        updateAccountMenu();

        createGui();

    }

    private void updateAccountMenu(){
        logOutAction.setEnabled(logged && client != null);
        signUpAction.setEnabled(!logged && client != null);
        signInAction.setEnabled(!logged && client != null);
        if (usersOnline != null) {
            usersOnline.revalidate();
        }
    }

    private void createGui(){
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu accountMenu = new JMenu("Account");
        accountMenu.add(signInAction);
        accountMenu.add(signUpAction);
        accountMenu.add(logOutAction);
        accountMenu.add(connectToServerAction);
        menuBar.add(accountMenu);

        myNameLabel = new JLabel("Not logged");
        myNameLabel.setForeground(Color.red);
        Box myNameBox = Box.createHorizontalBox();
        myNameLabel.setHorizontalAlignment(JLabel.LEFT);
        myNameBox.add(myNameLabel);
        myNameBox.add(Box.createHorizontalGlue());
        Box content = Box.createVerticalBox();
        content.add(myNameBox);
        Box labelBox = Box.createHorizontalBox();
        content.add(labelBox);
        labelBox.add(Box.createHorizontalGlue());
        labelBox.add(new JLabel("Users online:"));
        labelBox.add(Box.createHorizontalGlue());


        usersOnline = Box.createVerticalBox();
        JScrollPane jScrollPane = new JScrollPane(usersOnline);
        Box scrollPanel = Box.createVerticalBox();
        scrollPanel.add(jScrollPane);


        content.add(Box.createVerticalStrut(5));
        content.add(scrollPanel);

        add(content);

    }

    private void login(){
        if(client == null){
            JOptionPane.showMessageDialog(GuiClient.this ,"Connect to server first");
            return;
        }
        JTextField login = new JTextField(10);
        JPasswordField password = new JPasswordField(10);

        Box loginBox = Box.createVerticalBox();
        loginBox.add(new JLabel("Login"));
        loginBox.add(login);
        loginBox.add(new JLabel("Password"));
        loginBox.add(password);

        while (true) {

            int result = JOptionPane.showConfirmDialog(null, loginBox,
                    "Sign in", JOptionPane.OK_CANCEL_OPTION);

            if (result != JOptionPane.OK_OPTION) return;

            try {
                client.login(login.getText(), password.getText());
                break;
            } catch (WrongLogin wrongLogin) {
                JOptionPane.showMessageDialog(GuiClient.this,"Wrong login");
            } catch (WrongPassword wrongPassword) {
                JOptionPane.showMessageDialog(GuiClient.this,"Wrong password");
            } catch (UserAlreadyOnline userAlreadyOnline) {
                JOptionPane.showMessageDialog(GuiClient.this,"User already online");
            }

        }

        logged = true;
        setMyNameLabelText(client.getMyName());

        JOptionPane.showMessageDialog(GuiClient.this,"Logged as " + login.getText());

        updateAccountMenu();
    }

    private void logout(){
        if(client != null){
            client.dispose();
            closePrivateFrames();
        }
        logged = false;
        client = null;
        setMyNameLabelText(null);
        updateAccountMenu();
        updateUsersOnline(new ArrayList<String>());
    }

    private void register(){
        JTextField login = new JTextField(10);
        JPasswordField password = new JPasswordField(10);
        JPasswordField password2 = new JPasswordField(10);

        Box loginBox = Box.createVerticalBox();
        loginBox.add(new JLabel("Login"));
        loginBox.add(login);
        loginBox.add(new JLabel("Password"));
        loginBox.add(password);
        loginBox.add(new JLabel("Confirm password"));
        loginBox.add(password2);

        while (true) {
            int result = JOptionPane.showConfirmDialog(null, loginBox,
                    "Sign up", JOptionPane.OK_CANCEL_OPTION);

            if (result != JOptionPane.OK_OPTION) return;
            if (!password.getText().equals(password2.getText())) {
                JOptionPane.showMessageDialog(GuiClient.this, "Passwords not match");
                continue;
            }

            try {
                client.register(login.getText(), password.getText());
                logged = true;
                setMyNameLabelText(client.getMyName());
                updateAccountMenu();
                break;
            } catch (LoginAlreadyRegistered loginAlreadyRegistered) {
                JOptionPane.showMessageDialog(GuiClient.this, "Login already registered");
            }
            catch (UnknownExcepsion e){
                JOptionPane.showMessageDialog(GuiClient.this, "Excepsion: " + e.getMessage());
            }

        }
    }

    private void setMyNameLabelText(String text){
        if(text != null){
            myNameLabel.setText("Logged as " + text);
            myNameLabel.setForeground(Color.BLACK);
        }
        else {
            myNameLabel.setText("Not logged");
            myNameLabel.setForeground(Color.red);
        }
    }

    private void connectToServer(){
        JTextField ip = new JTextField("localhost",10);
        JTextField port = new JTextField("1155",10);

        Box connectBox = Box.createVerticalBox();
        connectBox.add(new JLabel("IP"));
        connectBox.add(ip);
        connectBox.add(new JLabel("Port"));
        connectBox.add(port);

        while (true) {

            int result = JOptionPane.showConfirmDialog(null, connectBox,
                    "Connect to server", JOptionPane.OK_CANCEL_OPTION);

            if (result != JOptionPane.OK_OPTION) return;

            try {
                if(client != null){
                    client.dispose();
                }
                client = new MessengerClient(ip.getText(), Integer.parseInt(port.getText()));
                setMyNameLabelText(client.getMyName());
                myNameLabel.setText("Not logged");
                closePrivateFrames();
                updateUsersOnline(new ArrayList<>());
                client.addUsersOnlineListener(users -> updateUsersOnline(users));
                client.addMessageListener(msg -> receiveMessage(msg));
                JOptionPane.showMessageDialog(GuiClient.this, "Connected");
                updateAccountMenu();
                break;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(GuiClient.this, "Server not found");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(GuiClient.this, "Port must be integer");
            }
        }
    }

    private void receiveMessage(Message message){
        if (message.sender.equals("SERVER")){
            JOptionPane.showMessageDialog(GuiClient.this, "Server says: " + message.message);
            return;
        }
        PrivateMessageFrame frame = openedPrivateFrames.get(message.sender);
        if (frame == null){
            frame = createPrivateFrame(message.sender);
            addPrivateFrame(message.sender ,frame);
        }
        frame.receiveMessage(message.message);
    }

    private void sendMessage(Message message){
        if (client != null ) {
            if(logged) {
                client.sendMessage(message);
            }
            else {
                JOptionPane.showMessageDialog(GuiClient.this ,"Login to send message");
            }
        }
        else {
            JOptionPane.showMessageDialog(GuiClient.this ,"Connect to send message");
        }
    }

    private void closePrivateFrames(){
        openedPrivateFrames.values().forEach(frame -> {
            frame.dispose();
        });
        openedPrivateFrames.clear();
    }

    private void updateUsersOnline(ArrayList<String> users){
        synchronized (usersOnline) {
            usersOnline.removeAll();
            for (String user : users) {

                Box horizontal = Box.createHorizontalBox();
                horizontal.add(Box.createHorizontalGlue());
                JButton button = new JButton(user);
                button.setPreferredSize(new Dimension(200, button.getPreferredSize().height));
                horizontal.add(button);
                horizontal.add(Box.createHorizontalGlue());
                usersOnline.add(horizontal);

                button.setMaximumSize(new Dimension(button.getPreferredSize().width, button.getMaximumSize().height));

                button.addActionListener(e -> {

                    synchronized (openedPrivateFrames) {
                        PrivateMessageFrame frame = openedPrivateFrames.get(user);
                        if (frame == null) {
                            createPrivateFrame(user);
                        } else {
                            frame.toFront();
                            frame.requestFocus();
                        }
                    }
                });
            }
            usersOnline.revalidate();
            usersOnline.repaint();
        }
    }

    private void addPrivateFrame(String user ,PrivateMessageFrame frame){
        synchronized (openedPrivateFrames){
            if (!openedPrivateFrames.keySet().contains(user)) {
                openedPrivateFrames.put(user, frame);
                openedPrivateFrames.keySet().forEach(x -> System.out.println(x));
                System.out.println("");
            }
        }
    }

    private void removePrivateFrame(String user){
        synchronized (openedPrivateFrames){
            openedPrivateFrames.remove(user);
            openedPrivateFrames.keySet().forEach(x -> System.out.println(x));
            System.out.println("");
        }
    }

    private PrivateMessageFrame createPrivateFrame(String user){
        MessageSender sender = new MessageSender() {
            @Override
            public void send(Message message) {
                sendMessage(message);
            }
        };

        PrivateMessageFrame frame = new PrivateMessageFrame(user, sender, 300, 400);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                removePrivateFrame(user);
            }
        });

        addPrivateFrame(user, frame);

        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        frame.setVisible(true);
        return frame;
    }

    public static void main(String[] args) {
        GuiClient frame = new GuiClient(400,500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
