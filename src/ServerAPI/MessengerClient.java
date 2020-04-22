package ServerAPI;

import ServerAPI.Excepsions.*;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import java.util.LinkedList;

public class MessengerClient {
    private Socket socket;

    LinkedList<MessageListener> listeners;

    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    private boolean logged = false;

    private String userName = "";

    public MessengerClient(String host, int port) throws IOException {
        listeners = new LinkedList();

        socket = new Socket(host, port);
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

    }

    public boolean register(String login, String password) throws LoginAlreadyRegistered {
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
                throw new UnknownServiceException(response);
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
