package com.nanodegree.myapps.popularmovies.gcm;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Deepesh_Gupta1 on 10/02/2016.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    final static String TAG = "popularmovies";

    public MyFirebaseInstanceIdService() {

    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        Intent intent = new Intent(this, ReceiveIntentService.class);
        startService(intent);
    }
}
