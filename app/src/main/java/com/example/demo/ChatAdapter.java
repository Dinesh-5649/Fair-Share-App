package com.example.demo;

import static android.content.Intent.getIntent;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.ArrayList;
public class ChatAdapter extends ArrayAdapter<Message> {

    final private Context context;
    final private ArrayList<Message> messages;
    String userName;
    private static final int MSG_TYPE_OTHERS = 0;
    private static final int MSG_TYPE_CURRENT_USER = 1;

    public ChatAdapter(Context context, ArrayList<Message> messages,String userName) {
        super(context, 0, messages);
        this.context = context;
        this.messages = messages;
        this.userName = userName;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);

        if (message.getSenderName().equals(userName)) {
            return MSG_TYPE_CURRENT_USER;
        } else {
            return MSG_TYPE_OTHERS;
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        int viewType = getItemViewType(position);
        Message message = messages.get(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);

            if(viewType==MSG_TYPE_OTHERS){
                convertView = inflater.inflate(R.layout.chat_item1, parent,false);
            }

            if(viewType==MSG_TYPE_CURRENT_USER){
                convertView = inflater.inflate(R.layout.chat_item2, parent,false);
            }
        }

        TextView tvSender = convertView.findViewById(R.id.tvSender);
        TextView tvMessage = convertView.findViewById(R.id.tvMessage);
        TextView tvTime = convertView.findViewById(R.id.tvTime);

        tvSender.setText(message.getSenderName());
        tvMessage.setText(message.getMessageText());
        tvTime.setText(message.getTimestamp());

        return convertView;
    }
}

