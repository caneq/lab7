package client;

import ServerAPI.Excepsions.LoginAlreadyRegistered;
import ServerAPI.Excepsions.WrongLogin;
import ServerAPI.Excepsions.WrongPassword;
import ServerAPI.ServerAPI;
import ServerAPI.*;
import java.util.Scanner;

public class Main {


    public Main(){}


    public static void main(String[] args){
        new Main().run();
    }

    public void run(){
        Scanner scanner = new Scanner(System.in);


        ServerAPI server = new ServerAPI();

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
