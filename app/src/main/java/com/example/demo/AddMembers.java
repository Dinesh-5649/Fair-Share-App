package com.example.demo;
//Hello

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddMembers extends AppCompatActivity {
    Button bt1,bt2;
    TextView tv;
    EditText et;
    String groupName;
    int groupId;
    MyDatabase db = new MyDatabase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.add_members);

        Intent j = getIntent();
        groupName = j.getStringExtra("groupName");
        groupId = j.getIntExtra("groupId",0);

        bt1 = findViewById(R.id.bt1);
        bt2 = findViewById(R.id.bt2);
        et = findViewById(R.id.et);
        tv = findViewById(R.id.tv);

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = et.getText().toString().trim();
                db.addMember(userName,groupId);
                et.setText("");

            }
        });
    }
}