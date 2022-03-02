package com.example.m3.Intros;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.m3.Home;
import com.example.m3.InitialSettings.InitialAffirmationsSettings;
import com.example.m3.InitialSettings.InitialExercisesSettings;
import com.example.m3.InitialSettings.InitialSilenceSettings;
import com.example.m3.InitialSettings.InitialVisualizationSettings;
import com.example.m3.R;
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
            CheckNewUserSettings();
        });
    }

    //Gets the UID to use in this file anywhere.
    public void onResume() {
        super.onResume();
        fAuth = FirebaseAuth.getInstance();
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
    }

    public void CheckNewUserSettings()
    {
        //Check for musicsettings
        db.collection("MusicSettings").document(UID).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists())
            {
                //Check for affirmationsettings
                db.collection("MusicSettings").document(UID).get().addOnSuccessListener(documentSnapshot1 -> {
                    if (documentSnapshot1.exists())
                    {
                        //Check for VisualizationSettings
                        db.collection("MusicSettings").document(UID).get().addOnSuccessListener(documentSnapshot2 -> {
                            if (documentSnapshot2.exists())
                            {
                                //Check for ExerciseSettings
                                db.collection("MusicSettings").document(UID).get().addOnSuccessListener(documentSnapshot3 -> {
                                    if (documentSnapshot3.exists())
                                    {
                                        Toast.makeText(IntroScreen3.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(IntroScreen3.this, Home.class);
                                        startActivity(i);

                                    }
                                    else
                                    {
                                        Toast.makeText(IntroScreen3.this, "Glad to meet you, Let's continue setup", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(IntroScreen3.this, InitialExercisesSettings.class);
                                        startActivity(i);
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(IntroScreen3.this, "Glad to meet you, Let's continue setup", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(IntroScreen3.this, InitialVisualizationSettings.class);
                                startActivity(i);
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(IntroScreen3.this, "Glad to meet you, Let's continue setup", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(IntroScreen3.this, InitialAffirmationsSettings.class);
                        startActivity(i);
                    }
                });
            }
            else
            {
                Toast.makeText(IntroScreen3.this, "Glad to meet you, Welcome!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(IntroScreen3.this, InitialSilenceSettings.class);
                startActivity(i);
            }
        });
    }
}