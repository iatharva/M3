package com.example.m3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.m3.InitialSettings.InitialSilenceSettings;
import com.example.m3.Savers.ActivityCompleted;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class InitialSettingsCompleted extends AppCompatActivity {

    public FloatingActionButton toNextScreenBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_settings_completed);
        toNextScreenBtn = findViewById(R.id.toNextScreenBtn);

        //Go to affirmations
        toNextScreenBtn.setOnClickListener(view -> {
            Intent i = new Intent(InitialSettingsCompleted.this, Home.class);
            startActivity(i);
            finish();
        });
    }
}