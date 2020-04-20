package ServerAPI.Excepsions;

public class UserNotFound extends Exception {
    public UserNotFound() {}

    public UserNotFound(String message)
    {
        super(message);
    }
}
