package com.nanodegree.myapps.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

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

        if (savedInstanceState == null){
            getFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, new DetailsFragment()).commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        NavUtils.navigateUpTo(this, upIntent);

        return true;
    }
}

