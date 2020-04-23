package GuiClient;

import ServerAPI.Message;

public interface MessageSender {
    public void send(Message message);
}
