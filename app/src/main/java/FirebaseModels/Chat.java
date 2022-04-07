package FirebaseModels;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chat {
    private HashMap<String, Map<String, Object>> users;

    public Chat() {}

    public Chat(HashMap<String, Map<String, Object>> users) {
        this.users = users;
    }

    public HashMap<String, Map<String, Object>> getUsers() {
        return users;
    }

    public void setUsers(HashMap<String, Map<String, Object>> users) {
        this.users = users;
    }
}
