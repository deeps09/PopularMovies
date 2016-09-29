package com.nanodegree.myapps.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Deepesh_Gupta1 on 09/29/2016.
 */

public class MoviesAuthenticatorService extends Service {

    // Instance field that holds authenticator object
    private MoviesAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new MoviesAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
