package com.example.mynote;

import android.content.Intent;
import android.os.BatteryManager;
import android.os.Bundle;
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

public class edit extends AppCompatActivity {
    Intent data;
    EditText eNoteTitle, eNoteContent;
    FirebaseFirestore editNote;
    ProgressBar editBar;
    FirebaseUser anon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        if(batLevel < 50){
            setTheme(R.style.AppTheme_noBar_Dark);

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        editBar = findViewById(R.id.editProgressBar);

        editNote = FirebaseFirestore.getInstance();

        data = getIntent();
        eNoteTitle = findViewById(R.id.eNTitle);
        eNoteContent = findViewById(R.id.eNContent);

        String title = data.getStringExtra("title");
        String content = data.getStringExtra("content");


        eNoteTitle.setText(title);
        eNoteContent.setText(content);

        anon = FirebaseAuth.getInstance().getCurrentUser();

        FloatingActionButton fab = findViewById(R.id.editFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = eNoteTitle.getText().toString();
                String content = eNoteContent.getText().toString();

                if(title.isEmpty() || content.isEmpty()) {
                    Toast.makeText(edit.this, "Missing fields...", Toast.LENGTH_SHORT).show();
                    return;
                }
                editBar.setVisibility(View.VISIBLE);
                DocumentReference ref = editNote.collection("notes").document(anon.getUid()).collection("myNotes").document(data.getStringExtra("noteID"));
                Map<String,Object> note = new HashMap<>();
                note.put("title",title);
                note.put("content",content);

                ref.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(edit.this, "Edited note!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(edit.this, "Could not edit note!", Toast.LENGTH_SHORT).show();
                        editBar.setVisibility(View.INVISIBLE);
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
            Toast.makeText(this, "Cancelled edit.", Toast.LENGTH_SHORT).show();
            onBackPressed();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
