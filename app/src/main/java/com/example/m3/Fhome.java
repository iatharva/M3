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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;

public class Fhome extends Fragment {

    //private FirebaseAuth fAuth;
    private MediaPlayer mediaPlayer;
    private Button demosound,demosound1;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        demosound = view.findViewById(R.id.demosound);
        demosound1 = view.findViewById(R.id.demosound1);

        demosound.setOnClickListener(view -> {
            playAudio();
            Toast.makeText(getActivity(), "Audio started playing..", Toast.LENGTH_SHORT).show();});

        demosound1.setOnClickListener(view -> {
            if (mediaPlayer.isPlaying()) {
                stopAudio();
                Toast.makeText(getActivity(), "Audio has been paused", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Audio has not played", Toast.LENGTH_SHORT).show();
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
    

}

