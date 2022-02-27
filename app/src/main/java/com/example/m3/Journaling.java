package com.example.m3;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Journaling extends AppCompatActivity {

    FloatingActionButton toDoneScreenBtn;
    EditText myJournalEntry;
    private FirebaseAuth fAuth;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public String UID;
    public Vibrator vibe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journaling);
        fAuth = FirebaseAuth.getInstance();
        myJournalEntry = findViewById(R.id.myJournalEntry);
        toDoneScreenBtn = findViewById(R.id.toDoneScreenBtn);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        toDoneScreenBtn.setVisibility(View.GONE);

        //Add Text change listener to the EditText and if the text is not empty then show the FloatingActionButton
        myJournalEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() != 0)
                    toDoneScreenBtn.setVisibility(View.VISIBLE);
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        toDoneScreenBtn.setOnClickListener(view -> {
            showCustomDialog();
        });
    }

    //Go to home screen on back button press
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(Journaling.this, Home.class));
        finish();
    }

    private void showCustomDialog()
    {
        getUserTimeLogs();
        vibe.vibrate(100);
        AlertDialog.Builder builder = new AlertDialog.Builder(Journaling.this);
        final ImageView originalVisual = new ImageView(Journaling.this);

        builder.setTitle("Mood:");
        builder.setMessage("How was your mood throughout the day?");
        builder.setPositiveButton("Go ahead", (dialogInterface, i) -> {
            Intent intent = new Intent(Journaling.this, ActivityCompleted.class);
            startActivity(intent);
        });
        builder.setCancelable(false);
        builder.setView(originalVisual);
        builder.show();
    }

    //Get the specific date TimeLogs
    private void getUserTimeLogs()
    {
        String today = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        DocumentReference affref = db.collection("UserLogs").document(UID);
        affref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists())
            {
                List<String> timeLogs=(List<String>)documentSnapshot.get(today+"-TimeLog");
                String[] timeLogsArray = timeLogs.toArray(new String[0]);
                updateUserLogs(timeLogsArray);
            }
        });
    }

    //Updates the TimeLogs
    private void updateUserLogs(String[] timeLogsArray)
    {
        String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        String today = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        timeLogsArray[5] = timestamp;
        DocumentReference usertimelogref = db.collection("UserLogs").document(UID);
        usertimelogref
                .update(today+"-TimeLog", Arrays.asList(timeLogsArray))
                .addOnSuccessListener(aVoid -> Toast.makeText(this,"Let's go",Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.w(TAG, "Time log not updated. Error :", e));

        //Add UserLog
        List<Boolean> ActivityLogString = new ArrayList<>();
        for(int i=0;i<6;i++)
        {
            if(i<=6)
            {
                ActivityLogString.add(true);
            }
            else
                ActivityLogString.add(false);
        }
        DocumentReference userlogref = db.collection("UserLogs").document(UID);
        userlogref
                .update(today, ActivityLogString)
                .addOnSuccessListener(aVoid -> Toast.makeText(this,"Let's go",Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.w(TAG, "Routine log not updated. Error :", e));

    }

}