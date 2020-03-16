package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;

public class Server {
    public static final int PORT = 1234;
    public static HashMap<String, UserThread> users = new HashMap<>();

    Server() {

    }

    public static void main (String[] args) throws IOException {
        System.out.println("Server started");
        ServerSocket server = new ServerSocket(PORT);
        try {
            while (true) {
                Socket socket = server.accept();
                UserThread userThread = new UserThread(socket);
                String userLogin = userThread.in.readUTF();
                String userPassword = userThread.in.readUTF();
                if (!users.containsKey(userLogin)) {
                    //TODO
                    if (true) {
                        users.put(userLogin, userThread);
                        System.out.println("new connection");
                    }
                }
                else {
                    userThread.out.writeUTF("ERROR");
                    userThread.out.writeUTF("USER ONLINE");
                }
            }
        } finally {
            server.close();
        }

    }
}
