package com.nanodegree.myapps.popularmovies.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nanodegree.myapps.popularmovies.MainActivity;
import com.nanodegree.myapps.popularmovies.R;

/**
 * Created by Deepesh_Gupta1 on 10/02/2016.
 */

public class MyReceiveMessagingService extends FirebaseMessagingService {
    final static String TAG = MyReceiveMessagingService.class.getSimpleName();
    final static int NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        showNotification(remoteMessage.getNotification().getBody());
    }

    private void showNotification (String message){
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_favorite);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(message)
                .setSmallIcon(R.drawable.ic_favorite)
                .setLargeIcon(largeIcon)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        mBuilder.setContentIntent(contentIntent);
        manager.notify(TAG, NOTIFICATION_ID, mBuilder.build());
    }
}
