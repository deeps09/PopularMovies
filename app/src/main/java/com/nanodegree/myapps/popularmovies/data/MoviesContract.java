package com.nanodegree.myapps.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Deepesh_Gupta1 on 08/24/2016.
 */
public class MoviesContract {

    public final static String CONTENT_AUTHORITY = "com.nanodegree.myapps.popularmovies";

    public final static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public final static String PATH_MOVIES = "movies_table";

    public static final class MoviesEntry implements BaseColumns{

        public static final String TABLE_NAME = "movies_table";

        public final static Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIES);

        //Columns to save in table

        public static final String COLUMN_MOVIE_ID = "_id";

        public static final String COLUMN_MOVIE_TITLE = "title";

        public static final String COLUMN_MOVIE_POSTER = "poster";

        public static final String COLUMN_MOVIE_FAVORITE = "favorite";

        public static final String COLUMN_MOVIE_SYNOPSIS = "movie_desc";

        public static final String COLUMN_MOVIE_RELEASE_DATE = "release_date";

        public static final String COLUMN_MOVIE_RATING = "rating";

        public static final String COLUMN_MOVIE_TRAILER_URL = "trailer";

        public static final String COLUMN_MOVIE_REVIEWS = "reviews";

    }
}
