package com.example.messenger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button some = findViewById(R.id.addChats);
        ChatAdapter adapter = new ChatAdapter(this, addChats());
        ImageView img = findViewById(R.id.userIcon);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                //i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        ListView lv = findViewById(R.id.chatList);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, ChatActivity.class);
                //i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        lv.setAdapter(adapter);
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
}