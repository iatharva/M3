package com.example.m3.Intros;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.m3.Account.LogIn;
import com.example.m3.Home;
import com.example.m3.InitialSettings.InitialAffirmationsSettings;
import com.example.m3.InitialSettings.InitialExercisesSettings;
import com.example.m3.InitialSettings.InitialSilenceSettings;
import com.example.m3.InitialSettings.InitialVisualizationSettings;
import com.example.m3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final int SPLASH_TIME_OUT=500;
    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public String UID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        fAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
        int SPLASH_TIME_OUT = 500;
        new Handler().postDelayed(() -> logincheck(), SPLASH_TIME_OUT);
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();
            if (mFirebaseUser != null) {
                new Handler().postDelayed(() -> {
                    //Success case (Be on Home screen)
                    CheckNewUserSettings();
                    finish();
                }, SPLASH_TIME_OUT);
            } else {
                //Failure case (Go to login screen)
                new Handler().postDelayed(() -> {
                    Intent i = new Intent(MainActivity.this, LogIn.class);
                    startActivity(i);
                    finish();
                }, SPLASH_TIME_OUT);
            }
        };
    }

    /**
     * Checks if user is logged in
     */
    public void logincheck(){
        fAuth.addAuthStateListener(mAuthStateListener);
    }

    public void CheckNewUserSettings()
    {
        fAuth = FirebaseAuth.getInstance();
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        //Check for musicsettings
        db.collection("MusicSettings").document(UID).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists())
            {
                //Check for affirmationsettings
                db.collection("AffirmationSettings").document(UID).get().addOnSuccessListener(documentSnapshot1 -> {
                    if (documentSnapshot1.exists())
                    {
                        //Check for VisualizationSettings
                        db.collection("VisualizationSettings").document(UID).get().addOnSuccessListener(documentSnapshot2 -> {
                            if (documentSnapshot2.exists())
                            {
                                //Check for ExerciseSettings
                                db.collection("ExerciseSettings").document(UID).get().addOnSuccessListener(documentSnapshot3 -> {
                                    if (documentSnapshot3.exists())
                                    {
                                        Toast.makeText(MainActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(MainActivity.this, Home.class);
                                        startActivity(i);

                                    }
                                    else
                                    {
                                        Toast.makeText(MainActivity.this, "Glad to meet you, Let's continue setup", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(MainActivity.this, InitialExercisesSettings.class);
                                        startActivity(i);
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this, "Glad to meet you, Let's continue setup", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(MainActivity.this, InitialVisualizationSettings.class);
                                startActivity(i);
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Glad to meet you, Let's continue setup", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(MainActivity.this, InitialAffirmationsSettings.class);
                        startActivity(i);
                    }
                });
            }
            else
            {
                Toast.makeText(MainActivity.this, "Glad to meet you, Welcome!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this, InitialSilenceSettings.class);
                startActivity(i);
            }
        });
    }
}