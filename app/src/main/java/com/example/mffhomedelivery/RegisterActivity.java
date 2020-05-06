package com.example.mffhomedelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Model.User;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    DatabaseReference users;
    private EditText nameET, phoneET, emailET, passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Initialising Firebase com.example.mffhomedelivery.Database and Authentication.
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        //Back Button Listener.
        FloatingActionButton backBtn = findViewById(R.id.signUpBackButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        emailET = findViewById(R.id.signEmail);
        passwordET = findViewById(R.id.signPassword);
        nameET = findViewById(R.id.signName);
        phoneET = findViewById(R.id.signPhone);

        TextView register = findViewById(R.id.signRegister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validate the entered data.
                if (TextUtils.isEmpty(nameET.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "Enter Valid Name", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(emailET.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(passwordET.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "Enter Valid Password", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(phoneET.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "Enter Valid Phone", Toast.LENGTH_SHORT).show();
                } else if (phoneET.getText().toString().length() != 10) {
                    Toast.makeText(RegisterActivity.this, "Phone length has to be 10", Toast.LENGTH_SHORT).show();
                } else if (passwordET.getText().toString().length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password length has to be more than 6", Toast.LENGTH_SHORT).show();
                } else {

                    final String email = emailET.getText().toString();
                    final String password = passwordET.getText().toString();
                    final String name = nameET.getText().toString();
                    final String phone = phoneET.getText().toString();

                    //Create a new user using Firebase Authentication.
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        final FirebaseUser firebaseUser = mAuth.getCurrentUser();

                                        //Create a User object to pass to the database.
                                        User user = new User();
                                        user.setEmail(email);
                                        user.setName(name);
                                        user.setPassword(password);
                                        user.setPhone(phone);

                                        //Passing the User object to the database with phone number being the Key value.
                                        users.child(phone).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                updateUI(firebaseUser);
                                            }
                                        });
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                        updateUI(null);
                                    }
                                }
                            });
                }
            }
        });
    }

    //Method to move to new activity upon authentication.
    private void updateUI(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
