package com.example.mynote.auth;

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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class register extends AppCompatActivity {

    EditText name, email,password,confirmpassword;
    Button register;
    TextView login;
    ProgressBar registerBar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        if(batLevel < 50){
            setTheme(R.style.AppTheme_Dark);

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Register account");

        name = findViewById(R.id.registerName);
        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.password);
        confirmpassword = findViewById(R.id.passwordConfirm);
        register = findViewById(R.id.createAccount);
        login = findViewById(R.id.login);
        registerBar = findViewById(R.id.progressBar4);
        auth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String regName = name.getText().toString();
                String regEmail = email.getText().toString();
                String regPassword = password.getText().toString();
                String regConfPassword = confirmpassword.getText().toString();

                if (regName.isEmpty() || regEmail.isEmpty() || regPassword.isEmpty() || regConfPassword.isEmpty()) {
                    Toast.makeText(register.this, "Enter all fields!", Toast.LENGTH_SHORT).show();

                } else if (!regPassword.equals(regConfPassword)) {
                    Toast.makeText(register.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                } else if (regPassword.length() < 6) {
                    Toast.makeText(register.this, "Passwords must be longer than 6 characters.", Toast.LENGTH_SHORT).show();

                } else {
                    registerBar.setVisibility(View.VISIBLE);
                    AuthCredential credential = EmailAuthProvider.getCredential(regEmail, regPassword);
                    auth.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(register.this, "Register complete.", Toast.LENGTH_SHORT).show();


                            FirebaseUser user = auth.getCurrentUser();
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(regName)
                                    .build();
                            user.updateProfile(request);

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(register.this, "Failed to register, try again. Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            registerBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), login.class));
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Cancelled account creation.", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
        finish();
    }
}
