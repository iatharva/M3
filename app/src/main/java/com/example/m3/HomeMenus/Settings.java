package com.example.m3.HomeMenus;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.m3.Intros.About;
import com.example.m3.R;
import com.example.m3.Support;
import com.example.m3.SystemSettings;

import java.util.Calendar;

public class Settings extends Fragment {
    public TextView tncBtn,alarmBtn,sysBtn,aboutBtn,supportBtn;
    public NotificationManagerCompat notificationManager;
    private Calendar mCalendar;
    private int mYear, mMonth, mHour, mMinute, mDay;
    private long mRepeatTime;
    private String mRepeatNo;
    private static final long milDay = 86400000L;
    TimePickerDialog timepickerdialog1;
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
        supportBtn = view.findViewById(R.id.supportBtn);
        notificationManager = NotificationManagerCompat.from(requireActivity());
        //Buttons which redirects to required pages/activities/fragment

        //Button which leads to alarm settings
        alarmBtn.setOnClickListener(view -> {
            setUpAlarmNotification();

        });

        //Button which leads to system settings
        sysBtn.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), SystemSettings.class);
            startActivity(i);
        });

        //Button which leads to Terms and Conditions
        tncBtn.setOnClickListener(view -> {
            //Open Terms and Conditions in browser
            String url = "https://www.app-privacy-policy.com/live.php?token=WiEp6mEK8zAcqW7bxc9834c0MQCV8pqE";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        //Button which leads to FAQ
        supportBtn.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), Support.class);
            startActivity(i);
        });

        //Button which leads to about page
        aboutBtn.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), About.class);
            startActivity(i);
        });

        //View for fragment
        return view;
    }

    //Setup notification everyday at a given time
    private void setUpAlarmNotification() {
        //Show time picker in custom dialog
        Calendar now = Calendar.getInstance();
        final int[] hour = {0};
        final int[] minute = {0};
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getActivity(),AlertDialog.THEME_HOLO_LIGHT, (timePicker, selectedHour, selectedMinute) -> {
            hour[0] =selectedHour; minute[0] =selectedMinute;
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
        mTimePicker.setTitle("Wake up Time");
        mTimePicker.show();

        //set the notification for everyday at the hour[0]:minute[0]
        /*
        mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.HOUR_OF_DAY, hour[0]);
        mCalendar.set(Calendar.MINUTE, minute[0]);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);
        mRepeatTime = mCalendar.getTimeInMillis();
        mRepeatNo = "0";

        //Set up notification
        AlarmReceiver alarm = new AlarmReceiver();
        alarm.SetAlarm(getActivity());*/
    }
}