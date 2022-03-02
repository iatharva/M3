package com.example.m3.Savers;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m3.Home;
import com.example.m3.R;
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
import java.util.Objects;

public class Affirmations extends AppCompatActivity {

    public ListView affirmationList;
    public FloatingActionButton toDoneScreenBtn;
    private FirebaseAuth fAuth;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public String UID;
    public Vibrator vibe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affirmations);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        affirmationList = findViewById(R.id.affirmationList);
        toDoneScreenBtn = findViewById(R.id.toDoneScreenBtn);
        fAuth = FirebaseAuth.getInstance();
        toDoneScreenBtn.setOnClickListener(view -> {
            showCustomDialog();
        });
    }

    //Called on Activity launch
    @Override
    public void onResume()
    {
        super.onResume();
        getUserSettings();
    }

    //Go to home screen on back button press
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(Affirmations.this, Home.class));
        finish();
    }

    //Gets the data to show on screen
    public void getUserSettings() {
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        DocumentReference affref = db.collection("AffirmationSettings").document(UID);
        affref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists())
            {
                List<String> affirmations=(List<String>)documentSnapshot.get("Sentences");
                getAffirmationSettings(affirmations);
            }
        });

    }
    //Shows the custom dialog on activity completion
    private void showCustomDialog()
    {
        getUserTimeLogs();
        vibe.vibrate(100);
        AlertDialog.Builder builder = new AlertDialog.Builder(Affirmations.this);
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_completedactivity, null);
        //message
        TextView message = customLayout.findViewById(R.id.message);
        message.setText("Affirmations completed !");
        builder.setPositiveButton("Go ahead", (dialogInterface, i) -> {
            Intent intent = new Intent(Affirmations.this, Visualization.class);
            startActivity(intent);
        });
        builder.setCancelable(false);
        builder.setView(customLayout);
        builder.show();
    }

    //Returns the list of affirmations
    private void getAffirmationSettings(List<String> affirmations) {
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,affirmations);
        ViewGroup.LayoutParams params = affirmationList.getLayoutParams();
        params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70*affirmations.size(), getResources().getDisplayMetrics());
        params.width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
        affirmationList.setLayoutParams(params);
        affirmationList.setAdapter(adapter);
        registerForContextMenu(affirmationList);
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
        String timestamp = new SimpleDateFormat("dd-MMM HH:mm a", Locale.getDefault()).format(new Date());
        String today = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        timeLogsArray[1] = timestamp;
        DocumentReference usertimelogref = db.collection("UserTimeLogs").document(UID);
        usertimelogref
                .update(today+"-TimeLog", Arrays.asList(timeLogsArray))
                .addOnSuccessListener(aVoid -> Toast.makeText(this,"Let's go",Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.w(TAG, "Time log not updated. Error :", e));

        //Add UserLog
        List<Boolean> ActivityLogString = new ArrayList<>();
        for(int i=0;i<6;i++)
        {
            if(i<=1)
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