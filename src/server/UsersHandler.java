package server;

import ServerAPI.Excepsions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class UsersHandler {
    private UserAuthorizator userAuthorizator;
    private HashMap<String, UserThread> userThreads;

    UsersHandler(UserAuthorizator userAuthorizator) {
        userThreads = new HashMap();
        this.userAuthorizator = userAuthorizator;
    }

    private void addUser(String name, UserThread userThread){
        synchronized (userThreads){
            userThreads.put(name, userThread);
        }
        ArrayList<String> users = getOnlineUsers();
        for(UserThread user : userThreads.values()){
            user.sendUsersOnline(users);
        }
    }

    public void removeUser(String name){
        synchronized (userThreads){
            userThreads.remove(name);
        }
        ArrayList<String> users = getOnlineUsers();
        for(UserThread user : userThreads.values()){
            user.sendUsersOnline(users);
        }
    }

    public UserThread findUser(String name) throws UserNotFound {
        synchronized (userThreads){
            UserThread user = userThreads.get(name);
            if (user != null) {
                return userThreads.get(name);
            }
            throw new UserNotFound();
        }
    }

    public boolean logIn(String user, String password, UserThread thread) throws WrongPassword, UserAlreadyOnline, WrongLogin {
        if(userAuthorizator.logIn(user, password)) {

            if (userThreads.get(user) != null) {
                throw new UserAlreadyOnline();
            }
            else{
                System.out.println(user + " loggedIn");
                addUser(user, thread);
                return true;
            }

        }
        return false;
    }

    public ArrayList<String> getOnlineUsers(){
        return new ArrayList<String>(userThreads.keySet());
    }

    public boolean register(String user, String password, UserThread thread) throws LoginAlreadyRegistered {
        if(userAuthorizator.register(user, password)){
            System.out.println(user + " registered and logged in");
            addUser(user, thread);
            return true;
        }
        return false;

    }

}
