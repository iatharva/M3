package com.example.m3.InitialSettings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dragankrstic.autotypetextview.AutoTypeTextView;
import com.example.m3.Intros.IntroScreen1;
import com.example.m3.R;
import com.example.m3.Extras.LogoutDialog;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InitialVisualizationSettings extends AppCompatActivity {

    private String UID;
    private FloatingActionButton toNextScreenBtn;
    private FirebaseAuth fAuth;
    private AutoTypeTextView subTitle1;
    private EditText visualizationTitle;
    private TextView addImageText;
    private ImageView visualizationImage;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_visualization_settings);
        subTitle1 = findViewById(R.id.subTitle1);
        toNextScreenBtn = findViewById(R.id.toNextScreenBtn);
        visualizationTitle = findViewById(R.id.visualizationTitle);
        visualizationImage = findViewById(R.id.visualizationImage);

        subTitle1.setTextAutoTyping("Please select the image for you visualization and name it");
        toNextScreenBtn.setVisibility(View.GONE);

        visualizationImage.setOnClickListener(view -> {
            String VTitle = visualizationTitle.getText().toString();
            if(TextUtils.isEmpty(VTitle)){
                Toast.makeText(InitialVisualizationSettings.this,"Please enter title first then add image",Toast.LENGTH_SHORT).show();
                return;
            }
            imageChooser();
        });

        toNextScreenBtn.setOnClickListener(view -> {
            Intent intent = new Intent(InitialVisualizationSettings.this, InitialExercisesSettings.class);
            startActivity(intent);
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
                    Picasso
                            .get()
                            .load(selectedImageUri)
                            .placeholder( R.drawable.loadinganimation)
                            .into(visualizationImage);
                    saveVisualizationSettings(selectedImageUri);
                    toNextScreenBtn.setVisibility(View.VISIBLE);
                }
            }
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

    //Gets the UID to use in this file anywhere.
    public void onResume() {
        super.onResume();
        fAuth = FirebaseAuth.getInstance();
        UID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
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
            Intent i = new Intent(InitialVisualizationSettings.this, IntroScreen1.class);
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

    public void saveVisualizationSettings(Uri link)
    {
        InputStream is = null;
        try {
            is = getContentResolver().openInputStream(link);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            String filePathAndName="Visualizations/"+UID;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String downloadUri = uriTask.getResult().toString();
                        if (uriTask.isSuccessful()) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("VLink",downloadUri);
                            map.put("VName",visualizationTitle.getText().toString());
                            db.collection("VisualizationSettings").document(UID).set(map, SetOptions.merge()).addOnSuccessListener(aVoid -> {
                                Log.d("TAG", "DocumentSnapshot successfully written!");
                            });
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(InitialVisualizationSettings.this, "Failed Uploading image", Toast.LENGTH_SHORT).show());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}