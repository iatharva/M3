package com.example.m3.ProfileOptions;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.m3.Account.LogIn;
import com.example.m3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JournalLogs extends AppCompatActivity {

    public String UID;
    private FirebaseAuth fAuth;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListView listJournal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_logs);
        listJournal = findViewById(R.id.listJournal);
        fAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        fAuth = FirebaseAuth.getInstance();
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        getUserData();
    }

    private void getUserData() {
        db.collection("JournalingLogs").document(UID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                //Get all the data in HashMap
                Map<String, Object> data = documentSnapshot.getData();
                showLogs(sortDateList(data));
            }
        });
    }

    private void showLogs(Map<String, Object> data) {
        //get the list of keys from the map
        List<String> keys = new ArrayList<>(data.keySet());

        //get the list of values from the map
        List<Object> values = new ArrayList<>(data.values());
        List<String> values_string = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            String[] parts = values.get(i).toString().split(",");
            String journalEntry="";
            for (int j = 0; j < parts.length; j++) {
                if(j<(parts.length-2))
                {
                    journalEntry = journalEntry + parts[j];
                }
                else{
                    values_string.add(journalEntry.replace("[",""));
                    break;
                }
            }
        }


        List<Map<String, String>> dataList = new ArrayList<>();
        for (int i=0; i<values_string.size(); i++)
        {
            Map<String, String> datum = new HashMap<>(2);
            datum.put("title", keys.get(i));
            String journal=values_string.get(i).replace("[", "").replace("]", "");
            //get first 3 characters from journal
            String first3 = journal.substring(0, 5);
            datum.put("subtitle", first3+"...");
            dataList.add(datum);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, dataList,
                android.R.layout.simple_list_item_2,
                new String[] {"title", "subtitle"},
                new int[] {android.R.id.text1,
                        android.R.id.text2});
        listJournal.setAdapter(adapter);

        listJournal.setOnItemClickListener((parent, view, position, id) -> {
            //Send title and subtitle to the next activity
            //Intent intent = new Intent(JournalLogs.this, JournalLogsDetails.class);
            //intent.putExtra("title", keys.get(position));
            //intent.putExtra("subtitle", values_string.get(position));
            //startActivity(intent);
            AlertDialog.Builder builder=new AlertDialog.Builder(JournalLogs.this);
            builder.setTitle(keys.get(position))
                    .setMessage(values_string.get(position));
            builder.setCancelable(true);
            builder.show();
        });

    }

    //to sort the list of string which has the dates in format DD-MM-YYYY where latest date is at the first index
    private Map<String, Object> sortDateList(Map<String, Object> list)
    {
        //Sort the Map keys with values in ascending order
        List<String> keys = new ArrayList<>(list.keySet());
        Collections.sort(keys);
        Map<String, Object> sortedMap = new HashMap<>();
        for (String key : keys) {
            sortedMap.put(key, list.get(key));
        }
        return sortedMap;
    }
}