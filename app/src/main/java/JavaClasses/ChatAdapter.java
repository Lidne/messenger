package JavaClasses;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.messenger.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import FirebaseModels.Chat;
import FirebaseModels.User;


public class ChatAdapter extends ArrayAdapter<Chat> {
    private FirebaseAuth mAuth;
    private final String TAG = "ChatAdapter";

    public ChatAdapter(Context context, Chat[] arr, FirebaseAuth mAuth) {
        super(context, R.layout.chat, arr);
        this.mAuth = mAuth;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Chat chat = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat, null);
        }

        ((TextView) convertView.findViewById(R.id.userName)).setText(chat.getAnotherUser().get("nick"));
        ((TextView) convertView.findViewById(R.id.lastMessage)).setText(chat.getLastMessage());

        return convertView;
    }
}