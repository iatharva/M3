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
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Fhome extends Fragment {

    //private FirebaseAuth fAuth;
    private MediaPlayer mediaPlayer;
    AutoTypeTextView AutoTypeLabel;
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
        typeWriterMessages();

        demosound.setOnClickListener(view -> {
            playAudio();
            AutoTypeLabel.setTextAutoTyping("Audio started playing..");
            Toast.makeText(getActivity(), "Audio started playing..", Toast.LENGTH_SHORT).show();});

        demosound1.setOnClickListener(view -> {
            if(mediaPlayer!=null) {
                if (mediaPlayer.isPlaying()) {
                    stopAudio();
                    AutoTypeLabel.setTextAutoTyping("Audio has been paused");
                    Toast.makeText(getActivity(), "Audio has been paused", Toast.LENGTH_SHORT).show();
                } else {
                    AutoTypeLabel.setTextAutoTyping("Audio has not played");
                    Toast.makeText(getActivity(), "Audio has not played", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    /**
     * Starts playing Audio/music from the given link
     */
    private void playAudio() {
        String audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3";
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
    private void stopAudio() {
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
        typeWriterMessages();
    }

    /**
     * Shows the message respective to the time
     */
    private void typeWriterMessages() {
        AutoTypeLabel = view.findViewById(R.id.AutoTypeLabel);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        //Below code is to get current date
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        //Below code is to get current day
        DateFormat dayFormat = new SimpleDateFormat("EEEE");
        String day = dayFormat.format(c);

        //If time is before 12pm and after 6 am then show message "Good Morning, let's start the day"
        if (currentDateandTime.compareTo("06:00:00") > 0 && currentDateandTime.compareTo("12:00:00") < 0) {
            AutoTypeLabel.setTextAutoTyping("Good Morning, let's start the day");
            Toast.makeText(getActivity(), "Good Morning, let's start the day", Toast.LENGTH_SHORT).show();
        }
        //If time is after 12pm and before 6 pm then show message "Hope you are having a great productive day"
        else if (currentDateandTime.compareTo("12:00:00") > 0 && currentDateandTime.compareTo("18:00:00") < 0) {
            AutoTypeLabel.setTextAutoTyping("Hope you are having a great productive day");
            Toast.makeText(getActivity(), "ope you are having a great productive day", Toast.LENGTH_SHORT).show();
        }
        //If time is after 6 pm and before 12 am then show message "Hope your day was as you planned :)"
        else if (currentDateandTime.compareTo("18:00:00") > 0 && currentDateandTime.compareTo("24:00:00") < 0) {
            AutoTypeLabel.setTextAutoTyping("Hope your day was as you planned :)");
            Toast.makeText(getActivity(), "Hope your day was as you planned :)", Toast.LENGTH_SHORT).show();
        }
    }
}

