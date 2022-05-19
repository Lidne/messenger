package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ComponentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.media.metrics.Event;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import FirebaseModels.Chat;
import FirebaseModels.Message;
import JavaClasses.MessageController;
import JavaClasses.RSA;

public class ChatActivity extends Activity {
    private String TAG = "ChatActivity";

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private Chat chat;
    private DocumentReference chatRef;
    private MessageController controller;

    private ImageView icon;
    private TextView warning;
    private EditText chatMessage;
    private Button sendButton;
    private RecyclerView chatWindow;
    private Button back;
    private TextView nickDisplay;
    private CollectionReference messages;
    private RSA rsa;

    private boolean generated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        chatMessage = findViewById(R.id.chatMessage);
        sendButton = findViewById(R.id.sendButton);
        sendButton.setEnabled(false);
        chatWindow = findViewById(R.id.chatWindow);
        back = findViewById(R.id.back);
        nickDisplay = findViewById(R.id.userNick);
        icon = findViewById(R.id.icon);
        warning = findViewById(R.id.warning);

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
                Log.d(TAG, "onTextChanged: " + s.toString().trim().length() + " generated: " + !generated);
                if (s.toString().trim().length() == 0 || !generated) {
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

        Bundle extras = getIntent().getExtras();
        if (extras == null) return;
        String chatId = extras.getString("chatId");
        chatRef = mFirestore.collection("chats").document(chatId);
        messages = chatRef.collection("messages");

        chatRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (!task.isSuccessful()) {
                    Log.d(TAG, "Can't load a file\n" + task.getException());
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
                    String publicKey = chat.getAnotherUser().get("public_key");
                    if (publicKey == null) {
                        Log.d(TAG, "onComplete1: " + generated);
                        icon.setVisibility(View.VISIBLE);
                        warning.setVisibility(View.VISIBLE);
                        generated = false;
                    } else {
                        rsa.setPublicKey(publicKey);
                        rsa.readPrivate(chatId);
                        icon.setVisibility(View.GONE);
                        warning.setVisibility(View.GONE);
                        generated = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                chatRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "Can't load a file\n" + task.getException());
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

                        if (another_user.get("public_key") == null) {
                            icon.setVisibility(View.VISIBLE);
                            warning.setVisibility(View.VISIBLE);
                            generated = false;
                        } else {
                            icon.setVisibility(View.GONE);
                            warning.setVisibility(View.GONE);
                            generated = true;
                        }
                    }
                });

                chatRef.collection("messages").orderBy("date").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        FirebaseUser current_user = mAuth.getCurrentUser();
                        if (value.size() < 1) return;

                        for (DocumentChange docChange : value.getDocumentChanges()) {
                            if (docChange.getType() != DocumentChange.Type.ADDED) continue;
                            String decr_text = null;
                            DocumentSnapshot doc = docChange.getDocument();
                            if (!doc.get("user_id").equals(current_user.getUid())) {
                                try {
                                    decr_text = rsa.decrypt((String) doc.get("text"));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                controller.addMessage(
                                        new MessageController.Message(decr_text,
                                                (String) doc.get("nick"), !doc.get("user_id").equals(current_user.getUid()))
                                );
                            } else {
                                try {
                                    Message msg = readMessage(doc.getId());
                                    controller.addMessage(
                                            new MessageController.Message(msg.getText(), msg.getNick(), !doc.get("user_id").equals(current_user.getUid())));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
            }
        });
        Log.d(TAG, "onCreate: " + rsa);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void postMessage(View view) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IOException {
        // фукнция отправки сообщения
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DocumentReference message = messages.document();
        String text = chatMessage.getText().toString();
        String enc_text = rsa.encrypt(text);
        chatMessage.setText("");
        HashMap<String, Object> msg = new HashMap<String, Object>();
        Timestamp date = new Timestamp(new Date());
        msg.put("text", text);
        msg.put("nick", chat.getUser().get("nick"));
        msg.put("user_id", currentUser.getUid());
        saveMessage(message.getId(), msg);

        msg.put("text", enc_text);
        msg.put("date", date);
        message.set(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                chatRef.update("last_message", text);
            }
        });
    }

    private void saveMessage(String id, HashMap<String, Object> message) throws IOException {
        Context context = ChatActivity.this;
        File path = new File(context.getFilesDir() + "/" + chat.getChatId() + "/messages");
        if (!path.exists()) path.mkdirs();
        File message_file = new File(context.getFilesDir() + "/" + chat.getChatId() + "/messages/" + id);
        if (!message_file.exists()) {
            if (!message_file.createNewFile()) return;
        }
        FileOutputStream message_file_out = new FileOutputStream(message_file);
        Gson gson = new Gson();
        message_file_out.write(gson.toJson(message).getBytes(StandardCharsets.UTF_8));
        message_file_out.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Message readMessage(String id) throws IOException {
        Context context = ChatActivity.this;
        File message_file = new File(context.getFilesDir() + "/" + chat.getChatId() + "/messages/" + id);
        byte[] messageBytes = Files.readAllBytes(message_file.toPath());
        String message = new String(messageBytes, StandardCharsets.UTF_8);
        Gson gson = new Gson();
        Message msg = gson.fromJson(message, (Type) Message.class);
        return msg;
    }

    public void goBack(View view) {
        finish();
    }
}