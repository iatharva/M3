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
import java.util.Objects;

public class Fhome extends Fragment
{
    private MediaPlayer mediaPlayer;
    AutoTypeTextView AutoTypeLabel;
    public String UID,FName,Dob;
    public FirebaseAuth fAuth;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        AutoTypeLabel = view.findViewById(R.id.AutoTypeLabel);

        Button demosound = view.findViewById(R.id.demosound);
        Button demosound1 = view.findViewById(R.id.demosound1);

        demosound.setOnClickListener(view -> {
            playAudio();
            AutoTypeLabel.setTextAutoTyping("Audio started playing..");
        });

        demosound1.setOnClickListener(view -> {
            if(mediaPlayer!=null) {
                if (mediaPlayer.isPlaying()) {
                    stopAudio();
                    AutoTypeLabel.setTextAutoTyping("Audio has been paused");
                } else {
                    AutoTypeLabel.setTextAutoTyping("Audio has not played");
                }
            }
        });
        return view;
    }

    /**
     * Starts playing Audio/music from the given link
     */
    private void playAudio()
    {
        String audioUrl = "https://github.com/iatharva/iatharva.github.io/raw/master/images/03.%20Morph.mp3";
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method stops the audio/music if it is playing
     */
    private void stopAudio()
    {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
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
                AutoTypeLabel.setTextAutoTyping("Good Morning " + FName + ","+"\n"+"Let's start the day" + "\n" + "🌞");
            }
            //If time is after 12pm and before 6 pm then show message "Hope you are having a great productive day"
            else if (currentDateandTime.compareTo("12:00:00") > 0 && currentDateandTime.compareTo("18:00:00") < 0) {
                AutoTypeLabel.setTextAutoTyping("Hope you are having a great "+"\n"+"productive day" + "\n" + "🌤 ");
            }
            //If time is after 6 pm and before 12 am then show message "Hope your day was as you planned :)"
            else if (currentDateandTime.compareTo("18:00:00") > 0 && currentDateandTime.compareTo("24:00:00") < 0) {
                AutoTypeLabel.setTextAutoTyping("Hope your day was as you "+"\n"+"planned 🌃 :)");
            }
        }
    }
}

