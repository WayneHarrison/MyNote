package com.example.mynote.auth;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mynote.MainActivity;
import com.example.mynote.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class login extends AppCompatActivity {

    EditText email, password;
    Button login;
    TextView forgot, newaccount;
    FirebaseAuth auth;
    FirebaseFirestore notes;
    ProgressBar loginBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        if(batLevel < 50){
            setTheme(R.style.AppTheme_Dark);

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");

        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        login = findViewById(R.id.loginBtn);
        forgot = findViewById(R.id.forgotPassword);
        newaccount = findViewById(R.id.createAccount);
        auth = FirebaseAuth.getInstance();
        notes = FirebaseFirestore.getInstance();
        loginBar = findViewById(R.id.progressBar3);

        alert();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String loginEmail = email.getText().toString();
                String loginPassword = password.getText().toString();



                if(loginEmail.isEmpty() || loginPassword.isEmpty()){
                    Toast.makeText(login.this, "All fields required.", Toast.LENGTH_SHORT).show();
                }
                else {
                    loginBar.setVisibility(View.VISIBLE);
                    if(auth.getCurrentUser().isAnonymous()){
                        FirebaseUser cUser = auth.getCurrentUser();



                        notes.collection("notes").document(cUser.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(login.this, "Temporary notes deleted.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        cUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(login.this, "Anonymous account deleted.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        auth.signInWithEmailAndPassword(loginEmail, loginPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(login.this, "Signin Succesful.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(login.this, "Login failed. Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                loginBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                }
            }

        });
        newaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), register.class));
            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), resetpass.class));
            }
        });


    }

    private void alert() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("Current notes will be lost, create a new account to save them.")
                .setPositiveButton("Create Account", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), register.class));
                        finish();
                    }
                }).setNegativeButton("Signin", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        warning.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        finish();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
