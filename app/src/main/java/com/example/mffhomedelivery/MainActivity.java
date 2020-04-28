package com.example.mffhomedelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView login = findViewById(R.id.loginButton);

        // log in button listener
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to login activity
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        TextView register = findViewById(R.id.RegisterLink);

        // register button listener
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go to register activity
                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }
}
