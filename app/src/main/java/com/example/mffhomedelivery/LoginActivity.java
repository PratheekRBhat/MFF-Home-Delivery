package com.example.mffhomedelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailET, passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        FloatingActionButton backBtn = findViewById(R.id.loginBackButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        TextView Register = findViewById(R.id.LoginToRegisterLink);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        emailET = findViewById(R.id.email);
        passwordET = findViewById(R.id.password);

        TextView submit = findViewById(R.id.login);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(emailET.getText().toString())){
                    Toast.makeText(LoginActivity.this,"Enter Valid Email",Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(passwordET.getText().toString())){
                    Toast.makeText(LoginActivity.this,"Enter Valid Password",Toast.LENGTH_SHORT).show();
                } else if(passwordET.getText().toString().length()<6){
                    Toast.makeText(LoginActivity.this,"Password length has to be more than 6",Toast.LENGTH_SHORT).show();
                } else {
                    String email = emailET.getText().toString();
                    String password = passwordET.getText().toString();

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                        updateUI(firebaseUser);
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Incorrect Email ID or Password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }

    private void updateUI(FirebaseUser firebaseUser) {
        if (firebaseUser != null){
            Intent loginIntent = new Intent(LoginActivity.this, Home.class);
            startActivity(loginIntent);
        }
    }
}
