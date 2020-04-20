package server;

import ServerAPI.Excepsions.UserNotFound;

import java.util.HashMap;

public class UsersHandler {
    private HashMap<String, UserThread> userThreads;

    UsersHandler() {
        userThreads = new HashMap();
    }

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

    public UserThread findUser(String name) throws UserNotFound {
        synchronized (userThreads){
            UserThread user = userThreads.get(name);
            if (user != null) {
                return userThreads.get(name);
            }
            throw new UserNotFound();
        }
    }

    public boolean logIn(String user, String password){
        //TODO
        UserThread userThread = userThreads.get(user);
        if (userThread != null){
            userThread.dispose();
            userThread.interrupt();
        }
        return true;
    }

    public boolean register(String user, String password){
        //TODO
        UserThread userThread = userThreads.get(user);
        if (userThread != null){
            userThread.dispose();
            userThread.interrupt();
        }

        return true;

    }

}
