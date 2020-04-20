package ServerAPI;

import client.ServerAPI;

public interface MessageListener {
    void messageReceived(Message message);
}
