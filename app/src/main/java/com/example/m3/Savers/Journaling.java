package com.example.m3.Savers;

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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m3.Home;
import com.example.m3.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Journaling extends AppCompatActivity {

    private FloatingActionButton toDoneScreenBtn;
    private EditText myJournalEntry;
    private FirebaseAuth fAuth;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public String UID;
    public Vibrator vibe;
    private Spinner moodSpinner;
    private SeekBar moodScale;
    private int showMessageOnce=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journaling);
        fAuth = FirebaseAuth.getInstance();
        myJournalEntry = findViewById(R.id.myJournalEntry);
        toDoneScreenBtn = findViewById(R.id.toDoneScreenBtn);
        moodSpinner = findViewById(R.id.moodSpinner);
        moodScale = findViewById(R.id.moodScale);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        toDoneScreenBtn.setVisibility(View.GONE);

        //Add Text change listener to the EditText and if the text is not empty then show the FloatingActionButton
        myJournalEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() != 0 && showMessageOnce==0) {
                    toDoneScreenBtn.setVisibility(View.VISIBLE);
                    Toast.makeText(Journaling.this,"Please also tell me how you feeling before saving journal",Toast.LENGTH_SHORT).show();
                    showMessageOnce=1;
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        toDoneScreenBtn.setOnClickListener(view -> {
            showCustomDialog();
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        fAuth = FirebaseAuth.getInstance();
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        Toast.makeText(this,"You can do today's journaling at the end of the day too",Toast.LENGTH_SHORT).show();

        DocumentReference typeref = db.collection("JournalingLogs").document("JournalingMaster");
        typeref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> moodMaster=(List<String>)documentSnapshot.get("MoodMaster");
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Journaling.this,
                        android.R.layout.simple_spinner_item, moodMaster);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                moodSpinner.setSelection(0);
                moodSpinner.setAdapter(dataAdapter);
            }
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
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_completedactivity, null);
        //message
        TextView message = customLayout.findViewById(R.id.message);
        message.setText("Journaling completed !");
        builder.setPositiveButton("Go ahead", (dialogInterface, i) -> {
            Intent intent = new Intent(Journaling.this, ActivityCompleted.class);
            startActivity(intent);
            finish();
        });
        builder.setCancelable(false);
        builder.setView(customLayout);
        builder.show();
    }

    //Get the specific date TimeLogs
    private void getUserTimeLogs()
    {
        String today = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        DocumentReference affref = db.collection("UserTimeLogs").document(UID);
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
        //Add timelogs
        String timestamp = new SimpleDateFormat("dd-MMM HH:mm a", Locale.getDefault()).format(new Date());
        String today = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        timeLogsArray[5] = timestamp;
        DocumentReference usertimelogref = db.collection("UserTimeLogs").document(UID);
        usertimelogref
                .update(today+"-TimeLog", Arrays.asList(timeLogsArray))
                .addOnSuccessListener(aVoid -> Toast.makeText(this,"Let's go",Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.w(TAG, "Time log not updated. Error :", e));

        //Add UserLog
        List<Boolean> ActivityLogString = new ArrayList<>();
        for(int i=0;i<6;i++)
        {
            if(i<=6)
                ActivityLogString.add(true);
            else
                ActivityLogString.add(false);
        }
        DocumentReference userlogref = db.collection("UserLogs").document(UID);
        userlogref
                .update(today, ActivityLogString)
                .addOnSuccessListener(aVoid -> Toast.makeText(this,"Let's go",Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.w(TAG, "Routine log not updated. Error :", e));

        //Add reading record
        List<String> JournalingRecord = new ArrayList<>();
        JournalingRecord.add(myJournalEntry.getText().toString());
        JournalingRecord.add(moodSpinner.getSelectedItem().toString());
        JournalingRecord.add(Integer.toString(moodScale.getProgress()));
        Map<String, Object> map2 = new HashMap<>();
        map2.put(today,JournalingRecord);
        db.collection("JournalingLogs").document(UID).set(map2, SetOptions.merge()).addOnSuccessListener(aVoid1 -> {
            Log.d("TAG", "DocumentSnapshot successfully written!");
        });

    }

}