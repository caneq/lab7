package server;

import ServerAPI.Excepsions.UserNotFound;
import ServerAPI.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class UserThread extends Thread {
    private UsersHandler usersHandler;
    private Socket socket;
    private boolean logged = false;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String userName;

    UserThread(Socket socket, UsersHandler usersHandler){
        System.out.println("UserThread ctor");
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

    private void processSystemMessage(Message message) throws IOException {
        String[] args = message.message.split(" ");
        if(args[0].equals("login")){
            if(usersHandler.logIn(args[1], args[2])){
                userName = args[1];
                System.out.println(userName + "logged in");
                usersHandler.addUser(userName, this);
                logged = true;
                objectOutputStream.writeObject(new Message("SERVER", userName, "OK"));
            }
        }
        else if(args[0].equals("register")){
            if(usersHandler.register(args[1], args[2])){
                userName = args[1];
                System.out.println(userName + "register and logged in");
                usersHandler.addUser(userName, this);
                logged = true;
                objectOutputStream.writeObject(new Message("SERVER", userName, "OK"));
            }
        }
    }

    private void sendToUser(Message message){
        if(!logged) return;
        System.out.println(message.sender + " -> " + message.receiver + " : " + message.message);
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
    }

}
