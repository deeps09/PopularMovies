package com.nanodegree.myapps.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Deepesh_Gupta1 on 09/16/2016.
 */
public class MoviesArrayAdapter extends ArrayAdapter<Movies> {
    Context mContext = null;

    public MoviesArrayAdapter(Context context) {
        super(context, 0);
        this.mContext = context;
    }

    public MoviesArrayAdapter(Context context, ArrayList<Movies> objects) {
        super(context, 0, objects);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        }

        Movies movies = getItem(position);

        TextView title = (TextView) view.findViewById(R.id.movie_title_tv);
        title.setText(movies.getMovieTitle());

        TextView releaseDate = (TextView) view.findViewById(R.id.release_date_tv);
        releaseDate.setText(movies.getReleaseDate());

        TextView rating = (TextView) view.findViewById(R.id.ratings_tv);
        rating.setText(movies.getUserRating());

        ImageView imageView = (ImageView) view.findViewById(R.id.image_poster);
        Picasso.with(mContext).load(movies.getPosterUrl()).into(imageView);

        return view;
    }


}
