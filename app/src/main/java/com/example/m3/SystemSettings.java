package com.example.m3;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class SystemSettings extends AppCompatActivity {

    public TextView musicPreferred;
    public ListView affirmationList;
    public Spinner durationPreferred;
    public MediaPlayer mediaPlayer;
    public String oldDuration,newDuration,UID,oldMusic;
    private FirebaseAuth fAuth;
    public ArrayAdapter<String> adapter;
    public String [] durations = {"5 minutes","10 minutes","15 minutes","20 minutes" };
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Initialization
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_settings);
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        musicPreferred = findViewById(R.id.musicPreferred);
        durationPreferred = findViewById(R.id.durationPreferred);
        affirmationList = findViewById(R.id.affirmationList);
        fAuth = FirebaseAuth.getInstance();
        //Refresh and reload the data
        pullToRefresh.setOnRefreshListener(() -> {
            getUserSettings();
            pullToRefresh.setRefreshing(false);
        });
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
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
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
        DocumentReference musicref = db.collection("MusicSettings").document(UID);
        musicref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists())
            {
                oldDuration=documentSnapshot.getString("Duration");
                oldMusic=documentSnapshot.getString("MusicName");
                musicPreferred.setText(oldMusic);
                durationPreferred.setSelection(getArrayIndex(durations,oldDuration));
            }
        });
        DocumentReference affref = db.collection("AffirmationSettings").document(UID);
        affref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists())
            {
                List<String> affirmations=(List<String>)documentSnapshot.get("Sentences");
                getAffirmationSettings(affirmations);
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

    //Returns the list of affirmations
    private void getAffirmationSettings(List<String> affirmations) {
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,affirmations);
        affirmationList.setAdapter(adapter);
        registerForContextMenu(affirmationList);
    }

    //Updates the music choice of user
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

    //Updates the affirmation
    public void updateAffirmationSettings(String updatedAffirmation,int position, String [] affirmationsList)
    {
        if(!updatedAffirmation.equals(affirmationsList[position]))
        {
            affirmationsList[position] = updatedAffirmation;
            DocumentReference musicref = db.collection("AffirmationSettings").document(UID);
            musicref
                    .update("Sentences", Arrays.asList(affirmationsList))
                    .addOnSuccessListener(aVoid -> Toast.makeText(this,"Affirmation Updated",Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Log.w(TAG, "Duration not updated. Error :", e));
        }
        else
        {
            Toast.makeText(this,"Please enter something in textbox" + newDuration,Toast.LENGTH_SHORT).show();
        }
    }

    //delete the affirmation
    public void deleteAffirmationSettings(int position,String [] affirmations)
    {
        //Remove affirmation[position] from affirmations array
        String[] newAffirmations = new String[affirmations.length-1];
        int i=0;
        for(int j=0;j<affirmations.length;j++)
        {
            if(j!=position)
            {
                newAffirmations[i]=affirmations[j];
                i++;
            }
        }
        
        if(newAffirmations!=null)
        {
            DocumentReference musicref = db.collection("AffirmationSettings").document(UID);
            musicref
                    .update("Sentences",Arrays.asList(newAffirmations))
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this,"Affirmation deleted",Toast.LENGTH_SHORT).show();
                        getUserSettings();})
                    .addOnFailureListener(e -> Log.w(TAG, "Duration not updated. Error :", e));
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

    //For creating menu for affirmation
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.affirmation_menu, menu);
    }

    //Code for action to perform on menu option selection
    @Override
    public boolean onContextItemSelected(MenuItem item){

        AdapterView.AdapterContextMenuInfo adapter = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        DocumentReference affref = db.collection("AffirmationSettings").document(UID);
        affref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists())
            {
                List<String> affirmationsList=(List<String>)documentSnapshot.get("Sentences");
                String[] affirmationsArray = affirmationsList.toArray(new String[0]);

                if(item.getItemId()==R.id.editOptn){
                    showAffirmationDialog(adapter.position,affirmationsArray);
                }
                else if(item.getItemId()==R.id.deleteOptn){
                    AlertDialog.Builder builder = new AlertDialog.Builder(SystemSettings.this);
                    builder.setTitle("Delete")
                            .setMessage("Are you sure you want to delete this affirmation?")
                            .setPositiveButton("Yes", (dialogInterface, i) -> {
                                deleteAffirmationSettings(adapter.position,affirmationsArray);
                                dialogInterface.dismiss();
                            })
                            .setNegativeButton("Cancel", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                            });
                    builder.show();
                }
            }
        });

        return true;
    }

    //Shows the Input Dialog box on UI to update affirmations
    private void showAffirmationDialog(int position,String [] affirmations) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SystemSettings.this);
        builder.setTitle("Edit affirmation");
        final EditText updatedAffirmation = new EditText(SystemSettings.this);
        updatedAffirmation.setInputType(InputType.TYPE_CLASS_TEXT);
        updatedAffirmation.setText(affirmations[position]);
        builder.setView(updatedAffirmation);
                builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                    stopAudio();
                    dialogInterface.dismiss();
                    getUserSettings();
                })
                .setPositiveButton("Update", (dialogInterface, i) -> {
                    updateAffirmationSettings(updatedAffirmation.getText().toString(),position,affirmations);
                    dialogInterface.dismiss();
                    getUserSettings();
                });;
        builder.show();
    }
}