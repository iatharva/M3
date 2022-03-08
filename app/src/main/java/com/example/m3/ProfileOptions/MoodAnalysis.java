package com.example.m3.ProfileOptions;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.example.m3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class MoodAnalysis extends AppCompatActivity {

    public String UID;
    private FirebaseAuth fAuth;
    private PieChart piechart;
    private BarChart barchart;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String[] moodMaster = new String[]{"Happy","Sad","Anxious","Nervous","Excited","Depressed","Neutral", "ExistentialCrisis","AmazinglyHappy"};
    private String[] colorMaster = new String[]{"#fff130","#225a87","#ad1717","#a4e8fc","#fca41e","#4a4a4a","#bba78e","#000000","#00945a"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_analysis);
        piechart = findViewById(R.id.piechart);
        barchart = findViewById(R.id.barchart);
        fAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        getUserData();
    }

    private void getUserData() {
        DocumentReference codesRef = db.collection("JournalingLogs").document(UID);
        codesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> dates = new ArrayList<>();
                List<Object> data = new ArrayList<>();
                Map<String, Object> map = task.getResult().getData();
                if (map != null) {
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        dates.add(entry.getKey());
                        data.add(entry.getValue());
                    }
                    ProcessData(dates,data);
                }
            }
        });
    }

    private void ProcessData(List<String> dates,List<Object> data) {

        String[] datesArray = dates.toArray(new String[0]);
        String[] emotions = new String[data.size()];
        int[] scale = new int[data.size()];
        for(int i=0;i<data.size();i++)
        {
            String[] temp = data.get(i).toString().split(",");
            emotions[i] = temp[temp.length-2];
            scale[i] = Integer.parseInt(temp[temp.length-1].replace("]","").replace("[","").replace(" ",""));
        }

        //Create map 
        Map<String, Integer> map = new HashMap<>();

        //Counting frequency of emotions
        for(int i=0;i<emotions.length;i++)
        {
            if(map.containsKey(emotions[i]))
                map.put(emotions[i],map.get(emotions[i])+1);
            else
                map.put(emotions[i],1);
        }

        for (int j=0;j<map.size();j++)
        {
            String mood = map.keySet().toArray()[j].toString().replace(" ","");
            if(moodMaster[0].equals(mood))
            {
                piechart.addPieSlice(new PieModel(map.keySet().toArray()[j].toString(),Integer.parseInt(map.values().toArray()[j].toString()), Color.parseColor(colorMaster[0])));
            }
            else if(moodMaster[1].equals(mood))
            {
                piechart.addPieSlice(new PieModel(map.keySet().toArray()[j].toString(),Integer.parseInt(map.values().toArray()[j].toString()), Color.parseColor(colorMaster[1])));
            }
            else if(moodMaster[2].equals(mood))
            {
                piechart.addPieSlice(new PieModel(map.keySet().toArray()[j].toString(),Integer.parseInt(map.values().toArray()[j].toString()), Color.parseColor(colorMaster[2])));
            }
            else if(moodMaster[3].equals(mood))
            {
                piechart.addPieSlice(new PieModel(map.keySet().toArray()[j].toString(),Integer.parseInt(map.values().toArray()[j].toString()), Color.parseColor(colorMaster[3])));
            }
            else if(moodMaster[4].equals(mood))
            {
                piechart.addPieSlice(new PieModel(map.keySet().toArray()[j].toString(),Integer.parseInt(map.values().toArray()[j].toString()), Color.parseColor(colorMaster[4])));
            }
            else if(moodMaster[5].equals(mood))
            {
                piechart.addPieSlice(new PieModel(map.keySet().toArray()[j].toString(),Integer.parseInt(map.values().toArray()[j].toString()), Color.parseColor(colorMaster[5])));
            }
            else if(moodMaster[6].equals(mood))
            {
                piechart.addPieSlice(new PieModel(map.keySet().toArray()[j].toString(),Integer.parseInt(map.values().toArray()[j].toString()), Color.parseColor(colorMaster[6])));
            }
            else if(moodMaster[7].equals(mood))
            {
                piechart.addPieSlice(new PieModel(map.keySet().toArray()[j].toString(),Integer.parseInt(map.values().toArray()[j].toString()), Color.parseColor(colorMaster[7])));
            }
            else if(moodMaster[8].equals(mood))
            {
                piechart.addPieSlice(new PieModel(map.keySet().toArray()[j].toString(),Integer.parseInt(map.values().toArray()[j].toString()), Color.parseColor(colorMaster[8])));
            }
            
        }

        piechart.startAnimation();

        for(int i=0;i<datesArray.length;i++)
        {
            barchart.addBar(new BarModel(datesArray[i],scale[i],Color.parseColor(colorMaster[i%9])));
        }
        barchart.startAnimation();

    }
}