package com.nanodegree.myapps.popularmovies;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static String LOG_TAG = MainActivity.class.getSimpleName();
    static final String BASE_URL = "https://api.themoviedb.org/3/movie/popular?api_key=69b589af19cead810bc805ab8f5363f6&";

    ProgressBar progressBar;
    GridView gridViewMovies;
    MoviesArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        gridViewMovies = (GridView) findViewById(R.id.movies_gridview);

        arrayAdapter = new MoviesArrayAdapter(this);
        gridViewMovies.setAdapter(arrayAdapter);

        backgroundTask bgTask = new backgroundTask();

        if (checkInternetAccess())
            bgTask.execute(BASE_URL);
        else
            Toast.makeText(this, "No Internet Access", Toast.LENGTH_SHORT).show();
    }

    private class backgroundTask extends AsyncTask<String, Void, ArrayList<Movies>> {
        @Override
        protected ArrayList<Movies> doInBackground(String... params) {
            Log.i(LOG_TAG, params[0]);
            return Utilities.fetchJSONDataFromInternet(params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Movies> data) {
            gridViewMovies.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            arrayAdapter.addAll(data);
            //Log.i(LOG_TAG, jsonString);
        }
    }

    private Boolean checkInternetAccess() {
        ConnectivityManager conManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        Boolean isNetConnected = false;

        if (networkInfo != null || networkInfo.isConnectedOrConnecting())
            isNetConnected = true;

        return isNetConnected;
    }
}
