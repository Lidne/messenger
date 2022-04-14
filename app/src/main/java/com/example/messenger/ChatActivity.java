package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.media.metrics.Event;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import FirebaseModels.Chat;
import JavaClasses.MessageController;
import JavaClasses.RSA;

public class ChatActivity extends Activity {
    private String TAG = "ChatActivity";

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private Chat chat;
    private DocumentReference chatRef;
    private MessageController controller;

    private EditText chatMessage;
    private Button sendButton;
    private RecyclerView chatWindow;
    private Button back;
    private TextView nickDisplay;
    private CollectionReference messages;
    private RSA rsa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        chatMessage = findViewById(R.id.chatMessage);
        sendButton = findViewById(R.id.sendButton);
        chatWindow = findViewById(R.id.chatWindow);
        back = findViewById(R.id.back);
        nickDisplay = findViewById(R.id.userNick);

        chat = new Chat();
        controller = new MessageController();

        controller.setIncomingLayout(R.layout.message);
        controller.setOutgoingLayout(R.layout.outgoing_message);
        controller.setMessageTextId(R.id.messageText);
        controller.setUserNameId(R.id.username);
        controller.setMessageTimeId(R.id.message_time);
        controller.appendTo(chatWindow, this);

        // если поле для ввода пустое, то выключаем кнопку
        chatMessage.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() == 0) {
                    sendButton.setEnabled(false);
                } else {
                    sendButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();

        Bundle extras = getIntent().getExtras();
        if (extras == null) return;
        String chatId = extras.getString("chatId");
        chatRef = mFirestore.collection("chats").document(chatId);


        chatRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (!task.isSuccessful()) {
                    Log.d(TAG, "Can't load a file");
                    return;
                }
                HashMap<String, Object> data = (HashMap<String, Object>) task.getResult().getData();
                HashMap<String, String> user = new HashMap<>();
                HashMap<String, String> another_user = new HashMap<>();
                String last_message = "";

                for (String key : data.keySet()) {
                    if (key.equals(currentUser.getUid())) {
                        user = (HashMap<String, String>) data.get(currentUser.getUid());
                    } else if (!key.equals("last_message")) {
                        another_user = (HashMap<String, String>) data.get(key);
                    } else {
                        last_message = (String) data.get("last_message");
                    }
                }
                chat = new Chat(task.getResult().getId(), user, another_user, last_message);
                nickDisplay.setText(chat.getAnotherUser().get("nick"));

                try {
                    rsa = new RSA(ChatActivity.this, false);
                    rsa.setPublicKey(chat.getAnotherUser().get("public_key"));
                    rsa.readPrivate(chatId);
                    Log.d(TAG, "onComplete: private" + rsa.getPrivateKey());
                    Log.d(TAG, "onStart: public" + rsa.getPublicKey());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        chatRef.collection("messages").orderBy("date").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                FirebaseUser current_user = mAuth.getCurrentUser();
                if (value.size() < 1) return;
                for (DocumentSnapshot doc : value.getDocuments()) {
                    String decr_text = null;

                    if (!doc.get("user_id").equals(current_user.getUid()) && (boolean) doc.getData().get("read")) {
                        Log.d(TAG, "onEvent: " + doc.get("user_id") + "---" + current_user.getUid());
                        try {
                            decr_text = rsa.decrypt((String) doc.get("text"));
                            chatRef.collection("messages").document(doc.getId()).update("read", true);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        controller.addMessage(
                                new MessageController.Message(decr_text,
                                        (String) doc.get("nick"), true)
                        );
                    }
                }
            }
        });

        messages = mFirestore.collection("chats").document(chatId).collection("messages");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void postMessage(View view) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DocumentReference message = messages.document();
        String text = chatMessage.getText().toString();
        String enc_text = rsa.encrypt(text);
        HashMap<String, Object> msg = new HashMap<String, Object>();
        Timestamp date = new Timestamp(new Date());
        msg.put("date", date);
        msg.put("text", enc_text);
        msg.put("nick", chat.getUser().get("nick"));
        msg.put("user_id", currentUser.getUid());
        msg.put("read", false);
        message.set(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                chatRef.update("last_message", enc_text);
                controller.addMessage(
                        new MessageController.Message(text, chat.getUser().get("nick"), false)
                );
            }
        });
    }

    public void onClick(View view) {
        String usermessage = chatMessage.getText().toString();
        if (!usermessage.equals(""))
            controller.addMessage(
                    new MessageController.Message(usermessage,
                            "Niggward", true)
            );
        chatMessage.setText("");
    }
}