package com.example.m3;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Visualization extends AppCompatActivity {

    public TextView visualizationTitle,readMore;
    public FloatingActionButton toDoneScreenBtn;
    public ImageView image;
    private FirebaseAuth fAuth;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public String UID;
    public Vibrator vibe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualization);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        toDoneScreenBtn = findViewById(R.id.toDoneScreenBtn);
        visualizationTitle = findViewById(R.id.T1Desc4);
        readMore = findViewById(R.id.T1Desc2);
        fAuth = FirebaseAuth.getInstance();
        image = findViewById(R.id.Image);

        //for making link clickable
        readMore.setMovementMethod(LinkMovementMethod.getInstance());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            readMore.setText(Html.fromHtml("<a href='https://www.unfinishedsuccess.com/the-importance-of-visualizing-your-goals/'>read more</a>", Html.FROM_HTML_MODE_LEGACY));
        } else {
            readMore.setText(Html.fromHtml("<a href='https://www.unfinishedsuccess.com/the-importance-of-visualizing-your-goals/'>read more</a>"));
        }

        //Shows the dialog
        toDoneScreenBtn.setOnClickListener(view -> {
            showCustomDialog();
        });
    }

    //Called on Activity launch
    @Override
    public void onResume()
    {
        super.onResume();
        getUserSettings();
    }

    //Go to home screen on back button press
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(Visualization.this, Home.class));
        finish();
    }

    //Gets the data required
    public void getUserSettings()
    {
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        DocumentReference visref = db.collection("VisualizationSettings").document(UID);
        visref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists())
            {
                String vName=documentSnapshot.getString("VName");
                String VLink= documentSnapshot.getString("VLink");
                visualizationTitle.setText(vName);
                Picasso
                        .get()
                        .load(VLink)
                        .placeholder( R.drawable.loadinganimation)
                        .into(image);
            }
        });
    }

    //Shows the alert dialog upon completion of activity
    private void showCustomDialog()
    {
        getUserTimeLogs();
        vibe.vibrate(100);
        AlertDialog.Builder builder = new AlertDialog.Builder(Visualization.this);
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_completedactivity, null);
        //message
        TextView message = customLayout.findViewById(R.id.message);
        message.setText("Visualizations completed !");
        builder.setPositiveButton("Go ahead", (dialogInterface, i) -> {
            //To go to next activity
            Intent intent = new Intent(Visualization.this, Exercises.class);
            startActivity(intent);
        });
        builder.setCancelable(false);
        builder.setView(customLayout);
        builder.show();
    }

    //Get the specific date TimeLogs
    private void getUserTimeLogs()
    {
        String today = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        DocumentReference affref = db.collection("UserTimeLogs").document(UID);
        affref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists())
            {
                List<String> timeLogs=(List<String>)documentSnapshot.get(today+"-TimeLog");
                String[] timeLogsArray = timeLogs.toArray(new String[0]);
                updateUserLogs(timeLogsArray);
            }
        });
    }

    //Updates the TimeLogs
    private void updateUserLogs(String[] timeLogsArray)
    {
        String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        String today = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        timeLogsArray[2] = timestamp;
        DocumentReference usertimelogref = db.collection("UserTimeLogs").document(UID);
        usertimelogref
                .update(today+"-TimeLog", Arrays.asList(timeLogsArray))
                .addOnSuccessListener(aVoid -> Toast.makeText(this,"Let's go",Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.w(TAG, "Time log not updated. Error :", e));

        //Add UserLog
        List<Boolean> ActivityLogString = new ArrayList<>();
        for(int i=0;i<6;i++)
        {
            if(i<=2)
            {
                ActivityLogString.add(true);
            }
            else
                ActivityLogString.add(false);
        }
        DocumentReference userlogref = db.collection("UserLogs").document(UID);
        userlogref
                .update(today, ActivityLogString)
                .addOnSuccessListener(aVoid -> Toast.makeText(this,"Let's go",Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.w(TAG, "Routine log not updated. Error :", e));

    }
}