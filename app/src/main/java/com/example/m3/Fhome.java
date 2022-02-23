package com.example.m3;


import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.dragankrstic.autotypetextview.AutoTypeTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Fhome extends Fragment
{
    private MediaPlayer mediaPlayer;
    AutoTypeTextView AutoTypeLabel;
    public String UID,FName,Dob;
    public FirebaseAuth fAuth;
    public Button StartBtn;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        AutoTypeLabel = view.findViewById(R.id.AutoTypeLabel);
        StartBtn = view.findViewById(R.id.StartBtn);

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
                                //To be added
                                Intent intent = new Intent(getActivity(), Journaling.class);
                                startActivity(intent);
                                Toast.makeText(getActivity(),"I am journal demo", Toast.LENGTH_LONG).show();
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
                        StartBtn.setVisibility(View.GONE);
                    else
                    {
                        //if the first value is false then set Text to Start the routine
                        if(!ActivityLog.get(0))
                        {
                            StartBtn.setText("Start routine");
                        }
                        else
                        {
                            StartBtn.setText("Continue routine");
                        }
                    }
                }
                else
                {
                    StartBtn.setText("Start routine");
                }

            }
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
}

