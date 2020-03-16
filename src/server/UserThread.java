package server;

import java.io.*;
import java.net.Socket;

class UserThread extends Thread {

    private Socket socket;
    public DataInputStream in;
    public DataOutputStream out;

    public UserThread(Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        start();
    }
    @Override
    public void run() {
        try {
            while (true) {
                String userLogin = in.readUTF();
                String userPassword = in.readUTF();

                //TODO
                if(false){
                    out.writeUTF("SERVER");
                    out.writeUTF("PROBLEMS WITH AUTHENTICATION");
                    socket.close();
                    Server.users.remove(userLogin, socket);
                    break;
                }

                String receiver = in.readUTF();
                String userMessage = in.readUTF();

                UserThread receiverThread;
                try {
                    receiverThread = Server.users.get(receiver);
                    receiverThread.send(userLogin, userMessage);
                }
                catch (Exception e){
                    send("SERVER", "USER NOT FOUND");
                }

            }

        } catch (IOException e) {

        }
    }

    private void send(String userLogin, String userMessage) {
        try {
            if(socket.isClosed()){
                Server.users.remove(socket);
                return;
            }
            out.writeUTF(userLogin);
            out.writeUTF(userMessage);
        }
        catch (IOException ignored) {

        }
    }
}