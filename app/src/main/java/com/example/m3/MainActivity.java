package com.example.m3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final int SPLASH_TIME_OUT=1500;
    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        //Check if user is logged in or not
        fAuth = FirebaseAuth.getInstance();
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();
            if (mFirebaseUser != null) {
                new Handler().postDelayed(() -> {
                    //Success case (Be on Home screen)
                    Intent i = new Intent(MainActivity.this, Home.class);
                    startActivity(i);
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

    @Override
    public void onStart() {
        super.onStart();
        int SPLASH_TIME_OUT = 1700;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                logincheck();
            }
        }, SPLASH_TIME_OUT);
    }

    /**
     * Checks if user is logged in
     */
    public void logincheck(){
        fAuth.addAuthStateListener(mAuthStateListener);
    }
}