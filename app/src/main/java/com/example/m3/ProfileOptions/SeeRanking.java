package com.example.m3.ProfileOptions;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.m3.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class SeeRanking extends AppCompatActivity {

    public String UID;
    private FirebaseAuth fAuth;
    private ListView listRank;
    private TextView myRank,myCount,myDate;
    private LottieAnimationView animation_view;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_ranking);
        listRank = findViewById(R.id.listRank);
        myRank = findViewById(R.id.myPosition);
        myCount = findViewById(R.id.myCount);
        myDate = findViewById(R.id.myDate);
        animation_view = findViewById(R.id.animation_view);
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
                List<String> usersNameList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    usersList.add(document.getId());
                    usersNameList.add(document.getString("FName")+" "+document.getString("LName"));
                }
                getCounts(usersList,usersNameList);
                Log.d(TAG, usersList.toString());
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });

    }

    private void getCounts(List<String> usersList,List<String> usersNameList)
    {
        List<String> userData = new ArrayList<String>();

        db.collection("OtherLogs").document("CountLogs").get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                    for (String user : usersList)
                    {
                        userData.add(user + ":" + documentSnapshot.getString(user));
                    }
                    calculateRank(usersList,userData,usersNameList);
            }
        });

    }

    //Calculate rank and show on UI
    public void calculateRank(List<String> usersList,List<String> userData,List<String> usersNameList)
    {
       //for loop through userData
        List<Integer> userDataList = new ArrayList<Integer>();
        List<String> uidList = usersList;
        for (String user : userData)
        {
            String[] userDataSplit = user.split(":");
            userDataList.add(Integer.parseInt(userDataSplit[1]));
        }

        //Convert userDataList to integer array
        Integer[] userDataArray = userDataList.toArray(new Integer[0]);
        //Convert uidList to array
        String[] uidArray = uidList.toArray(new String[0]);
        String[] usersNameArray = usersNameList.toArray(new String[0]);

        //Sort an integer array in descending order
        for (int i = 0; i < userDataArray.length; i++) {
            for (int j = i+1; j < userDataArray.length; j++) {
                if(userDataArray[i] < userDataArray[j]) {
                    int temp = userDataArray[i];
                    userDataArray[i] = userDataArray[j];
                    userDataArray[j] = temp;

                    String temp1 = uidArray[i];
                    uidArray[i] = uidArray[j];
                    uidArray[j] = temp1;

                    String temp2 = usersNameArray[i];
                    usersNameArray[i] = usersNameArray[j];
                    usersNameArray[j] = temp2;
                }
            }
        }

        Date currDate = new Date(fAuth.getCurrentUser().getMetadata().getCreationTimestamp());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = dateFormat.format(currDate);

        List<Map<String, String>> data = new ArrayList<>();
        for (int i=0; i<usersNameArray.length; i++)
        {
            Map<String, String> datum = new HashMap<>(2);
            datum.put("title", usersNameArray[i]);
            datum.put("subtitle", Integer.toString(userDataArray[i]));
            data.add(datum);
        }
        //Code which should be used when you want to show two rows in one list item
        /*

        SimpleAdapter adapter = new SimpleAdapter(this, data,
        android.R.layout.simple_list_item_2,
        new String[] {"title", "subtitle"},
        new int[] {android.R.id.text1,
                android.R.id.text2});
        listRank.setAdapter(adapter);

        */
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                usersNameArray);
        listRank.setAdapter(adapter);

        //On click listener for each item in list show toast
        listRank.setOnItemClickListener((parent, view, position, id) -> {

            if(position==0)
            {
                Toast.makeText(SeeRanking.this,"This user is leading with Total "+userDataArray[position]+" logs",Toast.LENGTH_SHORT).show();
            }
            else
            {
                int rank=position+1;
                Toast.makeText(SeeRanking.this,"This user is "+rank+"th with total "+userDataArray[position]+" logs",Toast.LENGTH_SHORT).show();
            }
        });

        //for loop for finding index of current user using UID
        int index = 0;
        for (int i=0; i<uidArray.length; i++)
        {
            if(uidArray[i].equals(UID))
            {
                index = i;
            }
        }
        index=index+1;

        myRank.setText(index+"th position");
        if(index==1)
        {
            animation_view.setAnimation("toprankanimation.json");
            myRank.setText(index+"st position");
        }
        else if(index<=3)
            animation_view.setAnimation("tryingrankanimation.json");
        else
            animation_view.setAnimation("joggingrankanimation.json");

        myCount.setText(Integer.toString(userDataArray[index-1])+" logs");
        myDate.setText(date);
    }
}