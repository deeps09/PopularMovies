package com.nanodegree.myapps.popularmovies;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static String LOG_TAG = MainActivity.class.getSimpleName();
    //static final String BASE_URL = "https://api.themoviedb.org/3/movie/popular?api_key=69b589af19cead810bc805ab8f5363f6&";
    //int page = 1;
    static final String BASE_URL = "https://api.themoviedb.org/3/movie";
    static final String API_KEY = "69b589af19cead810bc805ab8f5363f6";
    Boolean loadNewData = true;
    ProgressBar progressBar;
    GridView gridViewMovies;
    MoviesArrayAdapter moviesAdapter;
    ArrayList<Movies> moviesList;
    String lastPreference = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lastPreference   = Utilities.getPreferenceSortBy(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        gridViewMovies = (GridView) findViewById(R.id.movies_gridview);

        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")){
            Log.i(LOG_TAG, "No Parcelable data available");
            moviesList = new ArrayList<Movies>();
            backgroundTask bgTask = new backgroundTask();

            // Checking internet connection
            if (checkInternetAccess())
                bgTask.execute(constructURL());
            else
                Toast.makeText(this, "No Internet Access", Toast.LENGTH_SHORT).show();
        } else{
            Log.i(LOG_TAG, "Found Parcelable data");
            moviesList = savedInstanceState.getParcelableArrayList("movies");
            progressBar.setVisibility(View.GONE);
            gridViewMovies.setVisibility(View.VISIBLE);
            loadNewData = false;
        }

        moviesAdapter = new MoviesArrayAdapter(this, moviesList);
        gridViewMovies.setAdapter(moviesAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuitem_sortBy){
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName());
            intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true); // To not show headers
            startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", moviesList);
        super.onSaveInstanceState(outState);
        Log.i(LOG_TAG, "onSaveInstanceState()");
    }

    @Override
    protected void onResume() {
        Log.i(LOG_TAG, "onResume()");

        // Is preference changed
        if (!lastPreference.equals(Utilities.getPreferenceSortBy(this))){
            this.finish();
            startActivity(new Intent(this, MainActivity.class));
            Log.i(LOG_TAG, "onRestart() called from onResume()");
            Toast.makeText(this, "Preference changed loading afresh", Toast.LENGTH_SHORT).show();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i(LOG_TAG, "onPause() ");
        lastPreference = Utilities.getPreferenceSortBy(this);
        super.onPause();
    }

    private class backgroundTask extends AsyncTask<String, Void, ArrayList<Movies>> {
        @Override
        protected ArrayList<Movies> doInBackground(String... params) {
            return Utilities.fetchJSONDataFromInternet(params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Movies> data) {
            moviesList = data;
            gridViewMovies.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            moviesAdapter.addAll(data);
            loadNewData = true;
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

    private String constructURL (){
/*        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter("api_key", API_KEY)
                .build();*/
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendEncodedPath(Utilities.getPreferenceSortBy(this))
                .appendQueryParameter("api_key", API_KEY)
                .build();
        Log.i(LOG_TAG, uri.toString());
        return uri.toString();
    }
}
