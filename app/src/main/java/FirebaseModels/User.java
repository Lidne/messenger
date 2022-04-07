package FirebaseModels;

public class User {
    private String email;
    private String nick;
    private String avatar;

    public User() {}

    public User(String email, String nick, String avatar) {
        this.email = email;
        this.nick = nick;
        this.avatar = avatar;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
