package com.example.m3.extras;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class App extends Application {
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel 1");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Channel 2",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel2.setDescription("This is Channel 2");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }
    }
}

//Code which should be used for notification
   /* NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "My Notification");
            builder.setContentTitle("Sample M3 notification");
                    builder.setContentText("This is description");
                    builder.setSmallIcon(R.drawable.m3logoonly);
                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getActivity());
                    managerCompat.notify(1, builder.build());
                    Toast.makeText(getActivity(),"Notification should be visible",Toast.LENGTH_SHORT).show();*/