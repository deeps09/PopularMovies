package com.nanodegree.myapps.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nanodegree.myapps.popularmovies.data.MoviesContract.MoviesEntry;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {

    final static String LOG_TAG = DetailsActivity.class.getSimpleName();
    final static String BASE_URL = "https://api.themoviedb.org/3/movie";
    final static String YOUTUBE_BASE_URL = "https://www.youtube.com/watch";
    final static String REVIEWS_KEY = "reviews";

    // To show favorites icon properly
    boolean fav = false;

    Button reviews_btn;
    ImageView moviePoster, playArrow;
    String videoKey = null;
    ArrayList<String> listOfReviews;
    ProgressBar imageLoad;

    String mPosterUrl;
    String mMovieId;
    String mSynopsis;
    String mReleaseDate;
    String mRating;
    String mMovieTitle;
    byte[] mPosterImageInByte;

    Uri mVideoUri;
    Uri mUriMovieId;

    View.OnClickListener reviewsOnClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        TextView rating_text = (TextView) findViewById(R.id.ratings_tv);
        TextView relDate_text = (TextView) findViewById(R.id.release_date_tv);
        TextView synopsis_text = (TextView) findViewById(R.id.synopsis_tv);
        reviews_btn = (Button) findViewById(R.id.reviews_btn);
        imageLoad = (ProgressBar) findViewById(R.id.progressBar_image_load);

        Intent intent = getIntent();

        mPosterUrl = intent.getStringExtra(MainActivity.KEY_MOVIE_IMAGE_URL);
        mMovieId = intent.getStringExtra(MainActivity.KEY_MOVIE_ID);
        mMovieTitle = intent.getStringExtra(MainActivity.KEY_MOVIE_TITLE);
        mSynopsis = intent.getStringExtra(MainActivity.KEY_MOVIE_SYNOPSIS);
        mReleaseDate = intent.getStringExtra(MainActivity.KEY_MOVIE_REL_DATE);
        mRating = intent.getStringExtra(MainActivity.KEY_MOVIE_RATING);

        this.setTitle(mMovieTitle);

        mUriMovieId = Uri.parse(MoviesEntry.CONTENT_URI + "/" + mMovieId);

        rating_text.setText(mRating);
        relDate_text.setText(mReleaseDate);
        synopsis_text.setText(mSynopsis);
        moviePoster = (ImageView) findViewById(R.id.detailMoviePoster);
        playArrow = (ImageView) findViewById(R.id.playArrow);

        //Picasso.with(this).load(mPosterUrl).fit().into(moviePoster);


        // Out of scope for this stage
        Uri reviewUri = Uri.parse(BASE_URL).buildUpon()
                .appendEncodedPath(mMovieId)
                .appendEncodedPath("reviews")
                .appendQueryParameter("api_key", MainActivity.API_KEY)
                .build();

        mVideoUri = Uri.parse(BASE_URL).buildUpon()
                .appendEncodedPath(mMovieId)
                .appendEncodedPath("videos")
                .appendQueryParameter("api_key", MainActivity.API_KEY)
                .build();

        backgroundTask bgTask = new backgroundTask();

        if (Utilities.checkInternetAccess(this))
            bgTask.execute(mPosterUrl, reviewUri.toString(), mVideoUri.toString());
        else
            Toast.makeText(getApplicationContext(), "No Internet Connection Available", Toast.LENGTH_SHORT).show();

/*        reviewsOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReviewsActivity.class);
                intent.putStringArrayListExtra(REVIEWS_KEY, listOfReviews);
                startActivity(intent);
            }
        };*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        Cursor cursor = getContentResolver().query(mUriMovieId,
                null,
                null,
                null,
                null);

        if (cursor.getCount() == 1) {
            MenuItem fav_item = menu.findItem(R.id.fav_menu);
            fav_item.setIcon(R.drawable.ic_favorite);
            fav = true;
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.fav_menu:
                if (fav) {
                    // When favorite icon changes from red to white
                    item.setIcon(R.drawable.ic_non_favorite);
                    deleteMovie();
                    fav = false;
                } else {
                    // When favorite icon changes from white to red
                    item.setIcon(R.drawable.ic_favorite);
                    insertMovie();
                    fav = true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertMovie() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesEntry.COLUMN_MOVIE_ID, mMovieId);
        contentValues.put(MoviesEntry.COLUMN_MOVIE_TITLE, mMovieTitle);
        contentValues.put(MoviesEntry.COLUMN_MOVIE_RELEASE_DATE, mReleaseDate);
        contentValues.put(MoviesEntry.COLUMN_MOVIE_RATING, mRating);
        contentValues.put(MoviesEntry.COLUMN_MOVIE_FAVORITE, "Y");
        contentValues.put(MoviesEntry.COLUMN_MOVIE_POSTER, mPosterImageInByte);
        contentValues.put(MoviesEntry.COLUMN_MOVIE_SYNOPSIS, mSynopsis);
        contentValues.put(MoviesEntry.COLUMN_MOVIE_TRAILER_URL, mVideoUri.toString());
        contentValues.put(MoviesEntry.COLUMN_MOVIE_REVIEWS, Utilities.convertReviewsToJson(listOfReviews));

        Uri uri = getContentResolver().insert(MoviesEntry.CONTENT_URI, contentValues);
        Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
    }

    private void deleteMovie() {
        Uri uri = Uri.parse(MoviesEntry.CONTENT_URI + "/" + mMovieId);
        int row = getContentResolver().delete(uri, null, null);
        Toast.makeText(this, row + "Movie removed from favorites", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        NavUtils.navigateUpTo(this, upIntent);

        return true;
    }

    private class backgroundTask extends AsyncTask<String, Void, Bundle> {
        @Override
        protected Bundle doInBackground(String... params) {

            Bundle bundle = new Bundle();

            byte[] imageBytes = Utilities.DownloadImageFromInternet(params[0]);

            ArrayList<String> reviews;
            reviews = Utilities.extractReviewsFromJSON(Utilities.fetchJSONDataFromInternet(params[1]));

            videoKey = Utilities.extractVideoKeyFromJSON(Utilities.fetchJSONDataFromInternet(params[2]));

            bundle.putByteArray("poster", imageBytes);
            bundle.putStringArrayList("reviews", reviews);
            bundle.putString("trailer", videoKey);

            return bundle;
        }

        @Override
        protected void onPostExecute(final Bundle bundle) {
            final Uri youtubeUri = Uri.parse(YOUTUBE_BASE_URL).buildUpon().
                    appendQueryParameter("v", bundle.getString("trailer")).build();

            playArrow.setVisibility(View.VISIBLE);
            imageLoad.setVisibility(View.GONE);
            // Make this variable ready to save in DB if user favourites the movie
            listOfReviews = bundle.getStringArrayList("reviews");

            //Log.i(LOG_TAG," Reviews = " + reviews.get(0).toString());

            /*
            * Setting imageView for showing movie poster and assign the variable mPosterImageInByte to save in DB
            * in case user favorites the movie
            * */

            byte[] bytes = bundle.getByteArray("poster");
            mPosterImageInByte = bytes;

            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            moviePoster.setImageBitmap(bitmap);



            /*
            * Onclick event on play button to watch trailer on youtube
            * */
            playArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utilities.checkInternetAccess(getApplicationContext())) {
                        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, youtubeUri);
                        startActivity(youtubeIntent);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            /*
            * Onclick event on button to view reviews
            * */
            reviews_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utilities.checkInternetAccess(getApplicationContext())) {
                        Intent intent = new Intent(getApplicationContext(), ReviewsActivity.class);
                        intent.putStringArrayListExtra(REVIEWS_KEY, bundle.getStringArrayList("reviews"));
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
