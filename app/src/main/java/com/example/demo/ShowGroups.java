package com.example.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ShowGroups extends AppCompatActivity {

    FloatingActionButton button;
    TextView tv;
    EditText et;
    ListView lv;
    String name;

    MyDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.show_groups);
        Intent i = getIntent();
        name = i.getStringExtra("username");

        button = findViewById(R.id.button);
        tv = findViewById(R.id.tv);
        lv = findViewById(R.id.lv);
        tv.setText(""+name);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowGroups.this,AddGroups.class);
                intent.putExtra("name",name);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        updateGroup();
    }

    public void updateGroup(){

        db= new MyDatabase(this);

        ArrayList<String> groups = db.getGroupsForUser(name);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,groups);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String groupName = groups.get(position);
                int groupId = db.getGroupIdByGroupName(groupName);
                Intent i = new Intent(ShowGroups.this,GroupChat.class);
                i.putExtra("groupId",groupId);
                i.putExtra("groupName",groupName);
                i.putExtra("userName",name);
                startActivity(i);
            }
        });
    }

}
