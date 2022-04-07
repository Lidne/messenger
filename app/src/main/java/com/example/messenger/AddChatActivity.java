package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import FirebaseModels.Chat;

public class AddChatActivity extends Activity {
    private final int ADD_CHAT_REQUEST = 30;
    private String TAG = "AddChatActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private EditText userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        userEmail = findViewById(R.id.personName);
    }

    public void addChat(View target) {
        Log.d(TAG, "addChat: ");
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(AddChatActivity.this, R.string.unauthorized, Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Map<String, Object>> users = new HashMap<>();

        Query anotherUserQuery = mFirestore.collection("users")
                .whereEqualTo("email", userEmail.getText().toString());

        anotherUserQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot snapshot = task.getResult();
                List<DocumentSnapshot> snapshotDocuments = snapshot.getDocuments();
                if (snapshotDocuments.isEmpty()) {
                    Toast.makeText(AddChatActivity.this, R.string.no_user, Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d(TAG, "onComplete: " + snapshotDocuments.get(0).getId());
                users.put(snapshotDocuments.get(0).getId(), snapshotDocuments.get(0).getData());
                mFirestore.collection("users").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        users.put(task.getResult().getId(), task.getResult().getData());
                        Chat chat = new Chat();
                        chat.setUsers(users);
                        Log.d(TAG, "addChat: " + chat.getUsers().toString());
                        mFirestore.collection("chats").document().set(chat);
                        setResult(ADD_CHAT_REQUEST);
                        finish();
                    }
                });
            }
        });
    }
}