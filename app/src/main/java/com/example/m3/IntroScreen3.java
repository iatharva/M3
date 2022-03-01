package com.example.m3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class IntroScreen3 extends AppCompatActivity {

    private String UID;
    private FirebaseAuth fAuth;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public FloatingActionButton nextBtn3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_screen3);
        Objects.requireNonNull(getSupportActionBar()).hide();
        nextBtn3 = findViewById(R.id.nextBtn3);

        nextBtn3.setOnClickListener(view -> {
            if(CheckNewUser())
            {
                Toast.makeText(IntroScreen3.this, "Glad to meet you, Welcome!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(IntroScreen3.this,Home.class);
                startActivity(i);
            }
            else
            {
                //Create initial settings
                Intent i = new Intent(IntroScreen3.this,Home.class);
                startActivity(i);
            }
        });
    }

    //Gets the UID to use in this file anywhere.
    public void onResume() {
        super.onResume();
        fAuth = FirebaseAuth.getInstance();
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
    }

    public boolean CheckNewUser()
    {
        final boolean[] result = new boolean[1];
        db.collection("MusicSettings").document(UID).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists())
                result[0] = false;
            else
                result[0] = true;
        });
        return result[0];
    }
}