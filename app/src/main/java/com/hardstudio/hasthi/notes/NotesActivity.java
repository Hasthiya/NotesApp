package com.hardstudio.hasthi.notes;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hardstudio.hasthi.notes.Adapters.NotesAdapter;
import com.hardstudio.hasthi.notes.Models.NoteDetails;
import com.hardstudio.hasthi.notes.Models.User;
import com.hardstudio.hasthi.notes.Util.Constants;

import java.util.ArrayList;

public class NotesActivity extends AppCompatActivity {

    Button button;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    FloatingActionButton newNoteButton;
    boolean doubleBackToExitPressedOnce = false;

    private ArrayList<NoteDetails> Notes;
    private DatabaseReference mDatabase;
    public User user;
    private NotesAdapter adapter;
    private RecyclerView notesRecyclerView;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finishAndRemoveTask();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        button = findViewById(R.id.signOutButton);
        newNoteButton = findViewById(R.id.newNoteButton);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            user = new User(mAuth.getCurrentUser().getUid());
        }

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    startActivity(new Intent(NotesActivity.this, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                }
            }
        };

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    mAuth.signOut();
            }
        });

        newNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NotesActivity.this, NoteDetailsActivity.class)
                        .putExtra(Constants.NOTE_PROCESS, Constants.NEW_NOTE_VALUE));
            }
        });

        getData(user.getId());





    }

    private void getData(String userId){
        Notes = new ArrayList<>();
        DatabaseReference notesDatabaseRef = mDatabase.child("users").child(userId).child("notes");
        notesDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Notes.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    NoteDetails noteDetails = postSnapshot.getValue(NoteDetails.class);
                    noteDetails.setNoteID(postSnapshot.getKey());
                    Notes.add(noteDetails);

                }
                notesRecyclerView = findViewById(R.id.notesRecyclerView);
                notesRecyclerView.setLayoutManager(new LinearLayoutManager(NotesActivity.this));
                adapter = new NotesAdapter(getApplicationContext(), Notes);
                notesRecyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        });
    }
}
