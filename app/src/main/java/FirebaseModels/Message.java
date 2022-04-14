package FirebaseModels;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.Date;

public class Message {
    private String text;
    private Date date;
    private String userId;

    public Message() {
    }

    public Message(String text, Date date, String userId) {
        this.text = text;
        this.date = date;
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
