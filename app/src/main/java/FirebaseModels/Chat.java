package FirebaseModels;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Chat {
    private String chatId;
    private HashMap<String, String> user;
    private HashMap<String, String> anotherUser;
    private String lastMessage;

    public Chat() {}

    public Chat(String chatId, HashMap<String, String> user, HashMap<String, String> anotherUser, String lastMessage) {
        this.chatId = chatId;
        this.user = user;
        this.anotherUser = anotherUser;
        this.lastMessage = lastMessage;
    }

    public HashMap<String, String> getUser() {
        return user;
    }

    public void setUser(HashMap<String, String> user) {
        this.user = user;
    }

    public HashMap<String, String> getAnotherUser() {
        return anotherUser;
    }

    public void setAnotherUser(HashMap<String, String> anotherUser) {
        this.anotherUser = anotherUser;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "user=" + user +
                ", anotherUser=" + anotherUser +
                ", lastMessage='" + lastMessage + '\'' +
                '}';
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
