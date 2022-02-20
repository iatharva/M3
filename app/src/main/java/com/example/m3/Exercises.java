package com.example.m3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Exercises extends AppCompatActivity {

    public FloatingActionButton toDoneScreenBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);
        toDoneScreenBtn = findViewById(R.id.toDoneScreenBtn);

        toDoneScreenBtn.setOnClickListener(view -> {
            showCustomDialog();
        });
    }
    private void showCustomDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(Exercises.this);
        final View customLayout = getLayoutInflater().inflate(R.layout.completed_dialog, null);
        //message
        TextView message = customLayout.findViewById(R.id.message);
        message.setText("Exercises completed !");
        builder.setPositiveButton("Go ahead", (dialogInterface, i) -> {
            Intent intent = new Intent(Exercises.this, Reading.class);
            startActivity(intent);
        });
        builder.setCancelable(false);
        builder.setView(customLayout);
        builder.show();
    }
}