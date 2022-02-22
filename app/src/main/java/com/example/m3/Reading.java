package com.example.m3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Reading extends AppCompatActivity {

    public FloatingActionButton toDoneScreenBtn;
    public Vibrator vibe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        toDoneScreenBtn = findViewById(R.id.toDoneScreenBtn);

        toDoneScreenBtn.setOnClickListener(view -> {
            showCustomDialog();
        });
    }
    private void showCustomDialog()
    {
        vibe.vibrate(100);
        AlertDialog.Builder builder = new AlertDialog.Builder(Reading.this);
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_completedactivity, null);
        TextView message = customLayout.findViewById(R.id.message);
        message.setText("Reading completed !");
        builder.setPositiveButton("Go ahead", (dialogInterface, i) -> {
            Intent intent = new Intent(Reading.this, ActivityCompleted.class);
            startActivity(intent);
        });
        builder.setCancelable(false);
        builder.setView(customLayout);
        builder.show();
    }
}