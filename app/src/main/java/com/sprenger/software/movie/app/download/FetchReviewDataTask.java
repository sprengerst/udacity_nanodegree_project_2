/*
 * Created by Stefan Sprenger
 */

package com.sprenger.software.movie.app.download;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.sprenger.software.movie.app.R;
import com.sprenger.software.movie.app.configuration.ReviewSpec;

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


public class FetchReviewDataTask extends AsyncTask<String, Void, ArrayList<ReviewSpec>> {

    private final Context context;
    private final String LOG_TAG = FetchReviewDataTask.class.getSimpleName();

    public FetchReviewDataTask(Context context) {
        this.context = context;
    }

    @Override
    protected ArrayList<ReviewSpec> doInBackground(String... strings) {

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
                    .appendPath("reviews")
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

    private ArrayList<ReviewSpec> getTrailerArrayFromJson(String forecastJsonStr)
            throws JSONException, ParseException {

        final String REVIEW_LIST = "results";
        final String AUTHOR = "author";
        final String CONTENT = "content";

        JSONObject pageJSON = new JSONObject(forecastJsonStr);
        JSONArray movieArray = pageJSON.getJSONArray(REVIEW_LIST);

        ArrayList<ReviewSpec> reviewList = new ArrayList<>();
        for (int i = 0; i < movieArray.length(); i++) {

            JSONObject singleMovieJSON = movieArray.getJSONObject(i);

            String author = singleMovieJSON.getString(AUTHOR);
            String content = singleMovieJSON.getString(CONTENT);

            reviewList.add(new ReviewSpec(author,content));
        }

        return reviewList;

    }
}



