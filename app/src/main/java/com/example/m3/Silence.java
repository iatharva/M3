package com.example.m3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Objects;

public class Silence extends AppCompatActivity {

    public LottieAnimationView animation_view;
    private MediaPlayer mediaPlayer;
    public FloatingActionButton toDoneScreenBtn;
    public TextView Timer,MName,TimerOriginal;
    public ImageButton StartBtn;
    public boolean IsAnimation=false;
    public String UID;
    public FirebaseAuth fAuth;
    public int length=0;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CountDownTimer countDownTimer;
    private long TimeLeftInMillis;
    public Vibrator vibe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_silence);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        animation_view = findViewById(R.id.animation_view);
        StartBtn = findViewById(R.id.StartBtn);
        Timer = findViewById(R.id.Timer);
        TimerOriginal = findViewById(R.id.TimerOriginal);
        MName = findViewById(R.id.MName);
        toDoneScreenBtn = findViewById(R.id.toDoneScreenBtn);

        //Takes to next screen
        toDoneScreenBtn.setOnClickListener(view -> {
            showCustomDialog();
        });

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
                playAudio(MName.getText().toString(),Timer.getText().toString(),TimerOriginal.getText().toString());
                TimeLeftInMillis=getTimeInMilliSeconds(Timer.getText().toString());
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
                TimerOriginal.setText(timeString);
                MName.setText(musicName);
            }
        });
    }

    //Starts/pause the timer
    private void startTimer(boolean Start)
    {
        if(Start) {
            countDownTimer = new CountDownTimer(TimeLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    TimeLeftInMillis = millisUntilFinished;
                    updateTimer();
                }

                @Override
                public void onFinish() {
                    stopAudio();
                    toDoneScreenBtn.setVisibility(View.VISIBLE);
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
        int minutes = (int) (TimeLeftInMillis/1000) / 60;
        int seconds = (int) (TimeLeftInMillis/1000) % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);
        Timer.setText(timeString);
    }

    /**
     * Starts playing Audio/music from the given link
     */
    private void playAudio(String musicName,String length,String originalLength)
    {
        int playFrom = getTimeInMilliSeconds(originalLength) - getTimeInMilliSeconds(length);
        DocumentReference musicref = db.collection("MusicSettings").document("MusicMaster");
        musicref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String audioUrl = documentSnapshot.getString(musicName);
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.setDataSource(audioUrl);
                    mediaPlayer.prepare();
                    mediaPlayer.seekTo(playFrom);
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

    private void showCustomDialog()
    {
        vibe.vibrate(100);
        AlertDialog.Builder builder = new AlertDialog.Builder(Silence.this);
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_completedactivity, null);
        builder.setPositiveButton("Go ahead", (dialogInterface, i) -> {
            Intent intent = new Intent(Silence.this, Affirmations.class);
            startActivity(intent);
        });
        builder.setCancelable(false);
        builder.setView(customLayout);
        builder.show();
    }
}