package com.example.m3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateNewAccount extends AppCompatActivity {

    public EditText EmailField,PasswordField,FNameField,LNameField,DateField;
    public String Email,Password,FName,LName,DateOfBirth,UID;
    public DatePickerDialog.OnDateSetListener mDateSetListener;
    public Button AddAccountBtn;
    private FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);
        EmailField = findViewById(R.id.EmailField);
        PasswordField = findViewById(R.id.PasswordField);
        FNameField = findViewById(R.id.FNameField);
        LNameField = findViewById(R.id.LNameField);
        DateField = findViewById(R.id.DateField);
        AddAccountBtn = findViewById(R.id.AddAccountBtn);

        //Get Date DialogBox
        DateField.setOnClickListener(v -> {
            Calendar cal= Calendar.getInstance();
            int year=cal.get(Calendar.YEAR);
            int month=cal.get(Calendar.MONTH);
            int day=cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog= new DatePickerDialog(
                    CreateNewAccount.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    mDateSetListener,
                    year,month,day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        //Set Date
        mDateSetListener= (view, year, month, dayOfMonth) -> {
            month=month+1;
            String date= dayOfMonth + "-" + month+"-"+ year;
            DateField.setText(date);
        };

        AddAccountBtn.setOnClickListener(view -> {

            Email = EmailField.getText().toString().trim();
            Password = PasswordField.getText().toString().trim();
            FName = FNameField.getText().toString().trim();
            LName = LNameField.getText().toString().trim();
            DateOfBirth = DateField.getText().toString().trim();

            if(TextUtils.isEmpty(Email)){
                Toast.makeText(getApplicationContext(),"Please enter email",Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(Password)){
                Toast.makeText(getApplicationContext(),"Please enter password",Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(FName)){
                Toast.makeText(getApplicationContext(),"Please enter your name",Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(LName)){
                Toast.makeText(getApplicationContext(),"Please enter your last name",Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(DateOfBirth)){
                Toast.makeText(getApplicationContext(),"Select your date of birth",Toast.LENGTH_SHORT).show();
                return;
            }

            fAuth = FirebaseAuth.getInstance();
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            fAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                    Map<String, Object> user = new HashMap<>();
                    user.put("Uid", UID);
                    user.put("Email", Email);
                    user.put("FName", FName);
                    user.put("LName", LName);
                    user.put("Dob", DateOfBirth);

                    //NEW
                    db.collection("Users").document(UID).set(user).addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()) {
                            AddAccountBtn.setText(R.string.create_account);
                            Toast.makeText(CreateNewAccount.this,"Welcome to M3", Toast.LENGTH_LONG).show();
                            FirebaseAuth.getInstance().signOut();
                            Intent i = new Intent(CreateNewAccount.this, Home.class);
                            startActivity(i);
                        } else{
                            AddAccountBtn.setText(R.string.create_account);
                            String errorMessage = Objects.requireNonNull(task1.getException()).getMessage();
                            Toast.makeText(CreateNewAccount.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    Toast.makeText(CreateNewAccount.this, "Error! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

    }
}