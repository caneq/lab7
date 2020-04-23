package ServerAPI;

import ServerAPI.Excepsions.*;

import java.io.*;
import java.net.Socket;
import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.Set;

public class MessengerClient {
    private Socket socket;

    ArrayList<MessageListener> messageListeners;
    ArrayList<UsersOnlineListener> onlineUsersListener;

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    private boolean logged = false;

    private String userName = null;

    public MessengerClient(String host, int port) throws IOException {
        messageListeners = new ArrayList();
        onlineUsersListener = new ArrayList();

        socket = new Socket(host, port);
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

    }

    public boolean register(String login, String password) throws LoginAlreadyRegistered, UnknownExcepsion {
        if (logged) return false;

        try {
            Message message = new Message(userName, "SERVER", "register " + login + " " + password);
            objectOutputStream.writeObject(message);

            String response = ((Message) objectInputStream.readObject()).message;
            if (response.equals("OK")){
                userName = login;
                logged = true;
                new MessageReceiver().start();
                return true;
            }
            else if ("LoginAlreadyRegistered".equals(response)) {
                throw new LoginAlreadyRegistered();
            }
            else {
                throw new UnknownExcepsion(response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void login(String login, String password) throws WrongLogin, WrongPassword, UserAlreadyOnline {
        if (logged) return;

        try {

            Message message = new Message("", "SERVER", "login " + login + " " + password);
            objectOutputStream.writeObject(message);

            Object messageObject = objectInputStream.readObject();

            String response = ((Message) messageObject).message;
            if (response.equals("OK")){
                userName = login;
                logged = true;
            }

            else if ("WrongLogin".equals(response)){
                throw new WrongLogin();
            }
            else if ("WrongPassword".equals(response)){
                throw new WrongPassword();
            }
            else if ("UserAlreadyOnline".equals(response)){
                throw new UserAlreadyOnline();
            }
            else {
                throw new UnknownServiceException(response);
            }

            new MessageReceiver().start();
            logged = true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getMyName(){
        return userName;
    }

    public void sendMessage(Message message) {
        try {
            message.sender = userName;
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addMessageListener(MessageListener listener){
        synchronized (messageListeners) {
            this.messageListeners.add(listener);
        }
    }

    public void removeMessageListener(MessageListener listener){
        synchronized (messageListeners) {
            this.messageListeners.remove(listener);
        }
    }

    public void addUsersOnlineListener(UsersOnlineListener listener){
        synchronized (onlineUsersListener){
            onlineUsersListener.add(listener);
        }
    }

    public void removeUsersOnlineListener(UsersOnlineListener listener){
        synchronized (onlineUsersListener) {
            this.onlineUsersListener.remove(listener);
        }
    }

    private void notifyMessageListeners(Message message) {
        synchronized (messageListeners) {
            for (MessageListener listener : messageListeners) {
                listener.messageReceived(message);
            }
        }
    }

    private void notifyUsersonlineListeners(ArrayList<String> users) {
        synchronized (onlineUsersListener) {
            for (UsersOnlineListener listener : onlineUsersListener) {
                listener.update(users);
            }
        }
    }

    public void dispose(){
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class MessageReceiver extends Thread{
        public MessageReceiver(){ }

        @Override
        public void run() {
            super.run();
            while(true){
                try {
                    Object messageObject = objectInputStream.readObject();

                    if (messageObject instanceof ArrayList){
                        notifyUsersonlineListeners((ArrayList<String>) messageObject);
                    }
                    else {
                        Message message = (Message) messageObject;

                        notifyMessageListeners(message);
                    }

                } catch (ClassNotFoundException e) {
                    notifyMessageListeners(new Message("SERVER", userName, "UnknownError"));
                    e.printStackTrace();
                    logged = false;
                    break;
                } catch (IOException e) {
                    notifyMessageListeners(new Message("SERVER", userName, "ConectionClosed"));
                    logged = false;
                    break;
                }

            }
        }
    }

}
