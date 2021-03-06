/*
 * Created by Stefan Sprenger
 */

package com.sprenger.software.movie.app.download;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.sprenger.software.movie.app.MovieDiscoveryFragment;
import com.sprenger.software.movie.app.R;
import com.sprenger.software.movie.app.database.MovieContract;

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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;


public class FetchMovieDataTask extends AsyncTask<String, Void, Void> {

    private final MovieDiscoveryFragment movieDiscoveryFragment;
    private final String LOG_TAG = FetchMovieDataTask.class.getSimpleName();

    public FetchMovieDataTask(MovieDiscoveryFragment movieDiscoveryFragment) {
        this.movieDiscoveryFragment = movieDiscoveryFragment;
    }

    @Override
    protected Void doInBackground(String... strings) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesJSONStr = null;

        try {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath("top_rated")
                    .appendQueryParameter("page", "1")
                    .appendQueryParameter("api_key", movieDiscoveryFragment.getString(R.string.tmdb_api_key));

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
            moviesJSONStr = buffer.toString();
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
            getMovieDataFromJson(moviesJSONStr);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error get movie data from JSON", e);
            e.printStackTrace();
        }

        return null;

    }

    private void getMovieDataFromJson(String forecastJsonStr)
            throws JSONException, ParseException {

        final String FILM_LIST = "results";
        final String ID = "id";
        final String TITLE = "original_title";
        final String SYNOPSIS = "overview";
        final String POSTER = "poster_path";
        final String RATING = "vote_average";
        final String RELEASEDATE = "release_date";
        final String POPULARITY = "popularity";

        JSONObject pageJSON = new JSONObject(forecastJsonStr);
        JSONArray movieArray = pageJSON.getJSONArray(FILM_LIST);

        Vector<ContentValues> cVVector = new Vector<>(movieArray.length());

        for (int i = 0; i < movieArray.length(); i++) {

            JSONObject singleMovieJSON = movieArray.getJSONObject(i);

            String movieId = singleMovieJSON.getString(ID);
            String movieTitle = singleMovieJSON.getString(TITLE);
            String movieSynopsis = singleMovieJSON.getString(SYNOPSIS);
            String moviePoster = movieDiscoveryFragment.getString(R.string.tmdb_image_link)+singleMovieJSON.getString(POSTER);
            double movieRating = Double.parseDouble(singleMovieJSON.getString(RATING));
            String movieReleaseDate = extractReleaseYear(singleMovieJSON.getString(RELEASEDATE));
            double moviePopularity = Double.parseDouble(singleMovieJSON.getString(POPULARITY));

            int isFavorite = 0;
            Cursor alreadyExist = movieDiscoveryFragment.getContext().getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    new String[]{MovieContract.MovieEntry.COLUMN_IS_FAVORITE},
                    MovieContract.MovieEntry.COLUMN_MOVIEDBID + "= ?",
                    new String[]{movieId},
                    null);

            if(alreadyExist != null && alreadyExist.getColumnCount()!=0 && alreadyExist.moveToFirst()){
                //System.out.println("CURSOR: " + DatabaseUtils.dumpCursorToString(alreadyExist));
                isFavorite = alreadyExist.getInt(0);
                alreadyExist.close();
            }

            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIEDBID, movieId);
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movieTitle);
            movieValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, movieSynopsis);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, moviePoster);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movieReleaseDate);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, movieRating);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, moviePopularity);
            movieValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, isFavorite);

            cVVector.add(movieValues);
        }

        int inserted = 0;
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = movieDiscoveryFragment.getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
        }

        Log.d(LOG_TAG, "FetchMovieTask Complete. " + inserted + " Inserted");

    }

    private String extractReleaseYear(String date) throws ParseException {
        Date year = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        return new SimpleDateFormat("yyyy").format(year);
    }
}



