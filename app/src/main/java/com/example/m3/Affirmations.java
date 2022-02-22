package com.example.m3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
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
}