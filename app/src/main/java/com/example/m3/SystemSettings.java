package com.example.m3;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SystemSettings extends AppCompatActivity {

    public TextView musicPreferred,visualizationTitle,ExerciseBtn1,ExerciseBtn2;
    public ImageView addAffBtn;
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
        visualizationTitle = findViewById(R.id.visualizationTitle);
        addAffBtn = findViewById(R.id.addAffBtn);
        ExerciseBtn1 = findViewById(R.id.sysSetting4Sub1);
        ExerciseBtn2 = findViewById(R.id.sysSetting4Sub2);
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
        //Show Affirmation Dialog
        addAffBtn.setOnClickListener(view -> AddAffirmationSettings());
        //Show Visualization Dialog
        visualizationTitle.setOnClickListener(view -> showVisualizationDialog(null));
        //Show Exercise Dialog
        ExerciseBtn1.setOnClickListener(view -> getExerciseSettings(false));
        ExerciseBtn2.setOnClickListener(view -> getExerciseSettings(true));
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
        DocumentReference visref = db.collection("VisualizationSettings").document(UID);
        visref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists())
            {
                String vName=documentSnapshot.getString("VName");
                visualizationTitle.setText(vName);
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
        ViewGroup.LayoutParams params = affirmationList.getLayoutParams();
        params.height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70*affirmations.size(), getResources().getDisplayMetrics());
        params.width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
        affirmationList.setLayoutParams(params);
        affirmationList.setAdapter(adapter);
        registerForContextMenu(affirmationList);
    }
    //Returns the list of exercises
    private void getExerciseSettings(boolean isExercise) {
        DocumentReference affref = db.collection("ExerciseSettings").document(UID);
        affref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> pranayama = (List<String>) documentSnapshot.get("Pranayama");
                List<String> exercise = (List<String>) documentSnapshot.get("Exercise");
                showExerciseDialog(pranayama,exercise,isExercise);
            }
        });
    }

    private void AddAffirmationSettings()
    {
        DocumentReference affref = db.collection("AffirmationSettings").document(UID);
        affref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists())
            {
                List<String> affirmationsList=(List<String>)documentSnapshot.get("Sentences");
                String[] affirmationsArray = affirmationsList.toArray(new String[0]);
                //Last index + 1
                showAffirmationDialog(affirmationsList.size() + 1,affirmationsArray,true);
            }
        });
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
    public void updateAffirmationSettings(String updatedAffirmation,int position, String [] affirmationsList, Boolean IsNew)
    {
        if(!IsNew)
        {
            if(!updatedAffirmation.equals(affirmationsList[position]))
            {
                affirmationsList[position] = updatedAffirmation;
                DocumentReference musicref = db.collection("AffirmationSettings").document(UID);
                musicref
                        .update("Sentences", Arrays.asList(affirmationsList))
                        .addOnSuccessListener(aVoid -> Toast.makeText(this,"Affirmation Updated",Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Log.w(TAG, "Affirmation not updated. Error :", e));
            }
            else
            {
                Toast.makeText(this,"Please enter something in textbox" + newDuration,Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            if(updatedAffirmation!=null)
            {
                //add a new element to to the array
                affirmationsList = Arrays.copyOf(affirmationsList, affirmationsList.length + 1);
                affirmationsList[affirmationsList.length - 1] = updatedAffirmation;
                DocumentReference musicref = db.collection("AffirmationSettings").document(UID);
                musicref
                        .update("Sentences", Arrays.asList(affirmationsList))
                        .addOnSuccessListener(aVoid -> Toast.makeText(this,"Affirmation Added",Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Log.w(TAG, "Affirmation not added. Error :", e));
            }
            else
            {
                Toast.makeText(this,"Please enter something in textbox" + newDuration,Toast.LENGTH_SHORT).show();
            }
        }

    }

    //Updates the affirmation
    public void updateVisualizationSettings(Bitmap vData)
    {
        String filePathAndName="Visualizations/"+UID;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
            vData.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    String downloadUri = uriTask.getResult().toString();
                    if (uriTask.isSuccessful()) {
                        DocumentReference visualref = db.collection("VisualizationSettings").document(UID);
                        visualref
                                .update("VLink", downloadUri)
                                .addOnSuccessListener(aaVoid -> Toast.makeText(SystemSettings.this, "Visualization Updated", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Log.w(TAG, "Visualization not updated. Error :", e));
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(SystemSettings.this, "Failed Uploading image", Toast.LENGTH_SHORT).show());
    }

    public void updateExerciseSettings(String[] originalPreference,String newItem,boolean isExercise)
    {
        if(isExercise)
        {
            originalPreference = Arrays.copyOf(originalPreference, originalPreference.length + 1);
            originalPreference[originalPreference.length - 1] = newItem;
            DocumentReference exerref = db.collection("ExerciseSettings").document(UID);
            exerref
                    .update("Exercise", Arrays.asList(originalPreference))
                    .addOnSuccessListener(aVoid -> Toast.makeText(this,newItem+" added",Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Log.w(TAG, "Exercise not added. Error :", e));
        }
        else
        {
            originalPreference = Arrays.copyOf(originalPreference, originalPreference.length + 1);
            originalPreference[originalPreference.length - 1] = newItem;
            DocumentReference exerref = db.collection("ExerciseSettings").document(UID);
            exerref
                    .update("Pranayama", Arrays.asList(originalPreference))
                    .addOnSuccessListener(aVoid -> Toast.makeText(this,newItem+" added",Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Log.w(TAG, "Pranayama not added. Error :", e));
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
            DocumentReference affref = db.collection("AffirmationSettings").document(UID);
            affref
                    .update("Sentences",Arrays.asList(newAffirmations))
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this,"Affirmation deleted",Toast.LENGTH_SHORT).show();
                        getUserSettings();})
                    .addOnFailureListener(e -> Log.w(TAG, "Affirmation not deleted. Error :", e));
        }
    }

    //delete the Exercise
    public void deleteExerciseSettings(int position,String [] originalArray,boolean isExercise)
    {
        //Remove affirmation[position] from affirmations array
        String[] updatedArray = new String[originalArray.length-1];
        int i=0;
        for(int j=0;j<originalArray.length;j++)
        {
            if(j!=position)
            {
                updatedArray[i]=originalArray[j];
                i++;
            }
        }
        DocumentReference affref = db.collection("ExerciseSettings").document(UID);
        if(isExercise)
        {
            affref
                    .update("Exercise",Arrays.asList(updatedArray))
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this,"Exercise removed",Toast.LENGTH_SHORT).show();
                        getUserSettings();})
                    .addOnFailureListener(e -> Log.w(TAG, "Exercise not deleted. Error :", e));
        }
        else
        {
            affref
                    .update("Pranayama",Arrays.asList(updatedArray))
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this,"Pranayama deleted",Toast.LENGTH_SHORT).show();
                        getUserSettings();})
                    .addOnFailureListener(e -> Log.w(TAG, "Pranayama not deleted. Error :", e));
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

    //Returns the boolean array of the present items in selectedItemsArray
    public boolean [] getCheckedArray(List<String> original, List<String> selectedItems)
    {
        boolean [] checkedItems = new boolean[]{false,false,false,false,false,false,false};
        String [] originalArray = original.toArray(new String[original.size()]);
        String [] selectedItemsArray = selectedItems.toArray(new String[selectedItems.size()]);
        for(int i = 0; i < originalArray.length; i++)
        {
            for (String s : selectedItemsArray)
            {
                if (originalArray[i].equals(s))
                    checkedItems[i] = true;
            }
        }
        return checkedItems;
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
                    showAffirmationDialog(adapter.position,affirmationsArray,false);
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

    //Shows the Input Dialog box on UI to update affirmations
    //AddExtra paramter for new
    private void showAffirmationDialog(int position,String [] affirmations,Boolean NewAff) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SystemSettings.this);
        final EditText updatedAffirmation = new EditText(SystemSettings.this);
        updatedAffirmation.setInputType(InputType.TYPE_CLASS_TEXT);
        if(NewAff)
        {
            builder.setTitle("Add affirmation");
            builder.setView(updatedAffirmation);
            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                dialogInterface.dismiss();
                getUserSettings();
            });
            builder.setPositiveButton("Add", (dialogInterface, i) -> {
                updateAffirmationSettings(updatedAffirmation.getText().toString(),position,affirmations,true);
                dialogInterface.dismiss();
                getUserSettings();
            });
        }
        else
        {
            builder.setTitle("Edit affirmation");
            updatedAffirmation.setText(affirmations[position]);
            builder.setView(updatedAffirmation);
            builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                dialogInterface.dismiss();
                getUserSettings();
            });
            builder.setPositiveButton("Update", (dialogInterface, i) -> {
                updateAffirmationSettings(updatedAffirmation.getText().toString(),position,affirmations,false);
                dialogInterface.dismiss();
                getUserSettings();
            });
        }
        builder.show();
    }

    private void showVisualizationDialog(Uri link)
    {
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        DocumentReference typeref = db.collection("VisualizationSettings").document(UID);
        typeref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists())
            {
                String VName= documentSnapshot.getString("VName");
                String VLink= documentSnapshot.getString("VLink");
                AlertDialog.Builder builder = new AlertDialog.Builder(SystemSettings.this);
                builder.setTitle(VName);
                final ImageView originalVisual = new ImageView(SystemSettings.this);
                if(link!=null)
                {
                    builder.setMessage("Click on Save to update the visualization");
                    Picasso
                            .get()
                            .load(link)
                            .placeholder( R.drawable.loadinganimation)
                            .into(originalVisual);
                    builder.setPositiveButton("Save", (dialogInterface, i) -> {
                        InputStream is = null;
                        try {
                            is = getContentResolver().openInputStream(link);
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            is.close();
                            updateVisualizationSettings(bitmap);
                            getUserSettings();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    });
                }
                else
                {
                    builder.setMessage("Click on replace to change visualization");
                    Picasso
                            .get()
                            .load(VLink)
                            .placeholder( R.drawable.loadinganimation)
                            .into(originalVisual);
                    builder.setPositiveButton("Replace", (dialogInterface, i) -> {
                        imageChooser();
                        getUserSettings();
                    });
                }
                builder.setView(originalVisual);
                builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    getUserSettings();
                });
                builder.show();
            }
        });
    }

    private void showExerciseDialog(List<String> pranayama, List<String> exercise,boolean isExercise)
    {
        DocumentReference typeref = db.collection("ExerciseSettings").document("ExerciseMaster");
        typeref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists())
            {
                List<String> pranayamaMaster = (List<String>) documentSnapshot.get("Pranayama");
                List<String> exerciseMaster = (List<String>) documentSnapshot.get("Exercise");
                String[] pranayamaArrayMaster = pranayamaMaster.toArray(new String[0]);
                String[] exerciseArrayMaster = exerciseMaster.toArray(new String[0]);

                AlertDialog.Builder builder = new AlertDialog.Builder(SystemSettings.this);
                if(isExercise) {
                    builder.setTitle("Excercise");
                    builder.setMultiChoiceItems(
                            exerciseArrayMaster,
                            getCheckedArray(exerciseMaster,exercise),
                            (DialogInterface.OnMultiChoiceClickListener) (dialog, which, isChecked) -> {
                                if (isChecked)
                                    updateExerciseSettings(exercise.toArray(new String[0]),exerciseArrayMaster[which],isExercise);
                                else
                                    deleteExerciseSettings(getArrayIndex(exercise.toArray(new String[0]),exerciseArrayMaster[which]),exercise.toArray(new String[0]),isExercise);
                            });
                }
                else{
                    builder.setTitle("Pranayama");
                    builder.setMultiChoiceItems(
                            pranayamaArrayMaster,
                            getCheckedArray(pranayamaMaster,pranayama),
                            (DialogInterface.OnMultiChoiceClickListener) (dialog, which, isChecked) -> {
                                if (isChecked)
                                    updateExerciseSettings(pranayama.toArray(new String[0]),pranayamaArrayMaster[which],isExercise);
                                else
                                    deleteExerciseSettings(getArrayIndex(pranayama.toArray(new String[0]),pranayamaArrayMaster[which]),pranayama.toArray(new String[0]),isExercise);
                            });
                }

                builder.show();
            }
        });
    }

    public void imageChooser()
    {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"),0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    showVisualizationDialog(selectedImageUri);
                }
            }
        }
    }}