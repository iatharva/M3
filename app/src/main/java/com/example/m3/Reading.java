package com.example.m3;

import static android.content.ContentValues.TAG;

import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

public class Reading extends AppCompatActivity {

    private FloatingActionButton toDoneScreenBtn;
    private TextView booktype,articletype,T1Desc3,T1Desc4;
    private EditText count,bNameOrNotes;
    private FirebaseAuth fAuth;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public String UID;
    public boolean[] readingtype= new boolean[]{false,false};
    public Vibrator vibe;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
        fAuth = FirebaseAuth.getInstance();
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        toDoneScreenBtn = findViewById(R.id.toDoneScreenBtn);
        booktype = findViewById(R.id.booktype);
        articletype = findViewById(R.id.articletype);
        count = findViewById(R.id.count);
        T1Desc3 = findViewById(R.id.T1Desc3);
        T1Desc4 = findViewById(R.id.T1Desc4);
        bNameOrNotes = findViewById(R.id.bNameOrNotes);

        articletype.setBackground(ContextCompat.getDrawable(Reading.this, R.drawable.round_corner_hollow));
        booktype.setBackground(ContextCompat.getDrawable(Reading.this, R.drawable.round_corner_hollow));

        booktype.setOnClickListener(view -> {
            if(!readingtype[0])
            {
                T1Desc3.setText("How many pages of books have you read?");
                T1Desc4.setText("BookName (and takeaways/notes if any)");
                booktype.setTextColor(ContextCompat.getColor(Reading.this,R.color.white));
                articletype.setTextColor(ContextCompat.getColor(Reading.this,R.color.defaultTextColor));
                booktype.setBackground(ContextCompat.getDrawable(Reading.this, R.drawable.round_corner_filled));
                articletype.setBackground(ContextCompat.getDrawable(Reading.this, R.drawable.round_corner_hollow));
                readingtype[0] = true;
                readingtype[1] = false;
            }
        });
        articletype.setOnClickListener(view -> {
            if(!readingtype[1])
            {
                T1Desc3.setText("How many articles have you read?");
                T1Desc4.setText("Takeaways/notes from articles");
                booktype.setTextColor(ContextCompat.getColor(Reading.this,R.color.defaultTextColor));
                articletype.setTextColor(ContextCompat.getColor(Reading.this,R.color.white));
                articletype.setBackground(ContextCompat.getDrawable(Reading.this, R.drawable.round_corner_filled));
                booktype.setBackground(ContextCompat.getDrawable(Reading.this, R.drawable.round_corner_hollow));
                readingtype[1] = true;
                readingtype[0] = false;
            }
        });

        count.setOnClickListener(view -> {
            if(!readingtype[0] && !readingtype[1])
                Toast.makeText(Reading.this,"Please select reading type first to proceed", Toast.LENGTH_LONG).show();
        });

        toDoneScreenBtn.setOnClickListener(view -> {
            validationCheck();
        });

    }

    //Called on Activity launch
    @Override
    public void onResume()
    {
        super.onResume();
        Toast.makeText(Reading.this,"Please select reading type and then proceed", Toast.LENGTH_LONG).show();
    }

    //Go to home screen on back button press
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(Reading.this, Home.class));
        finish();
    }

    private void showCustomDialog()
    {
        getUserTimeLogs();
        vibe.vibrate(100);
        AlertDialog.Builder builder = new AlertDialog.Builder(Reading.this);
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_completedactivity, null);
        TextView message = customLayout.findViewById(R.id.message);
        message.setText("Reading completed !");
        builder.setPositiveButton("Go ahead", (dialogInterface, i) -> {
            Intent intent = new Intent(Reading.this, Journaling.class);
            startActivity(intent);
        });
        builder.setCancelable(false);
        builder.setView(customLayout);
        builder.show();
    }

    private void validationCheck()
    {
        if(!readingtype[0] && !readingtype[1])
            Toast.makeText(Reading.this,"Please select reading type first to proceed", Toast.LENGTH_LONG).show();
        else
        {
            if(count.getText().toString().isEmpty())
                Toast.makeText(Reading.this,"Please enter the count", Toast.LENGTH_LONG).show();
            else if(count.getText().toString().equals("0"))
                Toast.makeText(Reading.this,"If it's zero then please read and then submit :)", Toast.LENGTH_LONG).show();
            else
            {
                if(bNameOrNotes.getText().toString().isEmpty())
                {
                    if(readingtype[0])
                        Toast.makeText(Reading.this,"Please enter Book Name", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(Reading.this,"Please enter takeaways", Toast.LENGTH_LONG).show();
                }
                else
                    showCustomDialog();
                //Add code for firebase update
            }
        }
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
        timeLogsArray[4] = timestamp;
        DocumentReference usertimelogref = db.collection("UserLogs").document(UID);
        usertimelogref
                .update(today+"-TimeLog", Arrays.asList(timeLogsArray))
                .addOnSuccessListener(aVoid -> Toast.makeText(this,"Let's go",Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.w(TAG, "Time log not updated. Error :", e));

        //Add UserLog
        List<Boolean> ActivityLogString = new ArrayList<>();
        for(int i=0;i<6;i++)
        {
            if(i<=4)
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