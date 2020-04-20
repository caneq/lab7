package ServerAPI;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    public String sender;
    public String receiver;
    public String message;

    public Message(String sender, String receiver, String message){
        this.sender = sender;
        this.message = message;
        this.receiver = receiver;
    }

}
