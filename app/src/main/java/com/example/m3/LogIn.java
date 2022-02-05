package com.example.m3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LogIn extends AppCompatActivity {

    public EditText EmailField,PasswordField;
    public Button ForgetPasswordBtn,CreateAccountBtn,LogInBtn;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        fAuth = FirebaseAuth.getInstance();
        ForgetPasswordBtn = findViewById(R.id.ForgetPasswordBtn);
        CreateAccountBtn = findViewById(R.id.CreateAccountBtn);
        LogInBtn = findViewById(R.id.LogInBtn);
        EmailField = findViewById(R.id.EmailField);
        PasswordField = findViewById(R.id.PasswordField);

        //To open Forget Password Activity
        ForgetPasswordBtn.setOnClickListener(view -> {
            Intent i = new Intent(LogIn.this,ForgetPassword.class);
            startActivity(i);
        });

        //To open Create Account Activity
        CreateAccountBtn.setOnClickListener(view -> {
            Intent i = new Intent(LogIn.this,CreateNewAccount.class);
            startActivity(i);
        });

        //To log in
        LogInBtn.setOnClickListener(view -> {
            //Getting data from UI
            String email = EmailField.getText().toString().trim();
            String password = PasswordField.getText().toString().trim();

            //Validation
            if (TextUtils.isEmpty(email)) {
                EmailField.setError("Email ID is Required.");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                PasswordField.setError("Password is Required.");
                return;
            }
            if (password.length() < 6) {
                PasswordField.setError("Password Must be more than 6 Characters");
                return;
            }

            // Authenticating the user
            LogInBtn.setText(R.string.signingin);
            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful())
                {
                    //Success case (go to Home Screen)
                    Intent intent = new Intent(LogIn.this, Home.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    //Failure case (Show error)
                    String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                    Toast.makeText(LogIn.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                }
                LogInBtn.setText(R.string.log_in);
            });
        });
    }
}