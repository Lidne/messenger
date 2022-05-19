package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
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
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import JavaClasses.RSA;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AddChatActivity extends Activity {
    private final String BASE_URL = "http://6080-176-194-244-143.ngrok.io";
    private final int ADD_CHAT_REQUEST = 30;
    private String TAG = "AddChatActivity";
    private final String directory = "/data/keys";

    private OkHttpClient client;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private EditText userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat);

        client = new OkHttpClient();
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        userEmail = findViewById(R.id.personName);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addChat(View target) {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(AddChatActivity.this, R.string.unauthorized, Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> users = new HashMap<>();

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
                users.put(snapshotDocuments.get(0).getId(), snapshotDocuments.get(0).getData());
                users.put("last_message", "");

                mFirestore.collection("users").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentReference doc = mFirestore.collection("chats").document();
                        String pub_key = "";
                        try {
                            RSA rsa = new RSA(getApplicationContext(), true);
                            rsa.writeKeys(doc.getId());
                            pub_key = Base64.getEncoder().encodeToString(rsa.getPublicKey().getEncoded());
                        } catch (Exception e) {
                            Log.d(TAG, "onComplete: " + e);
                        }
                        Map<String, Object> user = task.getResult().getData();
                        user.put("public_key", pub_key);
                        users.put(task.getResult().getId(), user);

                        doc.set(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                setResult(ADD_CHAT_REQUEST);
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }
}