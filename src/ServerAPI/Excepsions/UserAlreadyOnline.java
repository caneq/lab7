package ServerAPI.Excepsions;

public class UserAlreadyOnline  extends Exception{
    public UserAlreadyOnline() {}

    public UserAlreadyOnline(String message)
    {
        super(message);
    }
}
