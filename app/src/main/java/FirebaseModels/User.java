package FirebaseModels;

public class User {
    private String email;
    private String nick;
    private String user_id;

    public User() {
    }

    public User(String email, String nick, String user_id) {
        this.email = email;
        this.nick = nick;
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
