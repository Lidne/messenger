package JavaClasses;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.messenger.R;

import java.util.Date;


public class ChatAdapter extends ArrayAdapter<ChatAdapter.Chat> {

    public static class Chat {
        String lastMessage;
        Date date;
        String userName;
        Image userAvatar;

        public Chat(String lastMessage, String userName) {
            this.lastMessage = lastMessage;
            this.userName = userName;
            this.userAvatar = null;
            this.date = new Date();
        }
    }

    public ChatAdapter(Context context, Chat[] arr) {
        super(context, R.layout.chat, arr);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Chat chat = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat, null);
        }

        ((TextView) convertView.findViewById(R.id.userName)).setText(chat.userName);
        ((TextView) convertView.findViewById(R.id.lastMessage)).setText(chat.lastMessage);

        ImageView img = (ImageView) convertView.findViewById(R.id.chatAvatar);
        img.setImageResource(R.drawable.sam);

        return convertView;
    }
}