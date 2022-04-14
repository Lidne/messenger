package FirebaseModels;

public class User {
    private String email;
    private String nick;

    public User() {}

    public User(String email, String nick) {
        this.email = email;
        this.nick = nick;
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
}
