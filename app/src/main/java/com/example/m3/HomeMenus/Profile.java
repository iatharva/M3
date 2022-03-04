package com.example.m3.HomeMenus;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.m3.ProfileOptions.JournalLogs;
import com.example.m3.ProfileOptions.MoodAnalysis;
import com.example.m3.ProfileOptions.ViewProfile;
import com.example.m3.R;
import com.example.m3.ProfileOptions.SeeRanking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Profile extends Fragment {

    public TextView ProfileTitle,DateText,creationDate,T1,T1Desc,T2,T2Desc,T3,T3Desc,T4,T4Desc;
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
        creationDate = view.findViewById(R.id.creationDate);
        T1 = view.findViewById(R.id.T1);
        T2 = view.findViewById(R.id.T2);
        T3 = view.findViewById(R.id.T3);
        T4 = view.findViewById(R.id.T4);
        T1Desc = view.findViewById(R.id.T1Desc);
        T2Desc = view.findViewById(R.id.T2Desc);
        T3Desc = view.findViewById(R.id.T3Desc);
        T4Desc = view.findViewById(R.id.T4Desc);

        //View Profile to update the info
        T1.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), ViewProfile.class);
            startActivity(i);
        });
        T1Desc.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), ViewProfile.class);
            startActivity(i);
        });

        //See your ranking
        T2.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), SeeRanking.class);
            startActivity(i);
        });
        T2Desc.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), SeeRanking.class);
            startActivity(i);
        });

        //Journal logs
        T3.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), JournalLogs.class);
            startActivity(i);
        });
        T3Desc.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), JournalLogs.class);
            startActivity(i);
        });

        //Mood analysis
        T4.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), MoodAnalysis.class);
            startActivity(i);
        });
        T4Desc.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), MoodAnalysis.class);
            startActivity(i);
        });

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getUserData();
    }
    public void getUserData()
    {
        fAuth = FirebaseAuth.getInstance();
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        Date currDate = new Date(Objects.requireNonNull(fAuth.getCurrentUser().getMetadata().getCreationTimestamp()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = dateFormat.format(currDate);
        creationDate.setText("created on "+date);
    }


}
