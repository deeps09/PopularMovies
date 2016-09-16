package com.nanodegree.myapps.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Deepesh_Gupta1 on 09/16/2016.
 */
public class Movies implements Parcelable {

    String mMovieId;
    String mMovieTitle;
    String mPosterUrl;
    String mUserRating;
    String mReleaseDate;
    String mMovieDesc;

    protected Movies(Parcel in) {
        mMovieId = in.readString();
        mMovieTitle = in.readString();
        mPosterUrl = in.readString();
        mUserRating = in.readString();
        mReleaseDate = in.readString();
        mMovieDesc = in.readString();
    }

    public Movies(String movieId, String movieTitle, String posterUrl, String userRating, String releaseDate, String movieDesc) {
        this.mMovieId = movieId;
        this.mMovieTitle = movieTitle;
        this.mPosterUrl = posterUrl;
        this.mUserRating = userRating;
        this.mReleaseDate = releaseDate;
        this.mMovieDesc = movieDesc;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMovieId);
        dest.writeString(mMovieTitle);
        dest.writeString(mPosterUrl);
        dest.writeString(mUserRating);
        dest.writeString(mReleaseDate);
        dest.writeString(mMovieDesc);
    }

    public static Parcelable.Creator<Movies> CREATOR = new Parcelable.Creator<Movies>() {
        @Override
        public Movies createFromParcel(Parcel source) {
            return new Movies(source);
        }

        @Override
        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };

    public String getMovieId() {
        return mMovieId;
    }

    public String getMovieTitle() {
        return mMovieTitle;
    }

    public String getPosterUrl() {
        return mPosterUrl;
    }

    public String getUserRating() {
        return mUserRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getMovieDesc() {
        return mMovieDesc;
    }
}
