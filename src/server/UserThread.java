package server;

import ServerAPI.Excepsions.UserNotFound;
import ServerAPI.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class UserThread extends Thread {
    private UsersHandler usersHandler;
    private Socket socket;
    private boolean logged = false;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String userName;

    UserThread(Socket socket, UsersHandler usersHandler){
        this.usersHandler = usersHandler;
        this.socket = socket;
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Send(Message message) throws UserNotFound {
        if(!logged) {
            throw new UserNotFound();
        }
        try {
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processSystemMessage(Message message){
        String[] args = message.message.split(" ");
        if(args[0].equals("login")){
            if(usersHandler.logIn(args[1], args[2])){
                userName = args[1];
                usersHandler.addUser(userName, this);
                logged = true;
            }
        }
        else if(args[0].equals("register")){
            if(usersHandler.register(args[1], args[2])){
                userName = args[1];
                usersHandler.addUser(userName, this);
                logged = true;
            }
        }
    }

    private void sendToUser(Message message){
        if(!logged) return;
        try {
            usersHandler.findUser(message.receiver).Send(message);
        } catch (UserNotFound userNotFound) {
            Message systemMessage = new Message("SERVER", userName, "UserNotFound");
            try {
                objectOutputStream.writeObject(systemMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        super.run();
        while (true){
            try {
                Message message = (Message) objectInputStream.readObject();

                if (message.receiver.equals("SERVER") && !logged){
                    processSystemMessage(message);
                }
                else {
                    sendToUser(message);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
