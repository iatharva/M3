package com.example.m3.InitialSettings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.dragankrstic.autotypetextview.AutoTypeTextView;
import com.example.m3.InitialSettingsCompleted;
import com.example.m3.Savers.ActivityCompleted;
import com.example.m3.Intros.IntroScreen1;
import com.example.m3.R;
import com.example.m3.Extras.LogoutDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InitialExercisesSettings extends AppCompatActivity {

    private String UID;
    private RelativeLayout relativeLayout;
    private FloatingActionButton toNextScreenBtn;
    private FirebaseAuth fAuth;
    private AutoTypeTextView subTitle1;
    private ListView exerciseList,pranayamList;
    List<String> selectedExercises = new ArrayList<>();
    List<String> selectedPranayamas= new ArrayList<>();
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_exercises_settings);
        relativeLayout = findViewById(R.id.relativeLayout);
        subTitle1 = findViewById(R.id.subTitle1);
        toNextScreenBtn = findViewById(R.id.toNextScreenBtn);
        exerciseList = findViewById(R.id.exerciseList);
        pranayamList = findViewById(R.id.pranayamList);
        subTitle1.setTextAutoTyping("Please select the exercises & pranayama you will be doing daily. Information about it will be provided by us");
        toNextScreenBtn.setVisibility(View.GONE);

        exerciseList.setOnItemClickListener((adapterView, view, i, l) -> {
            String exercise = exerciseList.getItemAtPosition(i).toString();
            //if exercise string already present in selectedExercises list then remove it
            if(selectedExercises.contains(exercise)){
                selectedExercises.remove(exercise);
                exerciseList.setItemChecked(i,false);
            }
            else
                selectedExercises.add(exercise);
            if(selectedPranayamas.size()>0 && selectedExercises.size()>0)
                toNextScreenBtn.setVisibility(View.VISIBLE);
            else
                toNextScreenBtn.setVisibility(View.GONE);
        });

        pranayamList.setOnItemClickListener((adapterView, view, i, l) -> {
            String pranayam = pranayamList.getItemAtPosition(i).toString();
            if(selectedPranayamas.contains(pranayam)){
                selectedPranayamas.remove(pranayam);
                pranayamList.setItemChecked(i,false);
            }
            else
                selectedPranayamas.add(pranayam);
            if(selectedPranayamas.size()>0 && selectedExercises.size()>0)
                toNextScreenBtn.setVisibility(View.VISIBLE);
            else
                toNextScreenBtn.setVisibility(View.GONE);
        });

        toNextScreenBtn.setOnClickListener(view -> {
            addExerciseSettings(selectedExercises,selectedPranayamas);
        });

    }

    //Gets the UID to use in this file anywhere.
    public void onResume() {
        super.onResume();
        fAuth = FirebaseAuth.getInstance();
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        DocumentReference typeref = db.collection("ExerciseSettings").document("ExerciseMaster");
        typeref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> pranayamaMaster = (List<String>) documentSnapshot.get("Pranayama");
                List<String> exerciseMaster = (List<String>) documentSnapshot.get("Exercise");
                String[] pranayamaArrayMaster = pranayamaMaster.toArray(new String[0]);
                String[] exerciseArrayMaster = exerciseMaster.toArray(new String[0]);

                ArrayAdapter<String> adapter= new ArrayAdapter<>(this,android.R.layout.simple_list_item_multiple_choice,exerciseArrayMaster);
                exerciseList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                exerciseList.setAdapter(adapter);

                ArrayAdapter<String> adapter1= new ArrayAdapter<>(this,android.R.layout.simple_list_item_multiple_choice,pranayamaArrayMaster);
                pranayamList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                pranayamList.setAdapter(adapter1);
            }
        });
    }

    public void addExerciseSettings(List<String> selectedExercises,List<String> selectedPranayamas)
    {
        Map<String, Object> map = new HashMap<>();
        map.put("Exercise",selectedExercises);
        map.put("Pranayama",selectedPranayamas);
        db.collection("ExerciseSettings").document(UID).set(map, SetOptions.merge()).addOnSuccessListener(aVoid -> {
            Log.d("TAG", "DocumentSnapshot successfully written!");
            Intent intent = new Intent(InitialExercisesSettings.this, InitialSettingsCompleted.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Create and show options in action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbarmenu, menu);
        return true;
    }

    /**
     * Operation to perform on item click in menu
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logoutButtonHeader) {
            openDialog();
            return true;
        }
        if (item.getItemId() == R.id.helpButtonHeader){
            //Add intro activity
            Intent i = new Intent(InitialExercisesSettings.this, IntroScreen1.class);
            //Intent i = new Intent(Home.this,InitialAffirmationSettings.class);
            startActivity(i);
        }
        if(item.getItemId() == R.id.shareButtonHeader){
            //Share the app link
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            String shareBody = "Hey, checkout my new favourite app, which helps you in your daily routine. \n To download the app click here \n https://github.com/iatharva/M3/releases";
            intent.setType("text/plain");
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
            intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(intent, getString(R.string.share_using)));
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Calls class for Log out
     */
    public void openDialog(){
        LogoutDialog logoutdialog=new LogoutDialog();
        logoutdialog.show(getSupportFragmentManager(),"Log out Dialog");
    }
}