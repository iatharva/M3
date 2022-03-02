package com.example.m3.Savers;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m3.Home;
import com.example.m3.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Exercises extends AppCompatActivity {

    public FloatingActionButton toDoneScreenBtn;
    public ListView exerciseList,pranayamList;
    public TextView trackExercise;
    private FirebaseAuth fAuth;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public String UID;
    public Vibrator vibe;
    public int exerciseCount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        toDoneScreenBtn = findViewById(R.id.toDoneScreenBtn);
        exerciseList = findViewById(R.id.ExerciseList);
        pranayamList = findViewById(R.id.PranayamList);
        trackExercise = findViewById(R.id.TrackExercise);
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
        startActivity(new Intent(Exercises.this, Home.class));
        finish();
    }

    private void showCustomDialog()
    {
        getUserTimeLogs();
        vibe.vibrate(100);
        AlertDialog.Builder builder = new AlertDialog.Builder(Exercises.this);
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_completedactivity, null);
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

    public void getUserSettings()
    {
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        DocumentReference typeref = db.collection("ExerciseSettings").document(UID);
        typeref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists())
            {
                List<String> pranayamaMaster = (List<String>) documentSnapshot.get("Pranayama");
                List<String> exerciseMaster = (List<String>) documentSnapshot.get("Exercise");
                String[] pranayamaArrayMaster = pranayamaMaster.toArray(new String[0]);
                String[] exerciseArrayMaster = exerciseMaster.toArray(new String[0]);
                boolean[] exerciseCompleted = new boolean[(pranayamaArrayMaster.length+exerciseArrayMaster.length)];
                trackExercise.setText("Exercises Completed : "+exerciseCount+"/"+(pranayamaArrayMaster.length+exerciseArrayMaster.length));
                setUserSettings(pranayamaArrayMaster,exerciseArrayMaster,exerciseCompleted);
            }
        });
    }

    public void setUserSettings(String[] pranayamaArrayMaster, String[] exerciseArrayMaster,boolean[] exerciseCompleted)
    {
        exerciseList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, exerciseArrayMaster));
        exerciseList.setOnItemClickListener((adapterView, view, i, l) -> {
            AlertDialog builder = new AlertDialog.Builder(Exercises.this).create();
            final View customLayout = getLayoutInflater().inflate(R.layout.dialog_exercises, null);
            TextView et = customLayout.findViewById(R.id.ExerciseTitle);
            TextView es = customLayout.findViewById(R.id.ExerciseSubtitle);
            ImageView eim = customLayout.findViewById(R.id.ExerciseImage);
            TextView ed = customLayout.findViewById(R.id.ExerciseDescription);
            TextView ei = customLayout.findViewById(R.id.ExerciseInstructions);
            CheckBox mc = customLayout.findViewById(R.id.MarkComplete);

            if(exerciseCompleted[i])
                mc.setChecked(true);
            else
                mc.setChecked(false);

            et.setText(exerciseArrayMaster[i]);
            DocumentReference specexerciseref = db.collection("ExerciseSettings").document(exerciseArrayMaster[i]);
            specexerciseref.get().addOnSuccessListener(documentSnapshot1 -> {
                if (documentSnapshot1.exists()) {
                    String exerciseSubtitle=documentSnapshot1.getString("ExerciseSubtitle");
                    String exerciseImage=documentSnapshot1.getString("ExerciseImage");
                    String exerciseDescription=documentSnapshot1.getString("ExerciseDescription");
                    String exerciseInstructions=documentSnapshot1.getString("ExerciseInstructions");
                    es.setText(exerciseSubtitle);
                    ed.setText(exerciseDescription);
                    ei.setText(exerciseInstructions);
                    Picasso
                            .get()
                            .load(exerciseImage)
                            .placeholder( R.drawable.loadinganimation)
                            .into(eim);
                }
            });
            mc.setOnClickListener(view1 -> {
                if(mc.isChecked())
                {
                    if(exerciseCount<(pranayamaArrayMaster.length+exerciseArrayMaster.length))
                    {
                        exerciseCount++;
                    }
                    exerciseCompleted[i]=true;
                    checkCompletion(exerciseCompleted);
                    trackExercise.setText("Exercises Completed : "+exerciseCount+"/"+(pranayamaArrayMaster.length+exerciseArrayMaster.length));
                    builder.dismiss();
                }
            });

            builder.setCancelable(true);
            builder.setView(customLayout);
            builder.show();
        });

        pranayamList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pranayamaArrayMaster));
        pranayamList.setOnItemClickListener((adapterView, view, i, l) -> {
            AlertDialog builder = new AlertDialog.Builder(Exercises.this).create();
            final View customLayout = getLayoutInflater().inflate(R.layout.dialog_exercises, null);
            TextView et = customLayout.findViewById(R.id.ExerciseTitle);
            TextView es = customLayout.findViewById(R.id.ExerciseSubtitle);
            ImageView eim = customLayout.findViewById(R.id.ExerciseImage);
            TextView ed = customLayout.findViewById(R.id.ExerciseDescription);
            TextView ei = customLayout.findViewById(R.id.ExerciseInstructions);
            CheckBox mc = customLayout.findViewById(R.id.MarkComplete);

            if(exerciseCompleted[exerciseArrayMaster.length+i])
                mc.setChecked(true);
            else
                mc.setChecked(false);

            et.setText(pranayamaArrayMaster[i]);
            DocumentReference specexerciseref = db.collection("ExerciseSettings").document(pranayamaArrayMaster[i]);
            specexerciseref.get().addOnSuccessListener(documentSnapshot2 -> {
                if (documentSnapshot2.exists()) {
                    String exerciseSubtitle=documentSnapshot2.getString("ExerciseSubtitle");
                    String exerciseImage=documentSnapshot2.getString("ExerciseImage");
                    String exerciseDescription=documentSnapshot2.getString("ExerciseDescription");
                    String exerciseInstructions=documentSnapshot2.getString("ExerciseInstructions");
                    es.setText(exerciseSubtitle);
                    ed.setText(exerciseDescription);
                    ei.setText(exerciseInstructions);
                    Picasso
                            .get()
                            .load(exerciseImage)
                            .placeholder( R.drawable.loadinganimation)
                            .into(eim);
                }
            });
            mc.setOnClickListener(view1 -> {
                if(mc.isChecked())
                {
                    if(exerciseCount<(pranayamaArrayMaster.length+exerciseArrayMaster.length))
                    {
                        exerciseCount++;
                    }
                    exerciseCompleted[exerciseArrayMaster.length+i]=true;
                    checkCompletion(exerciseCompleted);
                    trackExercise.setText("Exercises Completed : "+exerciseCount+"/"+(pranayamaArrayMaster.length+exerciseArrayMaster.length));
                    builder.dismiss();
                }
            });
            builder.setCancelable(true);
            builder.setView(customLayout);
            builder.show();
        });
    }

    private void checkCompletion(boolean[] array)
    {
        int count=0;
        for (boolean b : array)
            if (!b)
                count++;

        if(count==0)
        {
            toDoneScreenBtn.setVisibility(View.VISIBLE);
        }
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
        timeLogsArray[3] = timestamp;
        DocumentReference usertimelogref = db.collection("UserTimeLogs").document(UID);
        usertimelogref
                .update(today+"-TimeLog", Arrays.asList(timeLogsArray))
                .addOnSuccessListener(aVoid -> Toast.makeText(this,"Let's go",Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.w(TAG, "Time log not updated. Error :", e));

        //Add UserLog
        List<Boolean> ActivityLogString = new ArrayList<>();
        for(int i=0;i<6;i++)
        {
            if(i<=3)
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