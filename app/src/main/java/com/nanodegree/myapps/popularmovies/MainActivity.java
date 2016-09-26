package com.nanodegree.myapps.popularmovies;

import android.content.Intent;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static String LOG_TAG = MainActivity.class.getSimpleName();
    int page = 1;
    static final String BASE_URL = "https://api.themoviedb.org/3/movie";
    static final String API_KEY = "69b589af19cead810bc805ab8f5363f6";
    Boolean loadNewData = false;
    ProgressBar progressBar;
    GridView gridViewMovies;
    MoviesArrayAdapter moviesAdapter;
    ArrayList<Movies> moviesList;
    String lastPreference = null;
    backgroundTask bgTask;

    final static String KEY_MOVIE_ID = "movie_id";
    final static String KEY_MOVIE_TITLE = "movie_title";
    final static String KEY_MOVIE_REL_DATE = "rel_date";
    final static String KEY_MOVIE_RATING = "rating";
    final static String KEY_MOVIE_SYNOPSIS = "synopsis";
    final static String KEY_MOVIE_IMAGE_URL = "poster_url";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lastPreference = Utilities.getPreferenceSortBy(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        gridViewMovies = (GridView) findViewById(R.id.movies_gridview);

        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            Log.i(LOG_TAG, "No Parcelable data available");
            moviesList = new ArrayList<Movies>();

            // Checking internet connection
            if (Utilities.checkInternetAccess(this)) {
                loadMovies();
            } else {
                Toast.makeText(this, "No Internet Connection Available", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        } else {
            Log.i(LOG_TAG, "Found Parcelable data");
            moviesList = savedInstanceState.getParcelableArrayList("movies");
            progressBar.setVisibility(View.GONE);
            gridViewMovies.setVisibility(View.VISIBLE);
            page = savedInstanceState.getInt("page"); // loading current page number on screen rotate
            loadMovies();
        }

        // loading new movies
        gridViewMovies.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                //Log.i(LOG_TAG, "first Item: " + firstVisibleItem + " Visible Item " + visibleItemCount + " Total Item " +
                //totalItemCount);
                if (loadNewData == true && firstVisibleItem == totalItemCount - 8) {
                    if (Utilities.checkInternetAccess(getApplicationContext())) {
                        progressBar.setVisibility(View.VISIBLE);
                        loadMovies();
                        loadNewData = false;
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection Available", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        loadNewData = false;
                    }
                }
            }
        });

        gridViewMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Movies movies = moviesAdapter.getItem(position);

                Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                intent.putExtra(KEY_MOVIE_ID, movies.getMovieId());
                intent.putExtra(KEY_MOVIE_TITLE, movies.getMovieTitle());
                intent.putExtra(KEY_MOVIE_REL_DATE, movies.getReleaseDate());
                intent.putExtra(KEY_MOVIE_RATING, movies.getUserRating());
                intent.putExtra(KEY_MOVIE_SYNOPSIS, movies.getMovieDesc());
                intent.putExtra(KEY_MOVIE_IMAGE_URL, movies.getPosterUrl());
                startActivity(intent);


                //Toast.makeText(getApplicationContext(), "Movie ID: " + selectedMovieId + " " + movies.getMovieTitle(), Toast.LENGTH_SHORT).show();
            }
        });

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

        if (id == R.id.menuitem_sortBy) {
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
        outState.putInt("page", page);
        super.onSaveInstanceState(outState);
        Log.i(LOG_TAG, "onSaveInstanceState()");
    }

    @Override
    protected void onResume() {
        Log.i(LOG_TAG, "onResume()");

        // Is preference changed
        if (!lastPreference.equals(Utilities.getPreferenceSortBy(this))) {
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
            return Utilities.extractMovieInfoFromJSON(Utilities.fetchJSONDataFromInternet(params[0]));
        }

        @Override
        protected void onPostExecute(ArrayList<Movies> data) {
            //moviesList = data;
            Log.i(LOG_TAG, "onPostExecute() ");
            gridViewMovies.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            moviesAdapter.addAll(data);
            loadNewData = true;
            this.onCancelled();
            loadNewData = true;
            //Log.i(LOG_TAG, jsonString);
        }

        @Override
        protected void onCancelled() {
            if (bgTask != null)
                this.cancel(true);
            bgTask = null;
        }
    }

    private String constructURL() {
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendEncodedPath(Utilities.getPreferenceSortBy(this))
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("page", String.valueOf(page))
                .build();
        page++;
        Log.i(LOG_TAG + "WEBURL", uri.toString());
        return uri.toString();
    }

    private void loadMovies() {
        progressBar.setVisibility(View.VISIBLE);
        bgTask = new backgroundTask();
        bgTask.execute(constructURL());
    }
}
