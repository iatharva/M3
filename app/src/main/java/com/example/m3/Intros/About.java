package com.example.m3.Intros;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.m3.R;

public class About extends AppCompatActivity {

    public ImageView socialBtn,socialBtn1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        socialBtn = findViewById(R.id.socialBtn);
        socialBtn1 = findViewById(R.id.socialBtn1);

        socialBtn.setOnClickListener(view -> {
            //Open Github page in browser
            String url = "https://github.com/iatharva/m3";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        socialBtn1.setOnClickListener(view -> {
            //Open Github release page in browser
            String url = "https://github.com/iatharva/M3/releases/tag/beta";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
    }

}