package com.example.m3.ProfileOptions;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.m3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class SeeRanking extends AppCompatActivity {

    public String UID;
    private FirebaseAuth fAuth;
    private ListView listRank;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_ranking);
        listRank = findViewById(R.id.listRank);
        fAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        getUserList();
    }

    private void getUserList() {

        db.collection("Users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> usersList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    usersList.add(document.getId());
                }
                getTimeLogs(usersList);
                Log.d(TAG, usersList.toString());
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    private void getTimeLogs(List<String> usersList)
    {
        List<String> userTimeLogs = new ArrayList<String>();
        for (String user : usersList) {
            userTimeLogs.add(user + ": " + giveCount(user));
        }
        calculateRank(userTimeLogs);
    }


    private int giveCount(String userID) {
        AtomicInteger size = new AtomicInteger();
        db.collection("UserLogs").document(userID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> timeLogsList = new ArrayList<>();
                Map<String, Object> map = task.getResult().getData();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    timeLogsList.add(entry.getKey());
                    Log.d("TAG", entry.getKey());
                }
                size.set(timeLogsList.size());
                //return size[0];
            }
            else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
        return size.get();
    }
    
    public void calculateRank(List<String> userCount)
    {
        
    }
}