package com.example.messenger;

import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
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
        controller.setOutgoingLayout(R.layout.message);
        controller.setMessageTextId(R.id.message_text);
        controller.setUserNameId(R.id.username);
        controller.setMessageTimeId(R.id.message_time);
        controller.appendTo(chatWindow, this);

        controller.addMessage(
                new MessageController.Message("gg nnadnadawdjadwjaedjawiddad",
                        "GrG", true)
        );

        controller.addMessage(
                new MessageController.Message("hallo im anda da water",
                        "Indus", false)
        );
    }
}