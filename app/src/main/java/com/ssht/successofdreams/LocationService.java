package com.ssht.successofdreams;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.ssht.successofdreams.App.CHANNEL_ID;

public class LocationService  extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        //I used notification bar just to show that the app is running(location is updating) even if it is killed

        Intent notificationIntent = new Intent(this,LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,notificationIntent,0);


        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("Tech Active")
                .setContentText("Location Updating per Minute")
                .setSmallIcon(R.drawable.ic_baseline_location_on_24)
               // .setContentIntent(pendingIntent)
                .build();

        startForeground(1,notification);
        return  START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
