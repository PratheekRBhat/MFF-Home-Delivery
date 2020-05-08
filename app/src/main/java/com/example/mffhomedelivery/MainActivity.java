package com.example.mffhomedelivery;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mffhomedelivery.Common.Common;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

import Model.User;
import dmax.dialog.SpotsDialog;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {
    private static int APP_REQUEST_CODE = 1663;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener listener;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private List<AuthUI.IdpConfig> providers;

    AlertDialog dialog;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onStart() {
        super.onStart();
       mAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        if (listener != null)
            mAuth.removeAuthStateListener(listener);
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void updateUI(User currentUser) {
        Common.currentUser = currentUser;

        //Start activity
        Intent homeIntent = new Intent(MainActivity.this, Home.class);
        startActivity(homeIntent);
    }

    private void init() {
        providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(Common.USER_REF);
        mAuth = FirebaseAuth.getInstance();
        dialog = new SpotsDialog.Builder().setCancelable(false).setContext(this).build();
        listener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                checkUserFromFirebase(user);
            } else {
                phoneLogin();
            }
        };
    }

    private void checkUserFromFirebase(FirebaseUser user) {
        dialog.show();

        databaseReference.child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User user = dataSnapshot.getValue(User.class);
                            updateUI(user);
                        } else {
                            showRegisterDialog(user);
                        }

                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showRegisterDialog(FirebaseUser firebaseUser) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Register");
        builder.setMessage("Please fill out the form.");

        View itemView = LayoutInflater.from(this).inflate(R.layout.activity_register, null);
        EditText nameET = itemView.findViewById(R.id.txt_reg_name);
        EditText addressET = itemView.findViewById(R.id.txt_reg_address);
        EditText phoneET = itemView.findViewById(R.id.txt_reg_phone);

        phoneET.setText(firebaseUser.getPhoneNumber());

        builder.setView(itemView);
        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        builder.setPositiveButton("REGISTER", (dialogInterface, i) -> {
            if (TextUtils.isEmpty(nameET.getText().toString())) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(addressET.getText().toString())) {
                Toast.makeText(this, "Please enter your address", Toast.LENGTH_SHORT).show();
            }

            User user = new User();
            user.setUid(firebaseUser.getUid());
            user.setName(nameET.getText().toString());
            user.setAddress(addressET.getText().toString());
            user.setPhone(phoneET.getText().toString());

            databaseReference.child(firebaseUser.getUid()).setValue(user)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            dialogInterface.dismiss();
                            Toast.makeText(MainActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        }
                    });
        });

        builder.setView(itemView);

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void phoneLogin() {
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
                APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            } else {
                Toast.makeText(this, "Failed to sign in", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
