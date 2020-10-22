package com.example.mynote;

import android.content.Intent;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class fingerprint extends AppCompatActivity {
    private static final String TAG = fingerprint.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        if(batLevel < 50){
            setTheme(R.style.AppTheme_noBar_Dark);
            Toast.makeText(this, "Below 50% battery, setting darker background.", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "Above 50% battery, using lighter background.", Toast.LENGTH_SHORT).show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);

        Executor newExecutor = Executors.newSingleThreadExecutor();

        final FragmentActivity activity = this;

        final BiometricPrompt myBiometricPrompt = new BiometricPrompt(activity, newExecutor, new BiometricPrompt.AuthenticationCallback() {
            @Override

            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                } else {

                    Log.d(TAG, "An unrecoverable error occurred");
                }
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                Log.d(TAG, "Fingerprint recognised successfully");
                startActivity(new Intent(getApplicationContext(), splash.class));
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

                Log.d(TAG, "Fingerprint not recognised");
            }
        });

        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()


                .setTitle("Authenticator")
                .setSubtitle("Place your finger on the sensor.")
                .setNegativeButtonText("Cancel")
                .build();

        findViewById(R.id.launchAuthentication).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBiometricPrompt.authenticate(promptInfo);
            }
        });
    }
}
