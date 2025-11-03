package com.example.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ShowMembers extends AppCompatActivity {
    TextView tv;
    Button bt;
    ListView lv;
    String groupName;
    int groupId;
    MyDatabase db = new MyDatabase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.show_members);

        Intent i = getIntent();
        groupName = i.getStringExtra("groupName");
        groupId = i.getIntExtra("groupId",0);

        tv = findViewById(R.id.tv);
        bt = findViewById(R.id.button2);
        lv = findViewById(R.id.lv);
        tv.setText(groupName);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(ShowMembers.this,AddMembers.class);
                j.putExtra("groupId",groupId);
                j.putExtra("groupName",groupName);
                startActivity(j);
            }
        });

    }
    @Override
    protected void onResume(){

        super.onResume();
        updateMembers();
    }
    public void updateMembers(){
        ArrayList<String> members = new ArrayList<>();
        members = db.getMembersByGroup(groupId);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,members);
        lv.setAdapter(adapter);

    }
}