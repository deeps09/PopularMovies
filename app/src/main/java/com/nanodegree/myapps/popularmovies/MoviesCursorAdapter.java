package com.nanodegree.myapps.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nanodegree.myapps.popularmovies.data.MoviesContract.MoviesEntry;

/**
 * Created by Deepesh_Gupta1 on 09/27/2016.
 */
public class MoviesCursorAdapter extends CursorAdapter {

    public MoviesCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
       return LayoutInflater.from(context).inflate(R.layout.list_item, null);
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {

        TextView title = (TextView) view.findViewById(R.id.movie_title_tv);
        title.setText(cursor.getString(cursor.getColumnIndex(MoviesEntry.COLUMN_MOVIE_TITLE)));

        TextView releaseDate = (TextView) view.findViewById(R.id.release_date_tv);
        releaseDate.setText(cursor.getString(cursor.getColumnIndex(MoviesEntry.COLUMN_MOVIE_RELEASE_DATE)));

        TextView rating = (TextView) view.findViewById(R.id.ratings_tv);
        rating.setText(cursor.getString(cursor.getColumnIndex(MoviesEntry.COLUMN_MOVIE_RATING)));

        byte[] bytes = cursor.getBlob(cursor.getColumnIndex(MoviesEntry.COLUMN_MOVIE_POSTER));
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

        ImageView imageView = (ImageView) view.findViewById(R.id.image_poster);
        imageView.setImageBitmap(bitmap);
    }
}
