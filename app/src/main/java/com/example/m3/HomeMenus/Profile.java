package com.example.m3.HomeMenus;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.m3.ProfileOptions.JournalLogs;
import com.example.m3.ProfileOptions.MoodAnalysis;
import com.example.m3.ProfileOptions.ViewProfile;
import com.example.m3.R;
import com.example.m3.ProfileOptions.SeeRanking;
import com.example.m3.Savers.Exercises;
import com.example.m3.Savers.Reading;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Profile extends Fragment {

    public TextView ProfileTitle,DateText,creationDate,T1,T1Desc,T2,T2Desc,T3,T3Desc,T4,T4Desc;
    public String UID,IsPaid;
    public Vibrator vibe;
    private FirebaseAuth fAuth;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
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
            checkPaidAndRedirect("JournalLogs");
        });
        T3Desc.setOnClickListener(view -> {
            checkPaidAndRedirect("JournalLogs");
        });

        //Mood analysis
        T4.setOnClickListener(view -> {
            checkPaidAndRedirect("MoodAnalysis");
        });
        T4Desc.setOnClickListener(view -> {
            checkPaidAndRedirect("MoodAnalysis");
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

    public void checkPaidAndRedirect(String screen)
    {
        DocumentReference typeref = db.collection("Users").document(UID);
        typeref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                IsPaid=documentSnapshot.getString("IsPaid");

                if(IsPaid.equals("0"))
                {
                    showPayDialog();
                }
                else
                {
                    Intent i;
                    if(screen.equals("MoodAnalysis"))
                        i = new Intent(getActivity(), MoodAnalysis.class);
                    else
                        i = new Intent(getActivity(), JournalLogs.class);
                    startActivity(i);
                }
            }
        });
    }

    private void showPayDialog() {
        vibe.vibrate(100);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Oops");
        builder.setMessage("Looks like you are trying to access paid features.\nLet's get the full version");
        builder.setPositiveButton("Get Paid Version", (dialogInterface, i) -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/918698961607?text=Hi%20I%20am%20user%20of%20m3%20app%20and%20I%20am%20interested%20in%20buying%20paid%20version"));
            startActivity(browserIntent);
        });
        builder.setCancelable(true);
        builder.show();

    }
}
