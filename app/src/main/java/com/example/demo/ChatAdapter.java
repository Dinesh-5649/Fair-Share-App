package com.example.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.content.Context;


import java.util.ArrayList;

public class ChatAdapter extends ArrayAdapter<Message> {

    final private Context context;
    final private ArrayList<Message> messages;

    public ChatAdapter(Context context, ArrayList<Message> messages) {
        super(context, 0, messages);
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
        }

        Message message = messages.get(position);

        TextView tvSender = convertView.findViewById(R.id.tvSender);
        TextView tvMessage = convertView.findViewById(R.id.tvMessage);
        TextView tvTime = convertView.findViewById(R.id.tvTime);

        tvSender.setText(message.getSenderName());
        tvMessage.setText(message.getMessageText());
        tvTime.setText(message.getTimestamp());

        return convertView;
    }
}

