package com.example.mynote;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.mynote.auth.login;
import com.example.mynote.auth.register;
import com.example.mynote.model.Note;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    RecyclerView notes;
    FirebaseFirestore fbnotes;
    FirestoreRecyclerAdapter<Note, NoteView> noteAdapter;
    FirebaseUser anon;
    FirebaseAuth auth;




    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        super.onCreate(savedInstanceState);
        if(batLevel < 50){
            setTheme(R.style.AppTheme_noBar_Dark);

        }
        setContentView(R.layout.activity_main);


        fbnotes = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        anon = auth.getCurrentUser();

        Query getNotes = fbnotes.collection("notes").document(anon.getUid()).collection("myNotes").orderBy("title", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> Notes = new FirestoreRecyclerOptions.Builder<Note>().setQuery(getNotes, Note.class).build();

        noteAdapter = new FirestoreRecyclerAdapter<Note, NoteView>(Notes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteView noteView, final int i, @NonNull final Note note) {
                noteView.nTitle.setText(note.getTitle());
                noteView.nContent.setText(note.getContent());
                final Integer colorCode = getRandomColor();
                final String noteID = noteAdapter.getSnapshots().getSnapshot(i).getId();



                noteView.cardView.setCardBackgroundColor(noteView.view.getResources().getColor(colorCode, null));


                noteView.view.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Intent i = new Intent(v.getContext(), NoteDetails.class);
                        i.putExtra("noteID", noteID);
                        i.putExtra("title", note.getTitle());
                        i.putExtra("content", note.getContent());
                        i.putExtra("color", colorCode);
                        v.getContext().startActivity(i);
                    }
                });

                ImageView menu = noteView.view.findViewById(R.id.menuIcon);
                menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v){
                        final String nID = noteAdapter.getSnapshots().getSnapshot(i).getId();
                        PopupMenu menu = new PopupMenu(v.getContext(),v);
                        menu.setGravity(Gravity.END);
                        menu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent i = new Intent(v.getContext(), edit.class);
                                i.putExtra("noteID", nID);
                                i.putExtra("title", note.getTitle());
                                i.putExtra("content", note.getContent());
                                startActivity(i);
                                return false;
                            }
                        });
                        menu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){

                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                final DocumentReference dRef = fbnotes.collection("notes").document(anon.getUid()).collection("myNotes").document(nID);
                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                builder.setCancelable(true);
                                builder.setTitle("Irreversible action!");
                                builder.setMessage("Do you really want to delete this? The note will be gone forever!");
                                builder.setPositiveButton("Confirm",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(MainActivity.this, "Item Deleted!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(MainActivity.this, "Could not delete item!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });
                                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(MainActivity.this, "Cancelled!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();


                                return false;
                            }
                        });

                        menu.show();
                    }

                });
            }

            @NonNull
            @Override
            public NoteView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout, parent, false);
                return new NoteView(view);
            }
        };

        notes = findViewById(R.id.nList);

        Toolbar toolbar = findViewById(R.id.ToolBar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        notes.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        notes.setAdapter(noteAdapter);

        View hView = navigationView.getHeaderView(0);
        TextView uName = hView.findViewById(R.id.userName);
        TextView uEmail = hView.findViewById(R.id.userEmail);

        if(anon.isAnonymous()){
            uEmail.setVisibility(View.INVISIBLE);
            uName.setText("Anonymous user");
        }
        else {
            uEmail.setText(anon.getEmail());
            uName.setText(anon.getDisplayName());
        }

        FloatingActionButton fab = findViewById(R.id.fabAddNote);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                startActivity(new Intent(view.getContext(), NewNote.class));

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch(item.getItemId()){
            case R.id.createNote:
                startActivity(new Intent(this, NewNote.class));
                break;
            case R.id.logout:
                checkUser();
                break;
            case R.id.sync:
                if(anon.isAnonymous()){
                    startActivity(new Intent(this, login.class));
                }
                else{
                    Toast.makeText(this, "You are already signed in.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Toast.makeText(this, "Coming Soon.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void checkUser() {
        if(anon.isAnonymous()){
            alert();
        }
        else {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), splash.class));
            finish();
        }
    }

    private void alert() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Really logout?")
                .setMessage("Logging out will delete all notes as an anonymous user!")
                .setPositiveButton("Create Account", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), register.class));
                        finish();
                    }
                }).setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        anon.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getApplicationContext(), splash.class));
                                finish();
                            }
                        });
                    }
                });
        warning.show();

    }

  //  @Override
  //  public boolean onCreateOptionsMenu(Menu menu) {
  //      MenuInflater inflater = getMenuInflater();
  //      inflater.inflate(R.menu.options, menu);
  //      return super.onCreateOptionsMenu(menu);
  //  }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.settings){
            Toast.makeText(this, "Settings Menu", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public class NoteView extends RecyclerView.ViewHolder {
        TextView nTitle,nContent;
        View view;
        CardView cardView;
        public NoteView(@NonNull View itemView) {
            super(itemView);
            nTitle = itemView.findViewById(R.id.title);
            nContent = itemView.findViewById(R.id.content);
            cardView = itemView.findViewById(R.id.note);
            view = itemView;
        }
    }
    private int getRandomColor() {

        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.blue);
        colorCode.add(R.color.skyblue);
        colorCode.add(R.color.yellow);
        colorCode.add(R.color.gray);
        colorCode.add(R.color.lightGreen);
        colorCode.add(R.color.greenlight);
        colorCode.add(R.color.pink);
        colorCode.add(R.color.pink2);
        colorCode.add(R.color.red);
        colorCode.add(R.color.lightPurple);

        Random rColor = new Random();
        int num = rColor.nextInt(colorCode.size());
        return colorCode.get(num);

    }



    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        noteAdapter.stopListening();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
