package server;

import java.util.HashMap;

public class UsersHandler {
    private HashMap<String, UserThread> userThreads;

    UsersHandler() {}

    public void addUser(String name, UserThread userThread){
        synchronized (userThreads){
            userThreads.put(name, userThread);
        }
    }

    public void removeUser(String name){
        synchronized (userThreads){
            userThreads.remove(name);
        }
    }

    public UserThread findUser(String name){
        synchronized (userThreads){
            return userThreads.get(name);
        }
    }

    public boolean logIn(String user, String password){
        return true;
    }

    public boolean register(String user, String password){
        return true;
    }

}
