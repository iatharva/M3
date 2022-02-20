package com.example.m3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.load.model.FileLoader;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ActivityCompleted extends AppCompatActivity {

    public FloatingActionButton toNextScreenBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed);
        toNextScreenBtn = findViewById(R.id.toNextScreenBtn);

        //Go to affirmations
        toNextScreenBtn.setOnClickListener(view -> {
            Intent i = new Intent(ActivityCompleted.this, Home.class);
            startActivity(i);
        });
    }
}