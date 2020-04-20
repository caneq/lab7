package ServerAPI;

import ServerAPI.Excepsions.LoginAlreadyRegistered;
import ServerAPI.Excepsions.UserNotFound;
import ServerAPI.Excepsions.WrongLogin;
import ServerAPI.Excepsions.WrongPassword;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

public class ServerAPI {
    private final String HOST = "localhost";
    private final int PORT = 1155;
    private Socket socket;

    LinkedList<MessageListener> listeners;

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    private boolean logged = false;

    private String userName = "";

    public ServerAPI(){
        listeners = new LinkedList();
        try {
            socket = new Socket(HOST, PORT);
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (UnknownHostException exc) {
            exc.printStackTrace();
        }

        catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public void register(String login, String password) throws LoginAlreadyRegistered {
        if (logged) return;

        try {
            Message message = new Message(userName, "SERVER", "register " + login + " " + password);
            objectOutputStream.writeObject(message);

            Message response = (Message) objectInputStream.readObject();
            if (response.message.equals("OK")){
                userName = login;
                logged = true;
                new MessageReceiver().start();
            }

            if ("alreadyRegistered".equals(response)) {
                throw new LoginAlreadyRegistered();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void login(String login, String password) throws WrongLogin, WrongPassword {
        if (logged) return;

        try {

            Message message = new Message("", "SERVER", "login " + login + " " + password);
            objectOutputStream.writeObject(message);

            String response = ((Message) objectInputStream.readObject()).message;
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

            new MessageReceiver().start();
            logged = true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
        try {
            message.sender = userName;
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void addMessageListener(MessageListener listener){
        synchronized (listeners) {
            this.listeners.add(listener);
        }
    }

    public synchronized void removeMessageListener(MessageListener listener){
        synchronized (listeners) {
            this.listeners.remove(listener);
        }
    }

    private synchronized void notifyListeners(Message message) {
        synchronized (listeners) {
            for (MessageListener listener : listeners) {
                listener.messageReceived(message);
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
                    Message message = (Message) objectInputStream.readObject();
                    //if("SERVER".equals(message.sender)) {
                    //    if("userNotFound".equals(message.message)){
                    //
                    //   }
                    //}
                    notifyListeners(message);

                } catch (ClassNotFoundException e) {
                    notifyListeners(new Message("SERVER", userName, "UnknownError"));
                    e.printStackTrace();
                    logged = false;
                    break;
                } catch (IOException e) {
                    notifyListeners(new Message("SERVER", userName, "ConectionClosed"));
                    logged = false;
                    break;
                }

            }
        }
    }

}
