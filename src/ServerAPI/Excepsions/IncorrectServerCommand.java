package ServerAPI.Excepsions;

public class IncorrectServerCommand extends Exception {
    public IncorrectServerCommand() {}

    public IncorrectServerCommand(String message)
    {
        super(message);
    }

}
