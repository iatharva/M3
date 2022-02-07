package com.example.m3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class IntroScreen1 extends AppCompatActivity {

    public FloatingActionButton nextBtn1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_screen1);
        Objects.requireNonNull(getSupportActionBar()).hide();
        nextBtn1 = findViewById(R.id.nextBtn1);

        nextBtn1.setOnClickListener(view -> {
            Intent i = new Intent(IntroScreen1.this,IntroScreen2.class);
            startActivity(i);
        });
    }
}