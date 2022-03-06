package com.example.m3.InitialSettings;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dragankrstic.autotypetextview.AutoTypeTextView;
import com.example.m3.Intros.IntroScreen1;
import com.example.m3.R;
import com.example.m3.Extras.LogoutDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InitialSilenceSettings extends AppCompatActivity {

    private String UID;
    private RadioGroup musicGroup,durationGroup;
    private MediaPlayer mediaPlayer;
    private FloatingActionButton toNextScreenBtn;
    private RadioButton musicPrefer0,musicPrefer1,musicPrefer2,musicPrefer3,musicPrefer4,musicPrefer5,musicPrefer6,musicPrefer7,musicPrefer8,musicPrefer9,duration0,duration1,duration2,duration3;
    private FirebaseAuth fAuth;
    private AutoTypeTextView subTitle1;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public String [] durations = {"5 minutes","10 minutes","15 minutes","20 minutes" };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_silence_settings);
        subTitle1 = findViewById(R.id.subTitle1);
        musicGroup = findViewById(R.id.musicGroup);
        musicPrefer0 = findViewById(R.id.musicPrefer0);
        musicPrefer1 = findViewById(R.id.musicPrefer1);
        musicPrefer2 = findViewById(R.id.musicPrefer2);
        musicPrefer3 = findViewById(R.id.musicPrefer3);
        musicPrefer4 = findViewById(R.id.musicPrefer4);
        musicPrefer5 = findViewById(R.id.musicPrefer5);
        musicPrefer6 = findViewById(R.id.musicPrefer6);
        musicPrefer7 = findViewById(R.id.musicPrefer7);
        musicPrefer8 = findViewById(R.id.musicPrefer8);
        musicPrefer9 = findViewById(R.id.musicPrefer9);
        durationGroup = findViewById(R.id.durationGroup);
        duration0 = findViewById(R.id.duration0);
        duration1 = findViewById(R.id.duration1);
        duration2 = findViewById(R.id.duration2);
        duration3 = findViewById(R.id.duration3);
        duration0.setText(durations[0]);
        duration1.setText(durations[1]);
        duration2.setText(durations[2]);
        duration3.setText(durations[3]);
        toNextScreenBtn = findViewById(R.id.toNextScreenBtn);
        subTitle1.setTextAutoTyping("Please select one from each for music settings");
        toNextScreenBtn.setVisibility(View.GONE);

        musicGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if(musicGroup.getCheckedRadioButtonId()!=-1)
            {
                int selectedId = musicGroup.getCheckedRadioButtonId();
                toNextScreenBtn.setVisibility(View.VISIBLE);
                RadioButton selectedRadioButton = findViewById(selectedId);
                stopAudio();
                getMusicLink(selectedRadioButton.getText().toString());
            }
        });

        durationGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if(durationGroup.getCheckedRadioButtonId()!=-1)
            {
                toNextScreenBtn.setVisibility(View.VISIBLE);
            }
        });

        toNextScreenBtn.setOnClickListener(view -> {
            stopAudio();
            saveMusicSettings();
        });
    }

    private void saveMusicSettings() {
        if(durationGroup.getCheckedRadioButtonId()==-1)
            Toast.makeText(this,"Please select duration",Toast.LENGTH_SHORT).show();

        if(musicGroup.getCheckedRadioButtonId()==-1)
            Toast.makeText(this,"Please select music",Toast.LENGTH_SHORT).show();

        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        Map<String, Object> map = new HashMap<>();
        RadioButton selectduration = findViewById(durationGroup.getCheckedRadioButtonId());
        RadioButton selectmusic = findViewById(musicGroup.getCheckedRadioButtonId());
        map.put("Duration",selectduration.getText().toString());
        map.put("MusicName",selectmusic.getText().toString());

        db.collection("MusicSettings").document(UID).set(map, SetOptions.merge()).addOnSuccessListener(aVoid -> {
            Log.d("TAG", "DocumentSnapshot successfully written!");
        });

        Intent intent = new Intent(InitialSilenceSettings.this, InitialAffirmationsSettings.class);
        startActivity(intent);
        finish();
        
    }

    public void getMusicLink(String musicName)
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

    //Gets the UID to use in this file anywhere.
    public void onResume() {
        super.onResume();
        fAuth = FirebaseAuth.getInstance();
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        getMusicNamesMaster();
    }

    //Returns the whole list of Music Names to display
    private void getMusicNamesMaster() {
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        DocumentReference typeref = db.collection("MusicSettings").document("MusicMaster");
        typeref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> musicNamesMaster=(List<String>)documentSnapshot.get("MusicNamesMaster");
                assert musicNamesMaster != null;
                String[] musicNamesArray = musicNamesMaster.toArray(new String[0]);
                //for loop over the array and setText to respective radio button 
                for(int i=0;i<musicNamesArray.length;i++){
                    switch(i){
                        case 0:
                            musicPrefer0.setText(musicNamesArray[i]);
                            break;
                        case 1:
                            musicPrefer1.setText(musicNamesArray[i]);
                            break;
                        case 2:
                            musicPrefer2.setText(musicNamesArray[i]);
                            break;
                        case 3:
                            musicPrefer3.setText(musicNamesArray[i]);
                            break;
                        case 4:
                            musicPrefer4.setText(musicNamesArray[i]);
                            break;
                        case 5:
                            musicPrefer5.setText(musicNamesArray[i]);
                            break;
                        case 6:
                            musicPrefer6.setText(musicNamesArray[i]);
                            break;
                        case 7:
                            musicPrefer7.setText(musicNamesArray[i]);
                            break;
                        case 8:
                            musicPrefer8.setText(musicNamesArray[i]);
                            break;
                        case 9:
                            musicPrefer9.setText(musicNamesArray[i]);
                            break;
                    }
                }
            }
        });
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

    //Stops the audio/music if it is playing
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

    /**
     * Create and show options in action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbarmenu, menu);
        return true;
    }

    /**
     * Operation to perform on item click in menu
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logoutButtonHeader) {
            openDialog();
            return true;
        }
        if (item.getItemId() == R.id.helpButtonHeader){
            //Add intro activity
            Intent i = new Intent(InitialSilenceSettings.this, IntroScreen1.class);
            //Intent i = new Intent(Home.this,InitialAffirmationSettings.class);
            startActivity(i);
        }
        if(item.getItemId() == R.id.shareButtonHeader){
            //Share the app link
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            String shareBody = "Hey, checkout my new favourite app, which helps you in your daily routine. \n To download the app click here \n https://github.com/iatharva/M3/releases";
            intent.setType("text/plain");
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
            intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(intent, getString(R.string.share_using)));
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Calls class for Log out
     */
    public void openDialog(){
        LogoutDialog logoutdialog=new LogoutDialog();
        logoutdialog.show(getSupportFragmentManager(),"Log out Dialog");
    }
}