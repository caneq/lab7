package ServerAPI.Excepsions;

public class UnknownExcepsion extends Exception{
    public UnknownExcepsion() {}

    public UnknownExcepsion(String message)
    {
        super(message);
    }
}
