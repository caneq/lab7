package ConsoleClient;

import ServerAPI.MessengerClient;
import ServerAPI.*;
import java.util.Scanner;

public class Main {


    public Main(){}


    public static void main(String[] args){
        new Main().run();
    }

    public void run(){
        Scanner scanner = new Scanner(System.in);
        MessengerClient server;

        try {
            server = new MessengerClient("localhost", 1155);
        }
        catch (Exception e){
            System.out.println("Server not found");
            return;
        }


        while (true) {
            System.out.println("login or register");
            String command = scanner.nextLine();
            try {
                if (command.equals("login")) {
                    String loginPassword = scanner.nextLine();
                    String[] logpass = loginPassword.split(" ");

                    server.login(logpass[0], logpass[1]);
                    break;
                } else if (command.equals("register")) {
                    String loginPassword = scanner.nextLine();
                    String[] logpass = loginPassword.split(" ");

                    server.register(logpass[0], logpass[1]);
                    break;
                }
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
        }

        System.out.println("logged in");

        server.addMessageListener(message -> {
            System.out.println(message.sender + " -> " + message.receiver + ": " + message.message);
        });
        server.addUsersOnlineListener(users -> {
            System.out.println("Users online: ");
            for(String str : users){
                System.out.println(str);
            }
            System.out.println("");
        });

        while (scanner.hasNextLine()){
            try {
                String line = scanner.nextLine();
                String destination = line.substring(0, line.indexOf(" "));
                String message = line.substring(line.indexOf(" ") + 1);
                Message m = new Message("", destination, message);
                server.sendMessage(m);
            }
            catch (Exception e){
                System.out.println(e.getClass().getSimpleName());
            }
        }

    }
}
