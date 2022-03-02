package com.example.m3;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.dragankrstic.autotypetextview.AutoTypeTextView;
import com.example.m3.extras.LogoutDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InitialAffirmationsSettings extends AppCompatActivity {

    private ListView affirmationList;
    private FirebaseAuth fAuth;
    private AutoTypeTextView subTitle1;
    private ImageView addAffBtn;
    private FloatingActionButton toNextScreenBtn;
    private String UID;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_affirmations_settings);
        subTitle1 = findViewById(R.id.subTitle1);
        addAffBtn = findViewById(R.id.addAffBtn);
        affirmationList = findViewById(R.id.affirmationList);
        toNextScreenBtn = findViewById(R.id.toNextScreenBtn);
        subTitle1.setTextAutoTyping("Click on plus add some affirmations");
        addAffBtn.setOnClickListener(view -> AddAffirmationSettings());
        toNextScreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InitialAffirmationsSettings.this, InitialExercisesSettings.class);
                startActivity(intent);
            }
        });
    }

    //Gets the UID to use in this file anywhere.
    public void onResume() {
        super.onResume();
        fAuth = FirebaseAuth.getInstance();
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        getUserSettings();
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
                toNextScreenBtn.setVisibility(View.VISIBLE);
            }
            else
            {
                toNextScreenBtn.setVisibility(View.GONE);
                Toast.makeText(this,"Add some affirmation",Toast.LENGTH_SHORT).show();
            }
        });
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

    private void AddAffirmationSettings()
    {
        String[] affirmationsArrayblank = new String[]{""};
        DocumentReference affref = db.collection("AffirmationSettings").document(UID);
        affref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists())
            {
                List<String> affirmationsList=(List<String>)documentSnapshot.get("Sentences");
                String[] affirmationsArray = affirmationsList.toArray(new String[0]);
                //Last index + 1
                showAffirmationDialog(affirmationsList.size() + 1,affirmationsArray,true);
            }
            else
            {
                showAffirmationDialog(0,affirmationsArrayblank,true);
            }
        });
    }

    //Shows the Input Dialog box on UI to update affirmations
    private void showAffirmationDialog(int position,String [] affirmations,Boolean NewAff) {
        AlertDialog.Builder builder = new AlertDialog.Builder(InitialAffirmationsSettings.this);
        final EditText updatedAffirmation = new EditText(InitialAffirmationsSettings.this);
        updatedAffirmation.setInputType(InputType.TYPE_CLASS_TEXT);
        if(NewAff)
        {
            builder.setTitle("Add affirmation");
            builder.setView(updatedAffirmation);
            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                dialogInterface.dismiss();
                getUserSettings();
            });
            builder.setPositiveButton("Add", (dialogInterface, i) -> {
                if(position==0)
                {
                    addAffirmationSettings(updatedAffirmation.getText().toString());
                }
                else
                    updateAffirmationSettings(updatedAffirmation.getText().toString(),position,affirmations,true);
                dialogInterface.dismiss();
                getUserSettings();
            });
        }
        else
        {
            builder.setTitle("Edit affirmation");
            updatedAffirmation.setText(affirmations[position]);
            builder.setView(updatedAffirmation);
            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                dialogInterface.dismiss();
                getUserSettings();
            });
            builder.setPositiveButton("Update", (dialogInterface, i) -> {
                updateAffirmationSettings(updatedAffirmation.getText().toString(),position,affirmations,false);
                dialogInterface.dismiss();
                getUserSettings();
            });
        }
        builder.show();
    }

    //Updates the affirmation
    public void updateAffirmationSettings(String updatedAffirmation,int position, String [] affirmationsList, Boolean IsNew)
    {
        if(!IsNew)
        {
            if(!updatedAffirmation.equals(affirmationsList[position]))
            {
                affirmationsList[position] = updatedAffirmation;
                DocumentReference musicref = db.collection("AffirmationSettings").document(UID);
                musicref
                        .update("Sentences", Arrays.asList(affirmationsList))
                        .addOnSuccessListener(aVoid -> Toast.makeText(this,"Affirmation Updated",Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Log.w(TAG, "Affirmation not updated. Error :", e));
            }
            else
            {
                Toast.makeText(this,"Please enter something in textbox",Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            if(updatedAffirmation!=null)
            {
                //add a new element to to the array
                affirmationsList = Arrays.copyOf(affirmationsList, affirmationsList.length + 1);
                affirmationsList[affirmationsList.length - 1] = updatedAffirmation;
                DocumentReference musicref = db.collection("AffirmationSettings").document(UID);
                musicref
                        .update("Sentences", Arrays.asList(affirmationsList))
                        .addOnSuccessListener(aVoid -> Toast.makeText(this,"Affirmation Added",Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Log.w(TAG, "Affirmation not added. Error :", e));
            }
            else
            {
                Toast.makeText(this,"Please enter something in textbox",Toast.LENGTH_SHORT).show();
            }
        }

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
            Intent i = new Intent(InitialAffirmationsSettings.this,IntroScreen1.class);
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

    private void addAffirmationSettings(String firstAffirmation) {
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        List<String> affirmations = new ArrayList<>();
        affirmations.add(firstAffirmation);
        Map<String, Object> map = new HashMap<>();
        map.put("Sentences",affirmations);

        db.collection("AffirmationSettings").document(UID).set(map, SetOptions.merge()).addOnSuccessListener(aVoid -> {
            Log.d("TAG", "DocumentSnapshot successfully written!");
        });
        getUserSettings();
    }
}