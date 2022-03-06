package com.example.m3.Account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.m3.Home;
import com.example.m3.InitialSettings.InitialAffirmationsSettings;
import com.example.m3.InitialSettings.InitialExercisesSettings;
import com.example.m3.InitialSettings.InitialSilenceSettings;
import com.example.m3.InitialSettings.InitialVisualizationSettings;
import com.example.m3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LogIn extends AppCompatActivity {

    public EditText EmailField,PasswordField;
    public Button ForgetPasswordBtn,CreateAccountBtn,LogInBtn;
    private FirebaseAuth fAuth;
    private String UID;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                    //Success case (go to Home Screen) after checking for new user
                    CheckNewUserSettings();
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

    //Gets the UID to use in this file anywhere.
    public void onResume() {
        super.onResume();

    }

    //Check if user settings exist for each record
    public void CheckNewUserSettings()
    {
        fAuth = FirebaseAuth.getInstance();
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        //Check for musicsettings
        db.collection("MusicSettings").document(UID).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists())
            {
                //Check for affirmationsettings
                db.collection("AffirmationSettings").document(UID).get().addOnSuccessListener(documentSnapshot1 -> {
                    if (documentSnapshot1.exists())
                    {
                        //Check for VisualizationSettings
                        db.collection("VisualizationSettings").document(UID).get().addOnSuccessListener(documentSnapshot2 -> {
                            if (documentSnapshot2.exists())
                            {
                                //Check for ExerciseSettings
                                db.collection("ExerciseSettings").document(UID).get().addOnSuccessListener(documentSnapshot3 -> {
                                    if (documentSnapshot3.exists())
                                    {
                                        Toast.makeText(LogIn.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(LogIn.this, Home.class);
                                        startActivity(i);

                                    }
                                    else
                                    {
                                        Toast.makeText(LogIn.this, "Glad to see you again, Let's continue setup", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(LogIn.this, InitialExercisesSettings.class);
                                        startActivity(i);
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(LogIn.this, "Glad to see you again, Let's continue setup", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(LogIn.this, InitialVisualizationSettings.class);
                                startActivity(i);
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(LogIn.this, "Glad to see you again, Let's continue setup", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(LogIn.this, InitialAffirmationsSettings.class);
                        startActivity(i);
                    }
                });
            }
            else
            {
                Toast.makeText(LogIn.this, "Glad to see you again, Welcome!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(LogIn.this, InitialSilenceSettings.class);
                startActivity(i);
            }
        });
    }
}