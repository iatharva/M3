package com.example.m3;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.airbnb.lottie.L;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Profile extends Fragment {

    public EditText EmailField;
    public TextView EmailText;
    public String UID,Email,FName,LName,Dob;
    private FirebaseAuth fAuth;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        EmailText = view.findViewById(R.id.EmailText);
        EmailField = view.findViewById(R.id.EmailField);
        fAuth = FirebaseAuth.getInstance();

        EmailText.setOnClickListener(view -> {
            UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
            DocumentReference typeref = db.collection("Users").document(UID);
            typeref.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Email=documentSnapshot.getString("Email");
                    FName=documentSnapshot.getString("FName");
                    LName =documentSnapshot.getString("LName");
                    Dob=documentSnapshot.getString("Dob");

                    EmailField.setText(Email);
                }
            });
        });

        return view;
    }
}
