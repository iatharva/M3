package com.example.m3;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class Silence extends AppCompatActivity {

    public LottieAnimationView animation_view;
    private MediaPlayer mediaPlayer;
    public TextView Timer,MName;
    public ImageButton StartBtn;
    public boolean IsAnimation=false;
    public String UID;
    public FirebaseAuth fAuth;
    public int length=0;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CountDownTimer countDownTimer;
    private long TimeLeftinMillis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_silence);
        animation_view = findViewById(R.id.animation_view);
        StartBtn = findViewById(R.id.StartBtn);
        Timer = findViewById(R.id.Timer);
        MName = findViewById(R.id.MName);

        //Starts the animation timer
        StartBtn.setOnClickListener(view -> {
            if(IsAnimation)
            {
                IsAnimation=false;
                animation_view.pauseAnimation();
                StartBtn.setImageResource(R.drawable.playbutton);
                stopAudio();
                startTimer(false);
            }
            else
            {
                IsAnimation=true;
                animation_view.playAnimation();
                StartBtn.setImageResource(R.drawable.pausebutton);
                playAudio(MName.getText().toString(),Timer.getText().toString());
                TimeLeftinMillis=getTimeInMilliSeconds(Timer.getText().toString());
                startTimer(true);
            }
        });
    }

    //Called on Activity launch
    @Override
    public void onResume()
    {
        super.onResume();
        fAuth = FirebaseAuth.getInstance();
        getUserSettings(0);
    }

    //Gets the data to show on screen
    public void getUserSettings(int StartTimer) {
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        DocumentReference musicref = db.collection("MusicSettings").document(UID);
        musicref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists())
            {
                String duration=documentSnapshot.getString("Duration");
                String musicName=documentSnapshot.getString("MusicName");

                String[] time = duration.split(" ");
                int minutes = Integer.parseInt(time[0]);
                int seconds = 00;
                String timeString = String.format("%02d:%02d", minutes, seconds);
                Timer.setText(timeString);
                MName.setText(musicName);
            }
        });
    }

    //Starts/pause the timer
    private void startTimer(boolean Start)
    {
        if(Start) {
            countDownTimer = new CountDownTimer(TimeLeftinMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    TimeLeftinMillis = millisUntilFinished;
                    updateTimer();
                }

                @Override
                public void onFinish() {
                    stopAudio();
                }
            }.start();
        }
        else
        {
            countDownTimer.cancel();
        }
    }

    private void updateTimer()
    {
        int minutes = (int) (TimeLeftinMillis/1000) / 60;
        int seconds = (int) (TimeLeftinMillis/1000) % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);
        Timer.setText(timeString);
    }

    /**
     * Starts playing Audio/music from the given link
     */
    private void playAudio(String musicName,String length)
    {
        DocumentReference musicref = db.collection("MusicSettings").document("MusicMaster");
        musicref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String audioUrl = documentSnapshot.getString(musicName);
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
        });
        /*
        else
        {
            mediaPlayer.seekTo(length);
            mediaPlayer.start();
        }
         */
    }

    /**
     * Method stops the audio/music if it is playing
     */
    private void stopAudio()
    {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            //length=mediaPlayer.getCurrentPosition();
            //return length;
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    //return time in milliseconds
    private int getTimeInMilliSeconds(String time)
    {
        String[] timeSplit = time.split(":");
        int minutes = Integer.parseInt(timeSplit[0]);
        int seconds = Integer.parseInt(timeSplit[1]);
        int timeInMilliSeconds = (minutes * 60 + seconds) * 1000;
        return timeInMilliSeconds;
    }
}