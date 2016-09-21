package com.nanodegree.myapps.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    final static String BASE_URL = "https://api.themoviedb.org/3/movie";
    final static String YOUTUBE_BASE_URL = "https://www.youtube.com/watch";

    ImageView detailPoster, playArrow;
    //String videoKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        TextView rating_text = (TextView) findViewById(R.id.ratings_tv);
        TextView relDate_text = (TextView) findViewById(R.id.release_date_tv);
        TextView synopsis_text = (TextView) findViewById(R.id.synopsis_tv);

        Intent intent = getIntent();
        this.setTitle(intent.getStringExtra(MainActivity.KEY_MOVIE_TITLE));

        String mPosterUrl = intent.getStringExtra(MainActivity.KEY_MOVIE_IMAGE_URL);
        String mMovieId = intent.getStringExtra(MainActivity.KEY_MOVIE_ID);
        String mSynopsis = intent.getStringExtra(MainActivity.KEY_MOVIE_SYNOPSIS);
        String mReleaseDate = intent.getStringExtra(MainActivity.KEY_MOVIE_REL_DATE);
        String mRating = intent.getStringExtra(MainActivity.KEY_MOVIE_RATING);

        rating_text.setText(mRating);
        relDate_text.setText(mReleaseDate);
        synopsis_text.setText(mSynopsis);
        detailPoster = (ImageView) findViewById(R.id.detailMoviePoster);
        playArrow = (ImageView) findViewById(R.id.playArrow);

        Picasso.with(this).load(mPosterUrl).fit().into(detailPoster);
        playArrow.setVisibility(View.VISIBLE);

        Uri reviewUri = Uri.parse(BASE_URL).buildUpon()
                .appendEncodedPath(mMovieId)
                .appendEncodedPath("reviews")
                .appendQueryParameter("api_key", MainActivity.API_KEY)
                .build();

        Uri videoUri = Uri.parse(BASE_URL).buildUpon()
                .appendEncodedPath(mMovieId)
                .appendEncodedPath("videos")
                .appendQueryParameter("api_key", MainActivity.API_KEY)
                .build();

        backgroundTask bgTask = new backgroundTask();
        bgTask.execute(reviewUri.toString(), videoUri.toString());
    }


    @Override
    public boolean onSupportNavigateUp() {
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        NavUtils.navigateUpTo(this, upIntent);

        return true;
    }

    private class backgroundTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            return Utilities.extractVideoKeyFromJSON(Utilities.fetchJSONDataFromInternet(params[1]));

            // As user reviews is out of scope for this project hence commented out
            //return Utilities.extractReviewsFromJSON(Utilities.fetchJSONDataFromInternet(params[0]));
        }

        @Override
        protected void onPostExecute(String videoKey) {
            final Uri youtubeUri = Uri.parse(YOUTUBE_BASE_URL).buildUpon().appendQueryParameter("v", videoKey).build();

            playArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, youtubeUri);
                    startActivity(youtubeIntent);
                }
            });
        }
    }
}
