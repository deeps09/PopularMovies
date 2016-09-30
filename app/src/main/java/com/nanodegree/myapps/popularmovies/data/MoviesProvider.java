package com.nanodegree.myapps.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.nanodegree.myapps.popularmovies.data.MoviesContract.MoviesEntry;

/**
 * Created by Deepesh_Gupta1 on 09/26/2016.
 */
public class MoviesProvider extends ContentProvider {



    final static int MOVIES = 100;
    final static int MOVIES_ID = 101;
    private MoviesDBHelper mMoviesDBHelper;

    final static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIES, MOVIES);

        sUriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIES + "/#", MOVIES_ID);
    }


    @Override
    public boolean onCreate() {
        mMoviesDBHelper = new MoviesDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = mMoviesDBHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor cursor = null;

        switch (match) {
            case MOVIES:
                cursor = db.query(MoviesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIES_ID:
                selection = MoviesEntry.COLUMN_MOVIE_ID + " = ?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(MoviesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = mMoviesDBHelper.getWritableDatabase();
        long id;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                id = db.insert(MoviesEntry.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("No insertion supported for URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mMoviesDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int row = -1;

        switch (match) {
            case MOVIES:
                row = db.delete(MoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIES_ID:
                selection = MoviesEntry.COLUMN_MOVIE_ID + " = ?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                row = db.delete(MoviesEntry.TABLE_NAME, selection, selectionArgs);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return row;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        getContext().getContentResolver().notifyChange(uri, null);
        return 0;
    }
}
