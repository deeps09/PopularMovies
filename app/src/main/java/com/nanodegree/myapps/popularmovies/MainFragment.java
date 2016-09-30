package com.nanodegree.myapps.popularmovies;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    static String LOG_TAG = MainFragment.class.getSimpleName();
    int page = 1;
    static final String BASE_URL = "https://api.themoviedb.org/3/movie";
    static final String API_KEY = "69b589af19cead810bc805ab8f5363f6";
    Boolean loadNewData = false;
    //ProgressBar progressBar;
    GridView gridViewMovies;
    MoviesArrayAdapter moviesAdapter;
    ArrayList<Movies> moviesList;
    String lastPreference = null;
    backgroundTask bgTask;
    private SwipeRefreshLayout swipeContainer;

    public static Movies movies = null;

    final static String KEY_MOVIE_ID = "movie_id";
    final static String KEY_MOVIE_TITLE = "movie_title";
    final static String KEY_MOVIE_REL_DATE = "rel_date";
    final static String KEY_MOVIE_RATING = "rating";
    final static String KEY_MOVIE_SYNOPSIS = "synopsis";
    final static String KEY_MOVIE_IMAGE_URL = "poster_url";


    public MainFragment() {
        // Required empty public constructor
    }

    public interface Callback {

        public void onItemSelected(Movies movie);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        lastPreference = Utilities.getPreferenceSortBy(getActivity());
        //progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        gridViewMovies = (GridView) rootView.findViewById(R.id.movies_gridview);
        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMovies();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            Log.i(LOG_TAG, "No Parcelable data available");
            moviesList = new ArrayList<Movies>();

            // Checking internet connection
            if (Utilities.checkInternetAccess(getActivity())) {
                loadMovies();
            } else {
                Toast.makeText(getActivity(), "No Internet Connection Available", Toast.LENGTH_SHORT).show();
                swipeContainer.setRefreshing(false);
                //progressBar.setVisibility(View.INVISIBLE);
            }
        } else {
            Log.i(LOG_TAG, "Found Parcelable data");
            moviesList = savedInstanceState.getParcelableArrayList("movies");
            //progressBar.setVisibility(View.GONE);
            gridViewMovies.setVisibility(View.VISIBLE);
            page = savedInstanceState.getInt("page"); // loading current page number on screen rotate

            if (Utilities.checkInternetAccess(getActivity()))
                loadMovies();
            else
                Toast.makeText(getActivity(), "No Internet Connection Available to load more movies", Toast.LENGTH_SHORT).show();
        }

        // loading new movies
        gridViewMovies.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (loadNewData == true && firstVisibleItem == totalItemCount - 8) {
                    if (Utilities.checkInternetAccess(getActivity())) {
                        //progressBar.setVisibility(View.VISIBLE);
                        showProgressIndicator();
                        loadMovies();
                        loadNewData = false;
                    } else {
                        Toast.makeText(getActivity(), "No Internet Connection Available", Toast.LENGTH_SHORT).show();
                        //progressBar.setVisibility(View.INVISIBLE);
                        swipeContainer.setRefreshing(false);
                        loadNewData = false;
                    }
                }
            }
        });

        gridViewMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (Utilities.checkInternetAccess(getActivity())) {

                    movies = moviesAdapter.getItem(position);
                    ((Callback) getActivity()).onItemSelected(movies);

                } else {
                    Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

               /* Intent intent = new Intent(getActivity(), DetailScrollingActivity.class);
                startActivity(intent);*/
            }
        });

        moviesAdapter = new MoviesArrayAdapter(getActivity(), moviesList);
        gridViewMovies.setAdapter(moviesAdapter);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.menuitem_sortBy:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName());
                intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true); // To not show headers
                startActivity(intent);
                break;
            case R.id.menuitem_favorite:
                Intent intent1 = new Intent(getActivity(), FavouritesActivity.class);
                startActivity(intent1);
                break;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", moviesList);
        outState.putInt("page", page);
        super.onSaveInstanceState(outState);
        Log.i(LOG_TAG, "onSaveInstanceState()");
    }

    @Override
    public void onResume() {
        Log.i(LOG_TAG, "onResume()");

        // Is preference changed
        if (!lastPreference.equals(Utilities.getPreferenceSortBy(getActivity()))) {
            getActivity().finish();
            startActivity(new Intent(getActivity(), MainActivity.class));
            Log.i(LOG_TAG, "onRestart() called from onResume()");
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i(LOG_TAG, "onPause() ");
        lastPreference = Utilities.getPreferenceSortBy(getActivity());
        super.onPause();
    }

    private class backgroundTask extends AsyncTask<String, Void, ArrayList<Movies>> {
        @Override
        protected ArrayList<Movies> doInBackground(String... params) {

            if (!TextUtils.isEmpty(params[0])) {
                String istream = Utilities.fetchJSONDataFromInternet(params[0]);
                if (istream != null)
                    return Utilities.extractMovieInfoFromJSON(istream);
                return null;
            } else
                return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movies> data) {

            swipeContainer.setRefreshing(false);
            if (data.size() == 0) {
                Toast.makeText(getActivity(), "Something went wrong. Check internet connectivity", Toast.LENGTH_SHORT).show();
                //progressBar.setVisibility(View.GONE);
                return;
            }

            //moviesList = data;
            Log.i(LOG_TAG, "onPostExecute() ");
            gridViewMovies.setVisibility(View.VISIBLE);
            //progressBar.setVisibility(View.GONE);
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
                .appendEncodedPath(Utilities.getPreferenceSortBy(getActivity()))
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("page", String.valueOf(page))
                .build();
        page++;
        Log.i(LOG_TAG + "WEBURL", uri.toString());
        return uri.toString();
    }

    private void loadMovies() {
        //progressBar.setVisibility(View.VISIBLE);
        showProgressIndicator();
        bgTask = new backgroundTask();
        bgTask.execute(constructURL());
    }

    private void showProgressIndicator(){
        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(true);
            }
        });
    }

}
