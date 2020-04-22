package GuiClient;

import ServerAPI.Message;
import ServerAPI.MessengerClient;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;

public class PrivateMessageFrame extends JFrame {
    private Box messages;
    private Toolkit kit;
    private int width;
    private int height;
    private JTextField textField;
    private JScrollPane scrollPane;


    public PrivateMessageFrame(String userName, int width, int height){
        super("Chat with " + userName);
        this.width = width;
        this.height = height;
        kit = Toolkit.getDefaultToolkit();
        FontUIResource f = new FontUIResource(new Font("Verdana", 0, 12));
        UIManager.put("font", f);

        setSize(width, height);
        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - width) / 2, (kit.getScreenSize().height - height) / 2);

        createGui();

    }

    private void createGui(){
        Box content = Box.createVerticalBox();
        messages = Box.createVerticalBox();
        scrollPane = new JScrollPane(messages);
        content.add(scrollPane);

        content.add(Box.createVerticalStrut(5));

        textField = new JTextField(50);
        textField.setMaximumSize(textField.getPreferredSize());
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    sendMessage(textField.getText());
                    textField.setText("");
                }
            }
        });

        Box horizontal = Box.createHorizontalBox();
        horizontal.add(textField);
        content.add(horizontal);

        add(content);

    }

    public void receiveMessage(String msg){
        Box horizontal = Box.createHorizontalBox();

        JLabel message = new JLabel("<html><body><p style=\"width: "
                + width * 1/2 +"px\">" + msg + "</p></body></html>");
        message.setHorizontalAlignment(JLabel.LEFT);

        message.setMaximumSize(message.getPreferredSize());
        horizontal.add(message);
        horizontal.add(Box.createHorizontalGlue());
        messages.add(horizontal);

        messages.revalidate();

    }

    public void sendMessage(String msg){
        Box horizontal = Box.createHorizontalBox();

        JLabel message = new JLabel("<html><body><p align=\"right\" style=\"width: "
                + width * 1/2 +"px\">" + msg + "</p></body></html>");
        message.setHorizontalAlignment(JLabel.RIGHT);

        horizontal.add(Box.createHorizontalGlue());
        horizontal.add(message);
        messages.add(horizontal);

        messages.revalidate();
    }

    public static void main(String[] args) {
        PrivateMessageFrame frame = new PrivateMessageFrame("test",400,500);
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

}
