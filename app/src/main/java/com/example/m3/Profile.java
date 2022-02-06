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

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Objects;

public class Profile extends Fragment {

    public EditText EmailField,FNameField,LNameField;
    public TextView ProfileTitle,DateText;
    public String UID,Email,FName,LName,Dob;
    private FirebaseAuth fAuth;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        ProfileTitle = view.findViewById(R.id.ProfileTitle);
        EmailField = view.findViewById(R.id.EmailField);
        FNameField = view.findViewById(R.id.FNameField);
        LNameField = view.findViewById(R.id.LNameField);
        DateText = view.findViewById(R.id.DateText);
        fAuth = FirebaseAuth.getInstance();

        ProfileTitle.setOnClickListener(view -> {
            getUserData();
        });

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getUserData();
    }

    /**
     * Calculate the Age from Data of Birth of user
     */
    private int getAge(int year, int month, int day) {
        int age = 0;
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH)+1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        if (currentMonth > month) {
            age = currentYear - year;
        } else if (currentMonth == month) {
            if (currentDay >= day) {
                age = currentYear - year;
            } else {
                age = currentYear - year - 1;
            }
        } else {
            age = currentYear - year - 1;
        }
        return age;
    }

    public void getUserData()
    {
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        DocumentReference typeref = db.collection("Users").document(UID);
        typeref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Email=documentSnapshot.getString("Email");
                FName=documentSnapshot.getString("FName");
                LName =documentSnapshot.getString("LName");
                Dob=documentSnapshot.getString("Dob");

                EmailField.setText(Email);
                FNameField.setText(FName);
                LNameField.setText(LName);
                String[] dob = Dob.split("-");
                int age = getAge(Integer.parseInt(dob[2]),Integer.parseInt(dob[1]),Integer.parseInt(dob[0]));
                DateText.setText("User Age : "+String.valueOf(age));
            }
        });
    }
}
