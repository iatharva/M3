package com.example.m3;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class SystemSettings extends AppCompatActivity {

    public TextView musicPreferred;
    public Spinner durationPreferred;
    public MediaPlayer mediaPlayer;
    public String oldDuration,newDuration,UID,oldMusic;
    public String [] durations = {"5 minutes","10 minutes","15 minutes","20 minutes" };
    public ArrayAdapter<String> adapter;
    private FirebaseAuth fAuth;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_settings);
        musicPreferred = findViewById(R.id.musicPreferred);
        durationPreferred = findViewById(R.id.durationPreferred);
        fAuth = FirebaseAuth.getInstance();
        //Opens dialog to choose music
        musicPreferred.setOnClickListener(view -> getMusicNamesMaster());
        //For dialog to choose duration & update
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, durations);
        durationPreferred.setAdapter(adapter);
        durationPreferred.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                newDuration=durationPreferred.getSelectedItem().toString();
                getDurationSettings(newDuration);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //Returns the whole list of Music Names to display
    private void getMusicNamesMaster() {
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        DocumentReference typeref = db.collection("MusicSettings").document("MusicMaster");
        typeref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> musicNamesMaster=(List<String>)documentSnapshot.get("MusicNamesMaster");
                getMusicSettings(musicNamesMaster);
            }
        });
    }

    //Shows the Music Dialog box on UI to update
    private void showMusicDialog(String [] musicNames,int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SystemSettings.this);
        builder.setTitle("Update music choice");
        Toast.makeText(this,"Select another music to change and listen to it",Toast.LENGTH_SHORT).show();
        builder.setSingleChoiceItems(musicNames, index, (dialogInterface, i) -> {
            stopAudio();
            GetMusicLink(musicNames[i]);
            updateMusicSettings(musicNames[i],musicNames[index]);
        })
        .setOnDismissListener(dialogInterface -> {
            stopAudio();
            getUserSettings();
        })
        .setOnCancelListener(dialogInterface -> {
            stopAudio();
            getUserSettings();})
        .setNegativeButton("Cancel", (dialogInterface, i) -> {
            stopAudio();
            dialogInterface.dismiss();
            getUserSettings();
        });
        builder.show();
    }

    private void updateMusicSettings(String newMusic,String oldMusic) {
        if(!newMusic.equals(oldMusic))
        {
            DocumentReference musicref = db.collection("MusicSettings").document(UID);
            musicref
                    .update("MusicName",newMusic)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this,"Changed preference to " + newMusic,Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Log.w(TAG, "Music not updated. Error :", e));
        }
        else{
            Toast.makeText(this,"You have selected current music " + newMusic,Toast.LENGTH_SHORT).show();
        }
    }

    //Called on Activity launch
    @Override
    public void onResume()
    {
        super.onResume();
        getUserSettings();
    }

    //Gets the data to show on screen
    public void getUserSettings() {
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        DocumentReference typeref = db.collection("MusicSettings").document(UID);
        typeref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists())
            {
                oldDuration=documentSnapshot.getString("Duration");
                oldMusic=documentSnapshot.getString("MusicName");
                musicPreferred.setText(oldMusic);
                durationPreferred.setSelection(getArrayIndex(durations,oldDuration));
            }
        });
    }

    //Plays the preferred music by fetching the link
    public void GetMusicLink(String musicName)
    {
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        DocumentReference typeref = db.collection("MusicSettings").document("MusicMaster");
        typeref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists())
            {
                String musicLink=documentSnapshot.getString(musicName);
                Toast.makeText(this,"Playing " + musicName,Toast.LENGTH_SHORT).show();
                stopAudio();
                playAudio(musicLink);
            }
        });
    }

    //Gets the current duration
    public void getDurationSettings(String newDuration)
    {
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        DocumentReference typeref = db.collection("MusicSettings").document(UID);
        typeref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists())
            {
                oldDuration=documentSnapshot.getString("Duration");
                updateDurationSettings(newDuration, oldDuration);
            }
        });
    }

    //Gets the current duration
    public void getMusicSettings(List<String> musicNamesMaster)
    {
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        DocumentReference typeref = db.collection("MusicSettings").document(UID);
        typeref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists())
            {
                oldMusic= documentSnapshot.getString("MusicName");
                String[] musicNames = musicNamesMaster.toArray(new String[0]);
                showMusicDialog(musicNames,getArrayIndex(musicNames,oldMusic));
            }
        });
    }

    //Updates the duration
    public void updateDurationSettings(String newDuration,String oldDuration)
    {
        if(!newDuration.equals(oldDuration))
        {
            DocumentReference musicref = db.collection("MusicSettings").document(UID);
            musicref
                .update("Duration",newDuration)
                .addOnSuccessListener(aVoid -> Toast.makeText(this,"Your music will be played for " + newDuration,Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.w(TAG, "Duration not updated. Error :", e));
        }
        else
        {
            Toast.makeText(this,"You have selected current duration" + newDuration,Toast.LENGTH_SHORT).show();
        }
    }

    //Returns the array index of string passed if found in array
    //Else returns 0
    public int getArrayIndex(String [] array,String value)
    {
        for(int i = 0; i < array.length; i++)
        {
            if(array[i].equals(value))
            {
                return i;
            }
        }
        return 0;
    }

    //Starts playing Audio/music from the given link
    private void playAudio(String audioUrl)
    {
        mediaPlayer =new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try
        {
            stopAudio();
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //Method stops the audio/music if it is playing
    private void stopAudio()
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer=null;
            mediaPlayer=new MediaPlayer();
        }
    }
}