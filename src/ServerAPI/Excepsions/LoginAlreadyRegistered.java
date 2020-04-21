package ServerAPI.Excepsions;

public class LoginAlreadyRegistered  extends Exception{
    public LoginAlreadyRegistered() {}

    public LoginAlreadyRegistered(String message)
    {
        super(message);
    }
}
