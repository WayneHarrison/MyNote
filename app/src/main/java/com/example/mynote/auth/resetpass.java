package com.example.mynote.auth;

import android.os.BatteryManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mynote.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class resetpass extends AppCompatActivity {

    EditText email;
    Button resetBtn;
    ProgressBar resetBar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        if(batLevel < 50){
            setTheme(R.style.AppTheme_Dark);

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpass);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Forgotten Password");

        email = findViewById(R.id.resetEmail);
        resetBtn = findViewById(R.id.resetBtn);
        resetBar = findViewById(R.id.progressBar5);
        auth = FirebaseAuth.getInstance();

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String resetEmail = email.getText().toString();
                auth.sendPasswordResetEmail(resetEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(resetpass.this, "Reset Email Sent.", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(resetpass.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        finish();
        return super.onOptionsItemSelected(item);
    }
}
