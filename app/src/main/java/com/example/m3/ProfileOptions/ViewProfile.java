package com.example.m3.ProfileOptions;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class ViewProfile extends AppCompatActivity {

    EditText FNameField,LNameField,EmailField;
    TextView Age;
    Button UpdateBtn;
    public String UID,Email,FName,LName,Dob,EmailSecured;
    public String emailEntered,FNameEntered,LNameEntered;
    private FirebaseAuth fAuth;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        EmailField = findViewById(R.id.EmailField);
        FNameField = findViewById(R.id.FNameField);
        LNameField = findViewById(R.id.LNameField);
        UpdateBtn = findViewById(R.id.UpdateBtn);
        Age = findViewById(R.id.Age);
        fAuth = FirebaseAuth.getInstance();

        UpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateName();
            }
        });
    }

    private void UpdateName() {
        //Get all the data from UI
        emailEntered = EmailField.getText().toString().trim();
        FNameEntered = FNameField.getText().toString().trim();
        LNameEntered = LNameField.getText().toString().trim();
        String correctEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        //Validations
        if(TextUtils.isEmpty(FName)){
            Toast.makeText(getApplicationContext(),"Please enter your name",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(LName)){
            Toast.makeText(getApplicationContext(),"Please enter your last name",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(Email)){
            Toast.makeText(getApplicationContext(),"Please enter email",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!emailEntered.equals(correctEmail))
        {
            Toast.makeText(getApplicationContext(),"Please enter correct email to update",Toast.LENGTH_SHORT).show();
        }
        else
        {
            DocumentReference userref = db.collection("Users").document(UID);
            userref
                    .update("FName",FNameEntered)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this,"Name updated!",Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Log.w(TAG, "FName not updated. Error :", e));

            DocumentReference userref1 = db.collection("Users").document(UID);
            userref1
                    .update("LName",LNameEntered)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this,"Name updated!",Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Log.w(TAG, "LName not updated. Error :", e));
        }

    }

    @Override
    public void onResume()
    {
        super.onResume();
        getUserData();
    }

    public void getUserData()
    {
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        DocumentReference typeref = db.collection("Users").document(UID);
        typeref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Email=documentSnapshot.getString("Email");
                FName=documentSnapshot.getString("FName");
                LName =documentSnapshot.getString("LName");
                Dob=documentSnapshot.getString("Dob");

                //After 3 characters make all the string "*" in Email
                StringBuilder sb = new StringBuilder(Email);
                for (int i = 0; i < Email.length() - 3; i++) {
                    sb.setCharAt(i, '*');
                }
                EmailSecured = sb.toString();
                EmailField.setHint(EmailSecured);
                FNameField.setText(FName);
                LNameField.setText(LName);
                String[] dob = Dob.split("-");
                int age = getAge(Integer.parseInt(dob[2]),Integer.parseInt(dob[1]),Integer.parseInt(dob[0]));
                Age.setText("User Age : "+String.valueOf(age));
            }
        });
    }

    /**
     * Calculate the Age from Data of Birth of user
     */
    private int getAge(int year, int month, int day) {
        int age = 0;
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH)+1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        if (currentMonth > month) {
            age = currentYear - year;
        } else if (currentMonth == month) {
            if (currentDay >= day) {
                age = currentYear - year;
            } else {
                age = currentYear - year - 1;
            }
        } else {
            age = currentYear - year - 1;
        }
        return age;
    }
}