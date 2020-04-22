package server;

import ServerAPI.Excepsions.WrongLogin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class UserDb {

    static final String path = "D:/lab/java/lab7/users.txt";
    static final String delimiter = ":";

    public UserDb(){

    }

    public synchronized void addUser(User user){
        try {
            PrintWriter w = new PrintWriter(new FileWriter(path, true));
            w.println(user.getLogin() + delimiter + user.getPassword());
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized User getUser(String login) throws WrongLogin {

        Scanner input = null;
        try {
            input = new Scanner(new FileReader(path));
            while(input.hasNextLine()){
                String[] logPass = input.nextLine().split(delimiter);

                if (logPass[0].equals(login) && logPass.length == 2) {
                    return new User(logPass[0], logPass[1]);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException");
            throw new WrongLogin();
        }
        throw new WrongLogin();
    }
}
