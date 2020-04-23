package server;
import ServerAPI.Excepsions.UserNotFound;
import ServerAPI.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;

public class UserThread extends Thread {
    private UsersHandler usersHandler;
    private Socket socket;
    private boolean logged = false;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String userName;

    UserThread(Socket socket, UsersHandler usersHandler){
        System.out.println("new user connected");
        this.usersHandler = usersHandler;
        this.socket = socket;
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Message message) throws UserNotFound {
        if(!logged) {
            throw new UserNotFound();
        }
        try {
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendUsersOnline(ArrayList<String> users) {
        if (!logged) return;
        try {
            objectOutputStream.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendSystemMessage(String message){
        try {
            objectOutputStream.writeObject(new Message("SERVER", userName, message));
            System.out.println(userName + " " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processSystemMessage(Message message) {
        try {
            String[] args = message.message.split(" ");
            if (args[0].equals("login")) {
                try {
                    if (usersHandler.logIn(args[1], args[2], this)) {
                        userName = args[1];
                        logged = true;
                        objectOutputStream.writeObject(new Message("SERVER", userName, "OK"));
                        sendUsersOnline(usersHandler.getOnlineUsers());
                    }
                } catch (Exception e) {
                    sendSystemMessage(e.getClass().getSimpleName());
                }
            } else if (args[0].equals("register")) {
                try {
                    if (usersHandler.register(args[1], args[2], this)) {
                        userName = args[1];
                        logged = true;
                        objectOutputStream.writeObject(new Message("SERVER", userName, "OK"));
                        sendUsersOnline(usersHandler.getOnlineUsers());
                    }
                } catch (Exception e) {
                    sendSystemMessage(e.getClass().getSimpleName());
                }
            } else sendSystemMessage("CommandNotFound");
        }
        catch (Exception e){
            sendSystemMessage("IncorrectServerCommand");
        }
    }

    private void sendToUser(Message message){
        if(!logged) return;
        System.out.println(message.sender + " -> " + message.receiver + " : " + message.message);
        try {
            usersHandler.findUser(message.receiver).send(message);
        } catch (Exception e) {
            sendSystemMessage(e.getClass().getSimpleName());
        }
    }

    @Override
    public void run() {
        super.run();
        while (true){
            try {
                Message message = (Message) objectInputStream.readObject();

                if (message.receiver.equals("SERVER")){
                    processSystemMessage(message);
                }
                else {
                    sendToUser(message);
                }

            } catch (IOException e) {
                this.dispose();
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void dispose(){
        usersHandler.removeUser(userName);
        try {
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            objectInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(userName + " disconnected");
    }

}
