package FirebaseModels;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.Date;

public class Message {
    private String text;
    private String userId;
    private String nick;

    public Message() {
    }

    public Message(String text, String userId, String nick) {
        this.text = text;
        this.userId = userId;
        this.nick = nick;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    @Override
    public String toString() {
        return "Message{" +
                "text='" + text + '\'' +
                ", userId='" + userId + '\'' +
                ", nick='" + nick + '\'' +
                '}';
    }
}
