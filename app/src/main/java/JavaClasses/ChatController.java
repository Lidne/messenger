package JavaClasses;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatController extends RecyclerView.Adapter {
    private List<Chat> chatList;
    private RecyclerView recyclerView;

    private static final int TYPE_INCOMING = 0;
    private static final int TYPE_OUTGOING = 1;
    private static final int MAX_MESSAGES = 1000;

    private int chatId;
    private int chatTimeId;
    private int userNameId;
    private int layout;
    //private int outgoingLayout;
    //private int incomingLayout;


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
    public class ChatView extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView messageTime;
        TextView userName;


        ChatView(@NonNull View itemView, int chatId, int chatTimeId, int userNameId) {
            super(itemView);
            messageText = itemView.findViewById(chatId);
            messageTime = itemView.findViewById(chatTimeId);
            userName = itemView.findViewById(userNameId);
        }

        void bind(Chat chat) {
            DateFormat fmt = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);
            messageText.setText(chat.lastMessage);
            messageTime.setText(fmt.format(chat.date));
            userName.setText(chat.userName);
        }
    }


    public ChatController setChatId(int chatId) {
        this.chatId = chatId;
        return this;
    }

    public ChatController setChatTimeId(int chatTimeId) {
        this.chatTimeId = chatTimeId;
        return this;
    }

    public ChatController setUserNameId(int userNameId) {
        this.userNameId = userNameId;
        return this;
    }

    public ChatController setOutgoingLayout(int layout) {
        this.layout = layout;
        return this;
    }

    public ChatController() {
        this.chatList = new ArrayList<>();

    }
    public void appendTo(RecyclerView recyclerView, Context parent) {
        this.recyclerView = recyclerView;
        this.recyclerView.setLayoutManager(new LinearLayoutManager(parent));
        this.recyclerView.setAdapter(this);
    }

    public void addChat(Chat m) {
        chatList.add(m);
        if (chatList.size() > MAX_MESSAGES) {
            chatList = chatList.subList(chatList.size() - MAX_MESSAGES, chatList.size());
        }
        this.notifyDataSetChanged();
        this.recyclerView.scrollToPosition(chatList.size() - 1);
    }

    /*@Override
    public int getItemViewType(int position) {
        Chat chat = chatList.get(position);
        return chat.isOutgoing ? TYPE_OUTGOING : TYPE_INCOMING;
    }*/

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int view_type) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);
        return new ChatView(view, chatId, chatTimeId, userNameId);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Chat chat = chatList.get(i);
        ((ChatView) viewHolder).bind(chat);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}
