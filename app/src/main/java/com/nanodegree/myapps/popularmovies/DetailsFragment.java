package com.nanodegree.myapps.popularmovies;


import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nanodegree.myapps.popularmovies.data.MoviesContract;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    final static String LOG_TAG = DetailsActivity.class.getSimpleName();
    final static String BASE_URL = "https://api.themoviedb.org/3/movie";
    final static String YOUTUBE_BASE_URL = "https://www.youtube.com/watch";
    final static String REVIEWS_KEY = "reviews";

    // To show favorites icon properly
    boolean fav = false;

    Button reviews_btn;
    ImageView moviePoster, playArrow;
    String videoKey = null;
    static ArrayList<String> listOfReviews;
    ProgressBar imageLoad;
    TextView rating_text;
    TextView relDate_text;
    TextView synopsis_text;
    RelativeLayout parentView;

    static String mPosterUrl;
    static String mMovieId;
    static String mSynopsis;
    static String mReleaseDate;
    static String mRating;
    static String mMovieTitle;
    static byte[] mPosterImageInByte;

    static Uri mVideoUri, mReviewUri;
    Uri mUriMovieId;

    public DetailsFragment() {
        // Required empty public constructor
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
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        parentView = (RelativeLayout) rootView.findViewById(R.id.details_parent);
        rating_text = (TextView) rootView.findViewById(R.id.ratings_tv);
        relDate_text = (TextView) rootView.findViewById(R.id.release_date_tv);
        synopsis_text = (TextView) rootView.findViewById(R.id.synopsis_tv);
        reviews_btn = (Button) rootView.findViewById(R.id.reviews_btn);
        imageLoad = (ProgressBar) rootView.findViewById(R.id.progressBar_image_load);
        moviePoster = (ImageView) rootView.findViewById(R.id.detailMoviePoster);
        playArrow = (ImageView) rootView.findViewById(R.id.playArrow);


        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();
        Movies movies = intent.getParcelableExtra("bundle");

        if (movies == null) {
            Bundle arguments = getArguments();
            if (arguments == null)
                movies = MainFragment.movies;
            else
                movies = arguments.getParcelable("bundle");
        }

        if (movies != null) {

            parentView.setVisibility(View.VISIBLE);
            mPosterUrl = movies.getPosterUrl();
            mMovieId = movies.getMovieId();
            mMovieTitle = movies.getMovieTitle();
            mSynopsis = movies.getMovieDesc();
            mReleaseDate = movies.getReleaseDate();
            mRating = movies.getUserRating();

            //mMovieId = "13042";

            getActivity().setTitle(mMovieTitle);

            mUriMovieId = Uri.parse(MoviesContract.MoviesEntry.CONTENT_URI + "/" + mMovieId);

            rating_text.setText(mRating);
            relDate_text.setText(mReleaseDate);
            synopsis_text.setText(mSynopsis);

            mReviewUri = Uri.parse(BASE_URL).buildUpon()
                    .appendEncodedPath(mMovieId)
                    .appendEncodedPath("reviews")
                    .appendQueryParameter("api_key", MainFragment.API_KEY)
                    .build();

            mVideoUri = Uri.parse(BASE_URL).buildUpon()
                    .appendEncodedPath(mMovieId)
                    .appendEncodedPath("videos")
                    .appendQueryParameter("api_key", MainFragment.API_KEY)
                    .build();

            backgroundTask bgTask = new backgroundTask();

            if (Utilities.checkInternetAccess(getActivity()))
                bgTask.execute(mPosterUrl, mReviewUri.toString(), mVideoUri.toString());
            else
                Toast.makeText(getActivity(), "No Internet Connection Available", Toast.LENGTH_SHORT).show();
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);

        Cursor cursor = getActivity().getContentResolver().query(mUriMovieId,
                null,
                null,
                null,
                null);

        // Check if movie already exists in database
        if (cursor != null && cursor.getCount() == 1) {
            MenuItem fav_item = menu.findItem(R.id.fav_menu);
            fav_item.setIcon(R.drawable.ic_favorite);
            fav = true;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //Toast.makeText(getActivity(), "Heart Touched", Toast.LENGTH_SHORT).show();

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
                break;
        }
        return false;
    }





    public void insertMovie() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, mMovieId);
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE, mMovieTitle);
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_RELEASE_DATE, mReleaseDate);
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_RATING, mRating);
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_FAVORITE, "Y");
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER, mPosterImageInByte);
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_SYNOPSIS, mSynopsis);
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_TRAILER_URL, mVideoUri.toString());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_REVIEWS, Utilities.convertReviewsToJson(listOfReviews));

        Uri uri = getActivity().getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, contentValues);
        Toast.makeText(getActivity(), uri.toString(), Toast.LENGTH_SHORT).show();
    }

    public void deleteMovie() {
        Uri uri = Uri.parse(MoviesContract.MoviesEntry.CONTENT_URI + "/" + mMovieId);
        int row = getActivity().getContentResolver().delete(uri, null, null);
        Toast.makeText(getActivity(), row + "Movie removed from favorites", Toast.LENGTH_SHORT).show();
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
                    if (Utilities.checkInternetAccess(getActivity())) {
                        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, youtubeUri);
                        startActivity(youtubeIntent);
                    } else {
                        Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            /*
            * Onclick event on button to view reviews
            * */
            reviews_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utilities.checkInternetAccess(getActivity())) {
                        Intent intent = new Intent(getActivity(), ReviewsActivity.class);
                        intent.putStringArrayListExtra(REVIEWS_KEY, bundle.getStringArrayList("reviews"));
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}
