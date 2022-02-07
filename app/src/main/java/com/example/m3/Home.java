package com.example.m3;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.m3.extras.LogoutDialog;

public class Home extends AppCompatActivity {

    public Button HomeBtn,ProfileBtn;
    public ImageButton SettingsBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        HomeBtn = findViewById(R.id.HomeBtn);
        ProfileBtn = findViewById(R.id.ProfileBtn);
        SettingsBtn = findViewById(R.id.SettingsBtn);
        //Set by default Home fragment
        replaceFragment(new Fhome());
        //Setting respective fragment on selection
        ProfileBtn.setOnClickListener(v -> replaceFragment(new Profile()));
        HomeBtn.setOnClickListener(view -> replaceFragment(new Fhome()));
        SettingsBtn.setOnClickListener(view -> replaceFragment(new Settings()));
    }

    /**
     * Replace the fragment with the selected option
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }

    /**
     * Create and show options in action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbarmenu, menu);
        return true;
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
            Intent i = new Intent(Home.this,IntroScreen1.class);
            startActivity(i);
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
}