/*
 * Created by Stefan Sprenger
 */

package com.sprenger.software.movie.app.download;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.sprenger.software.movie.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;


public class FetchTrailerDataTask extends AsyncTask<String, Void, ArrayList<String>> {

    private final Context context;
    private final String LOG_TAG = FetchTrailerDataTask.class.getSimpleName();

    public FetchTrailerDataTask(Context context) {
        this.context = context;
    }

    @Override
    protected ArrayList<String> doInBackground(String... strings) {

        String movieId = strings[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String trailersJSONStr = null;

        try {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath(movieId)
                    .appendPath("videos")
                    .appendQueryParameter("api_key", context.getString(R.string.tmdb_api_key));

            URL url = new URL(builder.build().toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            trailersJSONStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getTrailerArrayFromJson(trailersJSONStr);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error get movie data from JSON", e);
            e.printStackTrace();
        }

        return null;

    }

    private ArrayList<String> getTrailerArrayFromJson(String forecastJsonStr)
            throws JSONException, ParseException {

        final String TRAILER_LIST = "results";
        final String YOUTUBEID = "key";
        final String VERIFICATIONSTRING = "site";

        JSONObject pageJSON = new JSONObject(forecastJsonStr);
        JSONArray movieArray = pageJSON.getJSONArray(TRAILER_LIST);

        ArrayList<String> trailerList = new ArrayList<>();
        for (int i = 0; i < movieArray.length(); i++) {

            JSONObject singleMovieJSON = movieArray.getJSONObject(i);

            String youtubeId = singleMovieJSON.getString(YOUTUBEID);
            String verification = singleMovieJSON.getString(VERIFICATIONSTRING);

            if(verification.equals("YouTube")){
                trailerList.add(youtubeId);
            }
        }

        return trailerList;

    }
}



