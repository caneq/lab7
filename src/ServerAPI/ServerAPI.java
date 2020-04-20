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
    private final int PORT = 775;
    LinkedList<MessageListener> listeners;
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private BufferedWriter outputWriter;
    private BufferedReader inputReader;
    private Boolean logged = false;

    public ServerAPI(){
        try {
            socket = new Socket(HOST, PORT);
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
            outputWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            inputReader = new BufferedReader(new InputStreamReader(inputStream));
        }
        catch (UnknownHostException exc) {
            exc.printStackTrace();
        }

        catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public void register(String login, String password) throws LoginAlreadyRegistered {
        try {
            outputWriter.write("reg " + login + ":" + password);
            outputWriter.flush();

            String response = inputReader.readLine();
            if ("alreadyRegistered".equals(response)){
                throw new LoginAlreadyRegistered();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void login(String login, String password) throws WrongLogin, WrongPassword {
        if (logged) return;

        try {
            outputWriter.write("login " + login + ":" + password);
            outputWriter.flush();

            String response = inputReader.readLine();
            if ("WrongLogin".equals(response)){
                throw new WrongLogin();
            }
            if ("WrongPassword".equals(response)){
                throw new WrongPassword();
            }
            new MessageReceiver().run();
            logged = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) throws UserNotFound {
        try {
            outputWriter.write("sendTo " + message.receiver + ":" + message.message);
            outputWriter.flush();

            String response = inputReader.readLine();
            if("userNotFound".equals(response)){
                throw new UserNotFound();
            }

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
        public void run() {
            while(true){
                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                    Message message = (Message) objectInputStream.readObject();
                    notifyListeners(message);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
