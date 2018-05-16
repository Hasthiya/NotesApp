package com.hardstudio.hasthi.notes;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hardstudio.hasthi.notes.Models.NoteDetails;
import com.hardstudio.hasthi.notes.Models.User;
import com.hardstudio.hasthi.notes.Util.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NoteDetailsActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    public User user;
    private EditText noteTitle;
    private EditText noteBody;
    private FloatingActionButton camButton;
    public NoteDetails noteDetails;
    int processID;
    boolean doubleBackToExitPressedOnce = false;
    boolean isNoteEmpty = true;
    ValueEventListener listener;
    DatabaseReference notesDatabaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        noteTitle = findViewById(R.id.noteTitle);
        noteBody = findViewById(R.id.noteBody);

        final Intent intent = getIntent();
        processID = intent.getIntExtra(Constants.NOTE_PROCESS, 0);

        camButton = findViewById(R.id.camButton);
        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo camera activity
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            user = new User(firebaseAuth.getCurrentUser().getUid());
        }
        if(processID == 1000) {
            setData(user.getId(),intent.getStringExtra(Constants.NOTE_ID_KEY));
        }

    }

    @Override
    public void onBackPressed() {
        if(!noteTitle.getText().toString().equalsIgnoreCase("") && !noteBody.getText().toString().equalsIgnoreCase("") ){
            isNoteEmpty = false;
        } else {
            isNoteEmpty = true;
        }
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();

            final Intent intent = getIntent();
            processID = intent.getIntExtra(Constants.NOTE_PROCESS, 0);

            if(!noteTitle.getText().toString().equalsIgnoreCase("") && !noteBody.getText().toString().equalsIgnoreCase("") ){
                if(processID == 2000) {
                    addNewNote(user.getId(), noteTitle.getText().toString(), noteBody.getText().toString(), Calendar.getInstance().getTime());
                } else if(processID == 1000) {
                    editNote(user.getId(), intent.getStringExtra(Constants.NOTE_ID_KEY), noteTitle.getText().toString(), noteBody.getText().toString(), Calendar.getInstance().getTime());
                }
            }
            if (notesDatabaseRef != null && listener != null) {
                notesDatabaseRef.removeEventListener(listener);
            }

            return;
        }

        this.doubleBackToExitPressedOnce = true;

        if(isNoteEmpty){
            Snackbar.make(findViewById(R.id.noteDetailsMainLayout), "Note is INCOMPLETE and will NOT SAVE", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(findViewById(R.id.noteDetailsMainLayout), "Please click BACK again to SAVE and exit", Snackbar.LENGTH_SHORT).show();
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


    private void addNewNote(String userId, String title, String body, Date date) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
        notesDatabaseRef = mDatabase.child("users").child(userId).child("notes").push();
        NoteDetails noteDetails = new NoteDetails(title, body, formatter.format(date));
        notesDatabaseRef.setValue(noteDetails);
    }

    private void editNote(String userId,String noteID, String title, String body, Date date){
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
        notesDatabaseRef = mDatabase.child("users").child(userId).child("notes").child(noteID);
        NoteDetails noteDetails = new NoteDetails(title, body, formatter.format(date));
        notesDatabaseRef.setValue(noteDetails);
    }

    private void setData(String userId, String noteID){
        notesDatabaseRef = mDatabase.child("users").child(userId).child("notes").child(noteID);
        listener = notesDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null) {
                    noteDetails = dataSnapshot.getValue(NoteDetails.class);
                    noteDetails.setNoteID(dataSnapshot.getKey());
                    noteTitle.setText(noteDetails.getTitle());
                    noteBody.setText(noteDetails.getBody());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
            }

        });

    }
}
