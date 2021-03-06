package com.nanodegree.myapps.popularmovies;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.nanodegree.myapps.popularmovies.data.MoviesContract;
import com.nanodegree.myapps.popularmovies.data.MoviesContract.MoviesEntry;


public class FavouritesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    final static int LOADER_ID = 1;

    public static byte[] posterBytes;
    ProgressBar progressBar;
    GridView moviesList;
    MoviesCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        this.setTitle("Favorite Movies");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        moviesList = (GridView) findViewById(R.id.movies_gridview);
        cursorAdapter = new MoviesCursorAdapter(this, null);
        moviesList.setAdapter(cursorAdapter);

        moviesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Uri uri = Uri.parse(MoviesEntry.CONTENT_URI + "/" + id);
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();
                Movies movies = new Movies(
                        cursor.getString(cursor.getColumnIndex(MoviesEntry.COLUMN_MOVIE_ID)),
                        cursor.getString(cursor.getColumnIndex(MoviesEntry.COLUMN_MOVIE_TITLE)),
                        null,
                        cursor.getString(cursor.getColumnIndex(MoviesEntry.COLUMN_MOVIE_RATING)),
                        cursor.getString(cursor.getColumnIndex(MoviesEntry.COLUMN_MOVIE_RELEASE_DATE)),
                        cursor.getString(cursor.getColumnIndex(MoviesEntry.COLUMN_MOVIE_SYNOPSIS))
                );

                posterBytes  = cursor.getBlob(cursor.getColumnIndex(MoviesEntry.COLUMN_MOVIE_POSTER));

                Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                intent.putExtra("bundle", movies);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                MoviesEntry.COLUMN_MOVIE_ID,
                MoviesEntry.COLUMN_MOVIE_TITLE,
                MoviesEntry.COLUMN_MOVIE_RELEASE_DATE,
                MoviesEntry.COLUMN_MOVIE_RATING,
                MoviesEntry.COLUMN_MOVIE_POSTER
        };

        return new CursorLoader(this, 
                MoviesContract.MoviesEntry.CONTENT_URI, 
                projection,
                null, 
                null, 
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            NavUtils.navigateUpTo(this, upIntent);
        }

       return true;
    }
}
