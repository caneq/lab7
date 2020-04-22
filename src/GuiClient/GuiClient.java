package GuiClient;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GuiClient extends JFrame {
    private boolean logged;
    Action signInAction;
    Action signUpAction;
    Action logOutAction;
    Box usersOnline;
    HashMap<String, PrivateMessageFrame> openedPrivateFrames;

    public GuiClient(int width, int height){
        super("Socket Chat");

        openedPrivateFrames = new HashMap();

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

        logged = false;
        setSize(width, height);
        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - width) / 2, (kit.getScreenSize().height - height) / 2);

        signInAction = new AbstractAction("Sign in") {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        signUpAction = new AbstractAction("Sign up") {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        logOutAction = new AbstractAction("Log out") {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        logOutAction.setEnabled(logged);

        createGui();

    }

    private void createGui(){
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu accountMenu = new JMenu("Account");
        accountMenu.add(signInAction);
        accountMenu.add(signUpAction);
        accountMenu.add(logOutAction);
        menuBar.add(accountMenu);

        Box content = Box.createVerticalBox();
        //add(content);
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

        String[] users = {"kek", "lol", "nelol", "trhtr","1"};
        updateUsersOnline(new ArrayList<String>(Arrays.asList(users)));

        add(content);

    }

    private void updateUsersOnline(ArrayList<String> users){
        usersOnline.removeAll();
        for(String user: users){

            Box horizontal = Box.createHorizontalBox();
            horizontal.add(Box.createHorizontalGlue());
            JButton button = new JButton(user);
            horizontal.add(button);
            horizontal.add(Box.createHorizontalGlue());
            usersOnline.add(horizontal);

            button.setMaximumSize(new Dimension(button.getPreferredSize().width, button.getMaximumSize().height));

            button.addActionListener(e -> {

                synchronized (openedPrivateFrames) {
                    if (!openedPrivateFrames.keySet().contains(user)) {
                        createPrivateFrame(user);
                    }
                }
            });
        }
        usersOnline.revalidate();

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
        PrivateMessageFrame frame = new PrivateMessageFrame(user,300,400);

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
