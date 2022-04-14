package com.example.messenger;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.common.internal.ServiceSpecificExtraArgs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import FirebaseModels.Chat;
import FirebaseModels.Message;
import FirebaseModels.User;
import JavaClasses.ChatAdapter;
import JavaClasses.RSA;

public class MainActivity extends Activity {
    private final int LOGIN_REQUEST = 20;
    private final int ADD_CHAT_REQUEST = 30;
    private final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser user;

    private TextView username;
    private Button addChat;
    private ListView chatList;
    private EditText userEmail;
    private Chat[] chat_arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.user_name);
        addChat = findViewById(R.id.addChats);

        chatList = findViewById(R.id.chatList);
        chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, ChatActivity.class);
                Chat chat = chat_arr[position];
                i.putExtra("chatId", chat.getChatId());
                startActivity(i);
            }
        });

        userEmail = findViewById(R.id.personName);


        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAuth();

        if (mAuth.getCurrentUser() == null) return;
        mFirestore.collection("chats")
                .whereNotEqualTo(mAuth.getCurrentUser().getUid(), null)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        updateChats();
                    }
                });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /*private void listenForTranslations() {
        CollectionReference translationsRef = mFirestore
    }*/

    public void addChats(View target) {
        Intent i = new Intent(MainActivity.this, AddChatActivity.class);
        startActivityForResult(i, ADD_CHAT_REQUEST);
    }

    public void profile(View target) {
        Intent i = new Intent(MainActivity.this, ProfileActivity.class);
        //i.setData(Uri.parse(url));
        startActivityForResult(i, LOGIN_REQUEST);
    }

    private void checkAuth() {
        if (mAuth.getCurrentUser() == null) {
            Intent i = new Intent(MainActivity.this, AuthActivity.class);
            startActivityForResult(i, LOGIN_REQUEST);
        } else {
            user = mAuth.getCurrentUser();
            final DocumentReference docUser = mFirestore.collection("users").document(user.getUid());
            docUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) username.setText(user.getNick());
                    else {
                        Toast.makeText(MainActivity.this, R.string.user_fail,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void updateChats() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mFirestore.collection("chats")
                .whereNotEqualTo(currentUser.getUid(), null)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Chat[] chats = new Chat[task.getResult().size()];
                if (task.isSuccessful()) {
                    int i = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        HashMap<String, String> user = new HashMap<>();
                        HashMap<String, String> another_user = new HashMap<>();
                        String last_message = "";
                        Map<String, Object> data = document.getData();
                        for (String key : data.keySet()) {
                            if (key.equals(currentUser.getUid())) {
                                user = (HashMap<String, String>) data.get(currentUser.getUid());
                            } else if (!key.equals("last_message")) {
                                another_user = (HashMap<String, String>) data.get(key);
                            } else {
                                last_message = (String) data.get("last_message");
                            }
                        }
                        if (user.get("public_key") == null) {
                            try {
                                RSA rsa = new RSA(MainActivity.this, true);
                                rsa.writeKeys(document.getId());
                                user.put("public_key", rsa.getStrPublicKey());
                                mFirestore.collection("chats").document(document.getId())
                                        .update(currentUser.getUid(), user.clone());
                            } catch (Exception err) {
                                Log.d(TAG, "onEvent: " + err);
                            }
                        }
                        chats[i] = new Chat(document.getId(), user, another_user, last_message);
                        i++;
                    }
                    chat_arr = chats.clone();
                    chatList.setAdapter(new ChatAdapter(MainActivity.this, chats, mAuth));
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOGIN_REQUEST:
                if (resultCode == RESULT_OK) {
                    checkAuth();
                }
        }
    }
}