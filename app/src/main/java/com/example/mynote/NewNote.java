package com.example.mynote;

import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewNote extends AppCompatActivity {
    FirebaseFirestore notes;
    EditText noteTitle, noteContent;
    ProgressBar savenote;
    FirebaseUser anon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        if(batLevel < 50){
            setTheme(R.style.AppTheme_noBar_Dark);

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        notes = FirebaseFirestore.getInstance();
        noteTitle = findViewById(R.id.aNTitle);
        noteContent = findViewById(R.id.aNDetails);
        savenote = findViewById(R.id.aNProgressBar);

        anon = FirebaseAuth.getInstance().getCurrentUser();



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = noteTitle.getText().toString();
                String content = noteContent.getText().toString();

                if(title.isEmpty() || content.isEmpty()) {
                    Toast.makeText(NewNote.this, "Missing fields...", Toast.LENGTH_SHORT).show();
                    return;
                }
                savenote.setVisibility(View.VISIBLE);
                DocumentReference ref = notes.collection("notes").document(anon.getUid()).collection("myNotes").document();
                Map<String,Object> note = new HashMap<>();
                note.put("title",title);
                note.put("content",content);

                ref.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(NewNote.this, "Added note!", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewNote.this, "Could not add note!", Toast.LENGTH_SHORT).show();
                        savenote.setVisibility(View.INVISIBLE);
                    }
                });

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.close, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.close){
            Toast.makeText(this, "Cancelled Note.", Toast.LENGTH_SHORT).show();
            onBackPressed();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("savedContent", noteContent.getText().toString());
        Log.d("Saved Content", String.valueOf(outState.get("savedContent")));
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    public void finish() {
        super.finish();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
