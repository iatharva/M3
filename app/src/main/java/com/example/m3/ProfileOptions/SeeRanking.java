package com.example.m3.ProfileOptions;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private TextView myRank,myCount,myDate,myRankTitle;
    private LottieAnimationView animation_view;
    private ImageView shareRank;
    private RelativeLayout userCard;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_ranking);
        listRank = findViewById(R.id.listRank);
        myRankTitle = findViewById(R.id.myRankTitle);
        myRank = findViewById(R.id.myPosition);
        myCount = findViewById(R.id.myCount);
        shareRank = findViewById(R.id.shareRank);
        myDate = findViewById(R.id.myDate);
        userCard =findViewById(R.id.userCard);
        animation_view = findViewById(R.id.animation_view);
        fAuth = FirebaseAuth.getInstance();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        shareRank.setOnClickListener(view -> {
            Dexter.withActivity(SeeRanking.this).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {

                                File file = saveBitMap(SeeRanking.this, userCard);
                                if (file != null) {
                                    Log.i("TAG", "Drawing saved to the gallery!");

                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.setType("image/*");
                                    intent.putExtra(Intent.EXTRA_TEXT, "Check out my rank in M3\n Join me in the race of having healthy routine :\n https://github.com/iatharva/M3/releases");
                                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                                    SeeRanking.this.startActivity(Intent.createChooser(intent, "Share Image"));

                                } else {
                                    Log.i("TAG", "Oops! Image could not be saved.");
                                    Toast.makeText(SeeRanking.this, "Failed sharing!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(SeeRanking.this, "Permissions are not granted!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> list, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();
        });
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

        String[] userNameSplit = usersNameArray[index-1].split(" ");
        myRankTitle.setText(userNameSplit[0]+"'s Rank");
        myCount.setText(Integer.toString(userDataArray[index-1])+" days");
        String[] dateSplit = date.split(" ");
        myDate.setText(dateSplit[0]);
    }

    private File saveBitMap(Context context, View drawView) {
        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Handcare"); // enter folder name to save image
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if (!isDirectoryCreated)
                Log.i("ATG", "Can't create directory to save the image");
            return null;
        }
        String filename = pictureFileDir.getPath() + File.separator + System.currentTimeMillis() + ".jpg";
        File pictureFile = new File(filename);
        Bitmap bitmap = getBitmapFromView(drawView);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        scanGallery(context, pictureFile.getAbsolutePath());
        return pictureFile;
    }

    //create bitmap from view and returns it
    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    private void scanGallery(Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}