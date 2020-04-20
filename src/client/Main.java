package client;

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

        String loginPassword = scanner.nextLine();
        String[] logpass = loginPassword.split(" ");

        ServerAPI server = new ServerAPI();
        try {
            server.login(logpass[0], logpass[1]);
        } catch (WrongLogin wrongLogin) {
            System.out.println("wrongLogin");
        } catch (WrongPassword wrongPassword) {
            System.out.println("wrongPassword");
        }

        server.addMessageListener(message -> {
            System.out.println(message.sender + " -> " + message.receiver + ": " + message.message);
        });

        while (scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] args = line.split(" ");
            Message message = new Message("", args[0], args[1]);
            server.sendMessage(message);
        }

    }
}
