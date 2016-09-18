package com.nanodegree.myapps.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by Deepesh_Gupta1 on 09/16/2016.
 */
public class Utilities {
    static String LOG_TAG = Utilities.class.getSimpleName();
    static String jsonString = null;
    private static String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w342";

    public static ArrayList<Movies> fetchJSONDataFromInternet(String url) {

        try {
            URL myUrl = new URL(url);

            HttpURLConnection httpURLConnection = (HttpURLConnection) myUrl.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(1000);
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            jsonString = readBufferedData(inputStream);

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return extractDataFromJson(jsonString);
    }

    public static String readBufferedData(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder jsonData = new StringBuilder();

        try {
            String line = bufferedReader.readLine();
            while (line != null) {
                jsonData = jsonData.append(line);
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonData.toString();
    }

    private static ArrayList<Movies> extractDataFromJson(String JsonString) {
        ArrayList<Movies> arrayList = new ArrayList<>();
        try {
            //listOfMovies.clear();
            JSONObject rootObj = new JSONObject(JsonString);
            JSONArray resultsArray = rootObj.getJSONArray("results");
            int i = 0;
            while (i < resultsArray.length()) {
                JSONObject dataObj = resultsArray.getJSONObject(i);

                String imagePath = dataObj.getString("poster_path");
                String movieDesc = dataObj.getString("overview");
                String relDate = dataObj.getString("release_date");
                String movieId = dataObj.getString("id");
                String movieTitle = dataObj.getString("original_title");
                String rating = dataObj.getString("vote_average");

                imagePath = IMAGE_BASE_URL + imagePath;
                //Log.i(LOG_TAG + "imagePath", imagePath);

                Log.i(LOG_TAG + " JSON ", "\n Poster: " + imagePath +
                        "\n Desc: " + movieDesc +
                        "\n Release Date: " + relDate +
                        "\n Movie ID: " + movieId +
                        "\n Title: " + movieTitle +
                        "\n Rating: " + rating +
                        "\n ---------------------");

                arrayList.add(new Movies(movieId, movieTitle, imagePath, rating, relDate, movieDesc ));
                i++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public static String getPreferenceSortBy (Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String preference = sharedPref.getString(context.getString(R.string.menu_sort_by_key),
                context.getString(R.string.pref_most_popular_value));
        return preference;
    }

}
