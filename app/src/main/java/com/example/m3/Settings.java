package com.example.m3;

import static com.example.m3.extras.App.CHANNEL_2_ID;

import android.app.Notification;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class Settings extends Fragment {
    public TextView tncBtn,alarmBtn,sysBtn,aboutBtn;
    public NotificationManagerCompat notificationManager;
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
        notificationManager = NotificationManagerCompat.from(requireActivity());
        alarmBtn.setOnClickListener(view -> {

        });

        sysBtn.setOnClickListener(view -> {

            Notification notification = new NotificationCompat.Builder(getActivity(), CHANNEL_2_ID)
                    .setSmallIcon(R.drawable.m3logoonly)
                    .setContentTitle("Sample notification")
                    .setContentText("Your notification will look like this")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build();
            notificationManager.notify(2, notification);
            Toast.makeText(getActivity(),"Notification should be visible",Toast.LENGTH_SHORT).show();

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