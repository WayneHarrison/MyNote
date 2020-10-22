package com.example.mynote;

import android.content.Intent;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class splash extends AppCompatActivity {
    FirebaseAuth userAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        if(batLevel < 50){
            setTheme(R.style.AppTheme_noBar_Dark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        userAuth = FirebaseAuth.getInstance();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){

                if(userAuth.getCurrentUser() != null){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
                else {
                    userAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(splash.this, "Temporary account created.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(splash.this, "Error!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }

            }
        }, 2000);
    }
}
