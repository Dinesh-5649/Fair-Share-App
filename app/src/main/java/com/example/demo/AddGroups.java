package com.example.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddGroups extends AppCompatActivity {
    EditText group_name,group_member;

    Button bt1,bt2;
    final long[] groupId = {-1};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.add_groups);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("name");

        group_name = findViewById(R.id.et1);
        group_member = findViewById(R.id.et2);
        bt1 = findViewById(R.id.bt1);
        bt2 = findViewById(R.id.bt2);

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatabase my = new MyDatabase(AddGroups.this);
                groupId[0] = my.addGroup(group_name.getText().toString().trim(),userName);
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatabase my = new MyDatabase(AddGroups.this);

                my.addMember(group_member.getText().toString().trim(), (int) groupId[0]);
                group_member.setText("");
            }
        });


    }
}
