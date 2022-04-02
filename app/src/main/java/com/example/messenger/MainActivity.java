package com.example.messenger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class MainActivity extends Activity {
    private final int LOGIN_REQUEST = 20;
    private final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;
    private FirebaseUser user;

    private TextView username;
    private Button addChats;
    private ImageView avatar;
    private ListView chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.user_name);
        addChats = findViewById(R.id.addChats);
        ChatAdapter adapter = new ChatAdapter(this, addChats());
        avatar = findViewById(R.id.userIcon);

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                //i.setData(Uri.parse(url));
                startActivityForResult(i, LOGIN_REQUEST);
            }
        });

        chatList = findViewById(R.id.chatList);
        chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, ChatActivity.class);
                //i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        chatList.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAuth();
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

    private void checkAuth() {
        Log.d(TAG, "checkAuth: " + mAuth.getCurrentUser());
        if (mAuth.getCurrentUser() == null) {
            Intent i = new Intent(MainActivity.this, AuthActivity.class);
            startActivityForResult(i, LOGIN_REQUEST);
        } else {
            user = mAuth.getCurrentUser();
            //ImageLoadTask loadTask = new ImageLoadTask(user.getPhotoUrl().toString(), img);
            //loadTask.execute();
            // TODO: update UI
        }
    }

    private ChatAdapter.Chat[] addChats() {
        ChatAdapter.Chat[] arr = new ChatAdapter.Chat[5];
        arr[0] = new ChatAdapter.Chat("Купи пельмешек", "Niggward");
        arr[1] = new ChatAdapter.Chat("lET'S DANCE", "Jetstream Sam");
        arr[2] = new ChatAdapter.Chat("я как томас шелби", "Tomas Bebra");
        arr[3] = new ChatAdapter.Chat("я не успел сделать бэкэнд...", "Ura Lidne");
        arr[4] = new ChatAdapter.Chat("SUS SUS SUS SUS", "Amogus");
        return arr;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOGIN_REQUEST:
                if (resultCode == RESULT_OK) {
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
    }
}