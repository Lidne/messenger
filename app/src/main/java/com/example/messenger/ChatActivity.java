package com.example.messenger;

import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class ChatActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        EditText chatMessage = (EditText) findViewById(R.id.chatMessage);
        Button sendButton = (Button) findViewById(R.id.sendButton);
        RecyclerView chatWindow = (RecyclerView) findViewById(R.id.chatWindow);

        MessageController controller = new MessageController();

        controller.setIncomingLayout(R.layout.message);
        controller.setOutgoingLayout(R.layout.outgoing_message);
        controller.setMessageTextId(R.id.messageText);
        controller.setUserNameId(R.id.username);
        controller.setMessageTimeId(R.id.message_time);
        controller.appendTo(chatWindow, this);

        controller.addMessage(
                new MessageController.Message("let's dance",
                        "GrG", true)
        );

        controller.addMessage(
                new MessageController.Message("say hi to niggward",
                        "Indus", false)
        );

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usermessage = chatMessage.getText().toString();
                if (!usermessage.equals(""))
                controller.addMessage(
                        new MessageController.Message(usermessage,
                                "Niggward", true)
                );
                chatMessage.setText("");
            }
        });

        // если поле для ввода пустое, то выключаем кнопку
        chatMessage.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().length()==0){
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
}