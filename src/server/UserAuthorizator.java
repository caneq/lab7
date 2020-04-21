package server;

import ServerAPI.Excepsions.*;

public class UserAuthorizator {
    UserDb db;

    public UserAuthorizator(UserDb db){
        this.db = db;
    }

    public boolean logIn(String user, String password) throws WrongLogin, WrongPassword {
        if (this.CheckLoginPassword(new User(user, password))){
            return true;
        }
        return false;
    }

    public boolean register(String user, String password) throws LoginAlreadyRegistered {
        try {
            db.getUser(user);
            throw new LoginAlreadyRegistered();

        } catch (WrongLogin userNotFound) {
            db.addUser(new User(user, password));
            return true;
        }
    }

    private boolean CheckLoginPassword(User user) throws WrongPassword, WrongLogin {

        String correctPassword = db.getUser(user.getLogin()).getPassword();
        String providedPassword = user.getPassword();

        if (providedPassword.equals(correctPassword) ){
            return true;
        }
        else throw new WrongPassword();

    }
}
