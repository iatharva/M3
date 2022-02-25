package com.example.m3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Journaling extends AppCompatActivity {

    FloatingActionButton toDoneScreenBtn;
    EditText myJournalEntry;
    private FirebaseAuth fAuth;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public String UID;
    public Vibrator vibe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journaling);
        fAuth = FirebaseAuth.getInstance();
        myJournalEntry = findViewById(R.id.myJournalEntry);
        toDoneScreenBtn = findViewById(R.id.toDoneScreenBtn);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        toDoneScreenBtn.setVisibility(View.GONE);

        //Add Text change listener to the EditText and if the text is not empty then show the FloatingActionButton
        myJournalEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() != 0)
                    toDoneScreenBtn.setVisibility(View.VISIBLE);
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        toDoneScreenBtn.setOnClickListener(view -> {

        });
    }

    private void showCustomDialog()
    {
        vibe.vibrate(100);
        AlertDialog.Builder builder = new AlertDialog.Builder(Journaling.this);
        final ImageView originalVisual = new ImageView(Journaling.this);

        builder.setTitle("Mood:");
        builder.setMessage("How was your mood throughout the day?");
        builder.setPositiveButton("Go ahead", (dialogInterface, i) -> {
            Intent intent = new Intent(Journaling.this, ActivityCompleted.class);
            startActivity(intent);
        });
        builder.setCancelable(false);
        builder.setView(originalVisual);
        builder.show();
    }
}