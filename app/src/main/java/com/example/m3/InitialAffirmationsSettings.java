package com.example.m3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.dragankrstic.autotypetextview.AutoTypeTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class InitialAffirmationsSettings extends AppCompatActivity {

    private ListView affirmationList;
    private FirebaseAuth fAuth;
    private AutoTypeTextView subTitle1;
    private String UID;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_affirmations_settings);
        subTitle1 = findViewById(R.id.subTitle1);
        affirmationList = findViewById(R.id.affirmationList);
    }

    //Gets the UID to use in this file anywhere.
    public void onResume() {
        super.onResume();
        fAuth = FirebaseAuth.getInstance();
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
    }
}