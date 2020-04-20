package client;

public class ServerAPI {

    public void register(String login, String password){

    }

    public void login(String login, String password){

    }

    public void sendMessage(String receiver, String message){

    }

    public Message receiveMessage(String receiver, String message){
        return new Message("keker", "haloy");
    }

}
