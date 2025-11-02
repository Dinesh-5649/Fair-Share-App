package com.example.demo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginPage extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin, btnRegister;
    MyDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        etUsername = findViewById(R.id.et1);
        etPassword = findViewById(R.id.et2);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        db = new MyDatabase(this);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();

            }

            if(db.registerUser(username, password)){
                Intent intent = new Intent(this, ShowGroups.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }

                etUsername.setText("");
                etPassword.setText("");

        });

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (db.loginUser(username, password)) {

                Intent intent = new Intent(this, ShowGroups.class);
                intent.putExtra("username", username);
                startActivity(intent);

            }
        });
    }
}
