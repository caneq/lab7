package GuiClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;

public class GuiClient extends JFrame {
    private boolean logged;
    Action signInAction;
    Action signUpAction;
    Action logOutAction;

    public GuiClient(int width, int height){
        super("Socket Chat");
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
        add(content);
        Box labelBox = Box.createHorizontalBox();
        content.add(labelBox);
        labelBox.add(Box.createHorizontalGlue());
        labelBox.add(new JLabel("Users online:"));
        labelBox.add(Box.createHorizontalGlue());

        JScrollPane jScrollPane = new JScrollPane();
        content.add(jScrollPane);
        jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        Box usersOnline = Box.createVerticalBox();
        jScrollPane.add(usersOnline);

    }

    public static void main(String[] args) {
        GuiClient frame = new GuiClient(400,500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
