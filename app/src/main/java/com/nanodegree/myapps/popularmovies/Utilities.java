package com.nanodegree.myapps.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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

    public static String fetchJSONDataFromInternet(String url) {
        InputStream inputStream = null;
        try {
            URL myUrl = new URL(url);

            HttpURLConnection httpURLConnection = (HttpURLConnection) myUrl.openConnection();
            httpURLConnection.setRequestMethod("GET");
            //vhttpURLConnection.setConnectTimeout(1000);
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();


        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return readBufferedData(inputStream);
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

    // To extract movie details from JSON
    public static ArrayList<Movies> extractMovieInfoFromJSON(String JsonString) {
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

                arrayList.add(new Movies(movieId, movieTitle, imagePath, rating, relDate, movieDesc));
                i++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    // To extract reviews from JSON
    public static ArrayList<String> extractReviewsFromJSON(String JsonString) {
        //JsonString = "https://api.themoviedb.org/3/movie/244786/reviews?api_key=69b589af19cead810bc805ab8f5363f6";
        ArrayList<String> reviews = new ArrayList<String>();
        Log.i(LOG_TAG," JSON Reviews: " + JsonString);

        try {
            JSONObject rootObj = new JSONObject(JsonString);
            JSONArray resultsArray = rootObj.getJSONArray("results");

            int i = 0;

            //if (Flag == FLAG_REVIEW) {
            reviews.clear();
            while (i < resultsArray.length()) {
                JSONObject dataObj = resultsArray.getJSONObject(i);
                reviews.add(dataObj.getString("content"));
                Log.v(LOG_TAG + " Review ", reviews.get(i));
                i++;
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, " JSONException Occurred ", e);
            //videoKey = null;
        }
        return reviews;
    }

    // To extract VideoKey from JSON
    public static String extractVideoKeyFromJSON(String JsonString) {
        //JsonString = "https://api.themoviedb.org/3/movie/244786/reviews?api_key=69b589af19cead810bc805ab8f5363f6";
        String videoKey;

        try {
            JSONObject rootObj = new JSONObject(JsonString);
            JSONArray resultsArray = rootObj.getJSONArray("results");

            JSONObject dataObj = resultsArray.getJSONObject(0);
            videoKey = dataObj.getString("key");
            Log.v(LOG_TAG + " Video ", videoKey);

        } catch (JSONException e) {
            Log.e(LOG_TAG, " JSONException Occurred ", e);
            videoKey = null;
        }
        return videoKey;
    }

    public static String getPreferenceSortBy(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String preference = sharedPref.getString(context.getString(R.string.menu_sort_by_key),
                context.getString(R.string.pref_most_popular_value));
        return preference;
    }

    public static Boolean checkInternetAccess(Context context) {
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /*
    * Convert reviews Array List into JSON string to insert in DB
    * */
    public static String convertReviewsToJson(ArrayList<String> myArrayList) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("json", new JSONArray(myArrayList.toArray()));
        } catch (JSONException e) {
            Log.v(LOG_TAG, "JSONException occurred in convertReviewsToJson", e);
        }

        String arrayList = jsonObject.toString();

        return arrayList;
    }

    /*
    * @param: urlPath Url of image to be downloaded
    * Download image from internet to show it on Details view and saving it in DB
    * */

    public static byte[] DownloadImageFromInternet(String urlPath) {
        URL url = null;
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        Bitmap bitmap = null;
        byte[] imageByte = null;

        try {
            url = new URL(urlPath);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            imageByte = os.toByteArray();

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, " MalformedURLException ", e);
        } catch (IOException e) {
            Log.e(LOG_TAG, " IOException ", e);
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
            } catch (IOException e) {
                Log.e(LOG_TAG, " IOException ", e);
            }

        }
        return imageByte;
    }


}
