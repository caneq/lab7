package ServerAPI;

import java.util.LinkedList;

public class ServerAPI {
    LinkedList<MessageListener> listeners;

    public void register(String login, String password){

    }

    public void login(String login, String password){

    }

    public void sendMessage(Message message){

    }

    public void addMessageListener(MessageListener listener){
        synchronized (listeners) {
            this.listeners.add(listener);
        }
    }

    public void removeMessageListener(MessageListener listener){
        synchronized (listeners) {
            this.listeners.remove(listener);
        }
    }

    private void notifyListeners(Message message) {
        synchronized(listeners) {
            for(MessageListener listener : listeners) {
                listener.messageReceived(message);
            }
        }
    }

}
