package com.example.m3.extras;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import com.example.m3.LogIn;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Dialog Box which performs log out of the user
 */
public class LogoutDialog extends AppCompatDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        //Alert Dialog Box
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("LogOut")
                .setMessage("Are you sure want to log out?")
                //Log out accepted
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseAuth.getInstance().signOut();
                        Intent intent=new Intent(getActivity(), LogIn.class);
                        startActivity(intent);
                        requireActivity().finish();

                    }
                })
                //Log out declined
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }
}

