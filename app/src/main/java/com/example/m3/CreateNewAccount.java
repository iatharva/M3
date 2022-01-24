package com.example.m3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class CreateNewAccount extends AppCompatActivity {

    public EditText EmailField,PasswordField,FNameField,LNameField,DateField;
    public Button AddAccountBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);
        EmailField = findViewById(R.id.EmailField);
        PasswordField = findViewById(R.id.PasswordField);
        FNameField = findViewById(R.id.FNameField);
        LNameField = findViewById(R.id.LNameField);
        DateField = findViewById(R.id.DateField);

    }
}