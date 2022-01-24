package com.example.m3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static int SPLASH_TIME_OUT=1500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        //Check if user is logged in or not
        fAuth = FirebaseAuth.getInstance();
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();
            if (mFirebaseUser != null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(MainActivity.this,Home.class);
                        startActivity(i);
                        finish();
                    }
                },SPLASH_TIME_OUT);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(MainActivity.this,LogIn.class);
                        startActivity(i);
                        finish();
                    }
                },SPLASH_TIME_OUT);
            }
        };
    }
}