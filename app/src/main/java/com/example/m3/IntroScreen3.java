package com.example.m3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class IntroScreen3 extends AppCompatActivity {

    public FloatingActionButton nextBtn3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_screen3);
        Objects.requireNonNull(getSupportActionBar()).hide();
        nextBtn3 = findViewById(R.id.nextBtn3);

        nextBtn3.setOnClickListener(view -> {
            Intent i = new Intent(IntroScreen3.this,Home.class);
            startActivity(i);
        });
    }
}