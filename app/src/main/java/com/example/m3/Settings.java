package com.example.m3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class Settings extends Fragment {
    public TextView tncBtn,alarmBtn,sysBtn,aboutBtn;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        alarmBtn = view.findViewById(R.id.alarmBtn);
        sysBtn = view.findViewById(R.id.sysBtn);
        tncBtn = view.findViewById(R.id.tncBtn);
        aboutBtn = view.findViewById(R.id.aboutBtn);

        alarmBtn.setOnClickListener(view -> {

        });
        sysBtn.setOnClickListener(view -> {

        });

        tncBtn.setOnClickListener(view -> {
            //Open Terms and Conditions in browser
            String url = "https://www.app-privacy-policy.com/live.php?token=WiEp6mEK8zAcqW7bxc9834c0MQCV8pqE";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
        aboutBtn.setOnClickListener(view -> {

        });
        return view;
    }
}