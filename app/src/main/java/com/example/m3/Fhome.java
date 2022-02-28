package com.example.m3;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dragankrstic.autotypetextview.AutoTypeTextView;
import com.example.m3.extras.AlarmReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.white.progressview.CircleProgressView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Fhome extends Fragment
{
    private ImageView saver_image_0,saver_image_1,saver_image_2,saver_image_3,saver_image_4,saver_image_5;
    private TextView saver_description_0,saver_description_1,saver_description_2,saver_description_3,saver_description_4,saver_description_5;
    private View join_0_1,join_1_2,join_2_3,join_3_4,join_4_5;
    AutoTypeTextView AutoTypeLabel;
    private String UID,FName,Dob;
    private FirebaseAuth fAuth;
    private Button StartBtn;
    private Spinner searchDates;
    private CircleProgressView circle_progress;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private long hourInMilliSecond = 3600000;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        searchDates = view.findViewById(R.id.searchDates);
        circle_progress = view.findViewById(R.id.circle_progress);
        AutoTypeLabel = view.findViewById(R.id.AutoTypeLabel);
        saver_image_0 = view.findViewById(R.id.saver_image_0);
        saver_image_1 = view.findViewById(R.id.saver_image_1);
        saver_image_2 = view.findViewById(R.id.saver_image_2);
        saver_image_3 = view.findViewById(R.id.saver_image_3);
        saver_image_4 = view.findViewById(R.id.saver_image_4);
        saver_image_5 = view.findViewById(R.id.saver_image_5);
        saver_description_0 = view.findViewById(R.id.saver_description_0);
        saver_description_1 = view.findViewById(R.id.saver_description_1);
        saver_description_2 = view.findViewById(R.id.saver_description_2);
        saver_description_3 = view.findViewById(R.id.saver_description_3);
        saver_description_4 = view.findViewById(R.id.saver_description_4);
        saver_description_5 = view.findViewById(R.id.saver_description_5);
        join_0_1 = view.findViewById(R.id.join_0_1);
        join_1_2 = view.findViewById(R.id.join_1_2);
        join_2_3 = view.findViewById(R.id.join_2_3);
        join_3_4 = view.findViewById(R.id.join_3_4);
        join_4_5 = view.findViewById(R.id.join_4_5);
        StartBtn = view.findViewById(R.id.StartBtn);
        createNotificationChannel();
        StartBtn.setOnClickListener(view -> {

            DocumentReference userlogref = db.collection("UserLogs").document(UID);
            userlogref.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Date date = new Date();
                    String currentDate = dateFormat.format(date);
                    List<Boolean> ActivityLog=(List<Boolean>) documentSnapshot.get(currentDate);
                    if(ActivityLog!=null)
                    {
                        boolean[] arrayActivityLog = new boolean[ActivityLog.size()];
                        for(int i=0;i<ActivityLog.size();i++)
                            arrayActivityLog[i]=ActivityLog.get(i);
                        int currentActivityIndex = 0;
                        
                        //execute only if last element is false
                        if(!arrayActivityLog[arrayActivityLog.length-1])
                        {
                            if(arrayActivityLog[0]==false)
                            {
                                Intent intent = new Intent(getActivity(), Silence.class);
                                startActivity(intent);
                            }
                            else{

                                for(int i=0;i<arrayActivityLog.length;i++)
                                {
                                    if(arrayActivityLog[i]==true && arrayActivityLog[i+1]==false)
                                    {
                                        currentActivityIndex = i;
                                        break;
                                    }
                                }

                                if(currentActivityIndex==0)
                                {
                                    Intent intent = new Intent(getActivity(), Affirmations.class);
                                    startActivity(intent);
                                }
                                else if(currentActivityIndex==1)
                                {
                                    Intent intent = new Intent(getActivity(), Visualization.class);
                                    startActivity(intent);
                                }
                                else if(currentActivityIndex==2)
                                {
                                    Intent intent = new Intent(getActivity(), Exercises.class);
                                    startActivity(intent);
                                }
                                else if(currentActivityIndex==3)
                                {
                                    Intent intent = new Intent(getActivity(), Reading.class);
                                    startActivity(intent);
                                }
                                else if(currentActivityIndex==4)
                                {
                                    Intent intent = new Intent(getActivity(), Journaling.class);
                                    startActivity(intent);
                                }
                            }
                        }
                    }
                    else
                    {
                        Intent i = new Intent(getActivity(), Silence.class);
                        startActivity(i);
                    }

                }
            });
        });
        return view;
    }

    /**
     * Calls the method whenever activity is called
     */
    @Override
    public void onResume()
    {
        super.onResume();
        fAuth = FirebaseAuth.getInstance();
        getUserData();
        //Logic for notification
        //Use for affirmation
        /*
        Intent i = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),0,i,0);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        long time = System.currentTimeMillis();
        long tenSecondsInMillis = 1000*10;
        alarmManager.set(AlarmManager.RTC_WAKEUP,
                time+tenSecondsInMillis,pendingIntent);*/
    }

    /**
     * Gets the user data and make call to typeWriterMessages()
     */
    public void getUserData()
    {
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        DocumentReference typeref = db.collection("Users").document(UID);
        typeref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                FName=documentSnapshot.getString("FName");
                Dob=documentSnapshot.getString("Dob");
                //Call message typing method by passing the parameters
                typeWriterMessages(FName,Dob);
            }
        });

        DocumentReference userlogref = db.collection("UserLogs").document(UID);
        userlogref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                //get current date and make it in DD-MM-YYYY format and then check if array exists
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date date = new Date();
                String currentDate = dateFormat.format(date);
                List<Boolean> ActivityLog=(List<Boolean>) documentSnapshot.get(currentDate);

                if(ActivityLog!=null)
                {
                    boolean[] arrayActivityLog = new boolean[ActivityLog.size()];
                    for(int i=0;i<ActivityLog.size();i++)
                        arrayActivityLog[i]=ActivityLog.get(i);

                    //if last element is true then StartBtn visibility is gone else below if
                    if(arrayActivityLog[arrayActivityLog.length-1]==true)
                    {
                        StartBtn.setVisibility(View.GONE);
                        circle_progress.setProgressInTime(100,2500);
                        //set all data
                    }
                    else
                    {
                        //if the first value is false then set Text to Start the routine
                        if(!ActivityLog.get(0))
                        {
                            StartBtn.setText("Start routine");
                            circle_progress.setProgressInTime(0,2500);
                        }
                        else
                        {
                            StartBtn.setText("Continue routine");
                            int currentActivityIndex=0;
                            for(int i=0;i<arrayActivityLog.length;i++)
                            {
                                if(arrayActivityLog[i]==true && arrayActivityLog[i+1]==false)
                                {
                                    currentActivityIndex = i;
                                    break;
                                }
                            }

                            if(currentActivityIndex==0)
                            {
                                circle_progress.setProgressInTime(0,16,2500);
                            }
                            else if(currentActivityIndex==1)
                            {
                                circle_progress.setProgressInTime(0,33,2500);
                            }
                            else if(currentActivityIndex==2)
                            {
                                circle_progress.setProgressInTime(0,50,2500);
                            }
                            else if(currentActivityIndex==3)
                            {
                                circle_progress.setProgressInTime(0,66,2500);
                            }
                            else if(currentActivityIndex==4)
                            {
                                circle_progress.setProgressInTime(0,83,2500);
                            }
                            setTimeline(currentActivityIndex,"Middle");
                        }
                    }

                }
                else
                {
                    createTodayUserLogs();
                    StartBtn.setText("Start routine");
                }

            }
        });

        DocumentReference codesRef = db.collection("UserLogs").document(UID);
        codesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> list = new ArrayList<>();
                Map<String, Object> map = task.getResult().getData();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    list.add(entry.getKey());
                }
                Collections.sort(list, Collections.reverseOrder());
                list.set(0, "Today");
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                searchDates.setSelection(0);
                searchDates.setAdapter(dataAdapter);

            }
        });
    }

    private void setTimeline(int index,String position)
    {
        String today = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        DocumentReference affref = db.collection("UserTimeLogs").document(UID);
        affref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists())
            {
                List<String> timeLogs=(List<String>)documentSnapshot.get(today+"-TimeLog");
                String[] timeLogsArray = timeLogs.toArray(new String[0]);

                if(position.equals("First"))
                {
                    //Do nothing as visual is already set
                }
                else if (position.equals("Middle"))
                {
                    if(index==0)
                    {
                        saver_image_0.setBackgroundResource(R.drawable.round_corner_filled_saver);
                        saver_image_0.setImageResource(R.drawable.s_filled);
                        saver_description_0.setText(timeLogsArray[0]);
                    }
                    else if (index==1)
                    {
                        saver_image_0.setBackgroundResource(R.drawable.round_corner_filled_saver);
                        saver_image_0.setImageResource(R.drawable.s_filled);
                        saver_description_0.setText("Completed on "+timeLogsArray[0]);

                        saver_image_1.setBackgroundResource(R.drawable.round_corner_filled_saver);
                        saver_image_1.setImageResource(R.drawable.a_filled);
                        saver_description_1.setText("Completed on "+timeLogsArray[1]);
                    }
                    else if(index==2)
                    {
                        saver_image_0.setBackgroundResource(R.drawable.round_corner_filled_saver);
                        saver_image_0.setImageResource(R.drawable.s_filled);
                        saver_description_0.setText("Completed on "+timeLogsArray[0]);

                        saver_image_1.setBackgroundResource(R.drawable.round_corner_filled_saver);
                        saver_image_1.setImageResource(R.drawable.a_filled);
                        saver_description_1.setText("Completed on "+timeLogsArray[1]);

                        saver_image_2.setBackgroundResource(R.drawable.round_corner_filled_saver);
                        saver_image_2.setImageResource(R.drawable.v_filled);
                        saver_description_2.setText("Completed on "+timeLogsArray[2]);
                    }
                    else if(index==3)
                    {
                        saver_image_0.setBackgroundResource(R.drawable.round_corner_filled_saver);
                        saver_image_0.setImageResource(R.drawable.s_filled);
                        saver_description_0.setText("Completed on "+timeLogsArray[0]);

                        saver_image_1.setBackgroundResource(R.drawable.round_corner_filled_saver);
                        saver_image_1.setImageResource(R.drawable.a_filled);
                        saver_description_1.setText("Completed on "+timeLogsArray[1]);

                        saver_image_2.setBackgroundResource(R.drawable.round_corner_filled_saver);
                        saver_image_2.setImageResource(R.drawable.v_filled);
                        saver_description_2.setText("Completed on "+timeLogsArray[2]);

                        saver_image_3.setBackgroundResource(R.drawable.round_corner_filled_saver);
                        saver_image_3.setImageResource(R.drawable.e_filled);
                        saver_description_3.setText("Completed on "+timeLogsArray[3]);
                    }
                    else if(index==4)
                    {
                        saver_image_0.setBackgroundResource(R.drawable.round_corner_filled_saver);
                        saver_image_0.setImageResource(R.drawable.s_filled);
                        saver_description_0.setText("Completed on "+timeLogsArray[0]);

                        saver_image_1.setBackgroundResource(R.drawable.round_corner_filled_saver);
                        saver_image_1.setImageResource(R.drawable.a_filled);
                        saver_description_1.setText("Completed on "+timeLogsArray[1]);

                        saver_image_3.setBackgroundResource(R.drawable.round_corner_filled_saver);
                        saver_image_3.setImageResource(R.drawable.e_filled);
                        saver_description_3.setText("Completed on "+timeLogsArray[3]);

                        saver_image_4.setBackgroundResource(R.drawable.round_corner_filled_saver);
                        saver_image_4.setImageResource(R.drawable.r_filled);
                        saver_description_4.setText("Completed on "+timeLogsArray[4]);
                    }
                }
                else if(position.equals("Last"))
                {
                    //Image
                    saver_image_0.setBackgroundResource(R.drawable.round_corner_filled_saver);
                    saver_image_1.setBackgroundResource(R.drawable.round_corner_filled_saver);
                    saver_image_2.setBackgroundResource(R.drawable.round_corner_filled_saver);
                    saver_image_3.setBackgroundResource(R.drawable.round_corner_filled_saver);
                    saver_image_4.setBackgroundResource(R.drawable.round_corner_filled_saver);
                    saver_image_5.setBackgroundResource(R.drawable.round_corner_filled_saver);
                    saver_image_0.setImageResource(R.drawable.s_filled);
                    saver_image_1.setImageResource(R.drawable.a_filled);
                    saver_image_2.setImageResource(R.drawable.v_filled);
                    saver_image_3.setImageResource(R.drawable.e_filled);
                    saver_image_4.setImageResource(R.drawable.r_filled);
                    saver_image_5.setImageResource(R.drawable.j_filled);

                    //TextView
                    saver_description_0.setText("Completed on "+timeLogsArray[0]);
                    saver_description_1.setText("Completed on "+timeLogsArray[1]);
                    saver_description_2.setText("Completed on "+timeLogsArray[2]);
                    saver_description_3.setText("Completed on "+timeLogsArray[3]);
                    saver_description_4.setText("Completed on "+timeLogsArray[4]);
                    saver_description_5.setText("Completed on "+timeLogsArray[5]);
                }
            }
        });
    }

    //Create Array for recording Time Logs of the user
    private void createTodayUserLogs() {
        //create boolean with 6 elements in list all set to false
        List<Boolean> ActivityLog = new ArrayList<>();
        for(int i=0;i<6;i++)
            ActivityLog.add(false);
        
        //get current date and make it in DD-MM-YYYY format
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        String currentDate = dateFormat.format(date);

        //create map with current date and arrayActivityLog
        Map<String, Object> map = new HashMap<>();
        map.put(currentDate,ActivityLog);

        //Add a new field in the existing document of UID
        db.collection("UserLogs").document(UID).set(map, SetOptions.merge()).addOnSuccessListener(aVoid -> {
            Log.d("TAG", "DocumentSnapshot successfully written!");
        });

        List<String> ActivityTimeLogString = new ArrayList<>();
        for(int i=0;i<6;i++)
            ActivityTimeLogString.add("");
        Map<String, Object> map2 = new HashMap<>();
        map2.put(currentDate+"-TimeLog",ActivityTimeLogString);
        db.collection("UserTimeLogs").document(UID).set(map2, SetOptions.merge()).addOnSuccessListener(aVoid1 -> {
            Log.d("TAG", "DocumentSnapshot successfully written!");
        });
    }

    /**
     * Shows the message respective to the time
     */
    public void typeWriterMessages(String FName,String Dob)
    {
        AutoTypeLabel = view.findViewById(R.id.AutoTypeLabel);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        //Below code is to get current date
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c);
        //Below code is to get current day
        DateFormat dayFormat = new SimpleDateFormat("EEEE");
        String day = dayFormat.format(c);

        //If formattedDate has same dd-MM as Dob then show message "Happy Birthday"
        if(formattedDate.substring(0,2).equals(Dob.substring(0,2)) && formattedDate.substring(3,5).equals(Dob.substring(3,5)))
        {
            AutoTypeLabel.setTextAutoTyping("Today is your birthday, Happy birthday, "+FName+"!"+"\n"+"Hope you make this day one of the best."+"\n"+"🥳 ");
        }
        else
        {
            //If time is before 12pm and after 6 am then show message "Good Morning, let's start the day"
            if (currentDateandTime.compareTo("06:00:00") > 0 && currentDateandTime.compareTo("12:00:00") < 0) {
                AutoTypeLabel.setTextAutoTyping("Good Morning " + FName + ","+"\n"+"Let's get started with our day" + "\n" + "🌞");
            }
            //If time is after 12pm and before 6 pm then show message "Hope you are having a great productive day"
            else if (currentDateandTime.compareTo("12:00:00") > 0 && currentDateandTime.compareTo("18:00:00") < 0) {
                AutoTypeLabel.setTextAutoTyping("Hope you are having a great "+"\n"+"productive day" + "\n" + "🌤 ");
            }
            //If time is after 6 pm and before 12 am then show message "Hope your day was as you planned :)"
            else if (currentDateandTime.compareTo("18:00:00") > 0 && currentDateandTime.compareTo("24:00:00") < 0) {
                AutoTypeLabel.setTextAutoTyping("Hope your day was as you "+"\n"+"planned 🌃 :)");
            }
            //If time is after 12am and before 6 am then show message "I know our app is great but you really need to get some sleep"
            else if (currentDateandTime.compareTo("00:00:00") > 0 && currentDateandTime.compareTo("06:00:00") < 0) {
                AutoTypeLabel.setTextAutoTyping("I know our app is great" + "\n" + "but you really need to get some sleep" + "\n" + "💤 ");
            }
        }
    }

    private void createNotificationChannel()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            CharSequence name = "M3Reminder";
            String description = "Channel for reminder";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("notifyUser",name,importance);
            channel.enableVibration(true);
            channel.setDescription(description);

            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

