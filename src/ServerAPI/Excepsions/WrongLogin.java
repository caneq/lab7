package ServerAPI.Excepsions;

public class WrongLogin extends Exception{
    public WrongLogin() {}

    public WrongLogin(String message)
    {
        super(message);
    }
}
