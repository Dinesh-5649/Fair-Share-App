package com.example.demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.content.Context;
import com.google.android.material.floatingactionbutton.FloatingActionButton;




import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class GroupChat extends AppCompatActivity {

    ListView chatListView;
    ArrayList<Message> messages;
    EditText et;
    Button bt;
    FloatingActionButton fb;
    ChatAdapter chatAdapter;
    MyDatabase db;
    int groupId;
    int senderId;
    String senderName;
    String groupName;

    Handler handler = new Handler();
    Runnable refreshMessages = new Runnable() {
        @Override
        public void run() {
            messages.clear();
            messages.addAll(db.getMessagesByGroup(groupId));
            chatAdapter.notifyDataSetChanged();
            handler.postDelayed(this, 2000); // refresh every 2 seconds
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_chat);
        bt = findViewById(R.id.bt);
        fb = findViewById(R.id.fb);
        et = findViewById(R.id.et);
        chatListView = findViewById(R.id.lv);

        Intent i = getIntent();
        groupName = i.getStringExtra("groupName");
        senderName = i.getStringExtra("userName");
        groupId = i.getIntExtra("groupId",0);


        db = new MyDatabase(this);
        senderId = db.getUserIdByUsername(senderName);


        chatListView = findViewById(R.id.lv);
        messages = db.getMessagesByGroup(groupId);
        chatAdapter = new ChatAdapter((Context) this, messages);
        chatListView.setAdapter(chatAdapter);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GroupChat.this,ShowMembers.class);
                i.putExtra("groupId",groupId);
                i.putExtra("groupName",groupName);
                startActivity(i);
            }
        });

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mes = et.getText().toString().trim();
                if(!mes.isEmpty()){
                    db.addMessage(groupId,senderId, mes);
                    et.setText("");
                    messages.clear();
                    messages.addAll(db.getMessagesByGroup(groupId));
                    chatAdapter.notifyDataSetChanged();

                    chatListView.setSelection(chatAdapter.getCount() - 1);
                }

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(refreshMessages); // start periodic updates
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(refreshMessages); // stop refreshing when activity not visible
    }
}
