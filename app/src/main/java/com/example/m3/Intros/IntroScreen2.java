package com.example.m3.Intros;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.m3.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class IntroScreen2 extends AppCompatActivity {

    public FloatingActionButton nextBtn2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_screen2);
        Objects.requireNonNull(getSupportActionBar()).hide();
        nextBtn2 = findViewById(R.id.nextBtn2);

        nextBtn2.setOnClickListener(view -> {
            Intent i = new Intent(IntroScreen2.this,IntroScreen3.class);
            startActivity(i);
        });
    }
}