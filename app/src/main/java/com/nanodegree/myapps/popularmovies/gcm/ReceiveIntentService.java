package com.nanodegree.myapps.popularmovies.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nanodegree.myapps.popularmovies.R;

/**
 * Created by Deepesh_Gupta1 on 10/02/2016.
 */

public class ReceiveIntentService extends IntentService {
    final static String TAG = ReceiveIntentService.class.getSimpleName();

    public ReceiveIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        Log.i(TAG, "GCM Registration Token: " + token);
    }

}
