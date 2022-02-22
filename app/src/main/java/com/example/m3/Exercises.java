package com.example.m3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class Exercises extends AppCompatActivity {

    public FloatingActionButton toDoneScreenBtn;
    public ListView exerciseList,pranayamList;
    private FirebaseAuth fAuth;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public String UID;
    public Vibrator vibe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        toDoneScreenBtn = findViewById(R.id.toDoneScreenBtn);
        exerciseList = findViewById(R.id.ExerciseList);
        pranayamList = findViewById(R.id.PranayamList);
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

    private void showCustomDialog()
    {
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

                //Make a check list of the both the array and show on UI using exerciseList
                exerciseList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, exerciseArrayMaster));
                //On click of any item in the list, show the dialog which will show the details of the exercise and at the end of it, will show a button to mark exercise as completed
                exerciseList.setOnItemClickListener((adapterView, view, i, l) -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Exercises.this);
                    final View customLayout = getLayoutInflater().inflate(R.layout.dialog_exercises, null);
                    TextView et = customLayout.findViewById(R.id.ExerciseTitle);
                    TextView es = customLayout.findViewById(R.id.ExerciseSubtitle);
                    ImageView eim = customLayout.findViewById(R.id.ExerciseImage);
                    TextView ed = customLayout.findViewById(R.id.ExerciseDescription);
                    TextView ei = customLayout.findViewById(R.id.ExerciseInstructions);
                    CheckBox mc = customLayout.findViewById(R.id.MarkComplete);
                    et.setText(exerciseArrayMaster[i]);
                    DocumentReference specexerciseref = db.collection("ExerciseSettings").document((exerciseArrayMaster[i]).toString());
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
                    builder.setCancelable(true);
                    builder.setView(customLayout);
                    builder.show();
                });

                pranayamList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pranayamaArrayMaster));
                //On click of any item in the list, show the dialog which will show the details of the exercise and at the end of it, will show a button to mark exercise as completed
                pranayamList.setOnItemClickListener((adapterView, view, i, l) -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Exercises.this);
                    final View customLayout = getLayoutInflater().inflate(R.layout.dialog_exercises, null);
                    TextView et = customLayout.findViewById(R.id.ExerciseTitle);
                    TextView es = customLayout.findViewById(R.id.ExerciseSubtitle);
                    ImageView eim = customLayout.findViewById(R.id.ExerciseImage);
                    TextView ed = customLayout.findViewById(R.id.ExerciseDescription);
                    TextView ei = customLayout.findViewById(R.id.ExerciseInstructions);
                    CheckBox mc = customLayout.findViewById(R.id.MarkComplete);
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
                    builder.setCancelable(true);
                    builder.setView(customLayout);
                    builder.show();
                });
            }
        });
    }
}