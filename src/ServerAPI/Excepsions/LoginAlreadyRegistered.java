package ServerAPI.Excepsions;

import ServerAPI.ServerAPI;

public class LoginAlreadyRegistered  extends Exception{
    public LoginAlreadyRegistered() {}

    public LoginAlreadyRegistered(String message)
    {
        super(message);
    }
}
