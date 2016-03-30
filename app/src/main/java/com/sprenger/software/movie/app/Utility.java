package com.sprenger.software.movie.app;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.sprenger.software.movie.app.data.MovieContract;

/**
 * Created by stefa on 24.03.2016.
 *
 */
public class Utility {
    public static String getPreferedSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sortOrder = prefs.getString(context.getString(R.string.pref_sortorder_key), context.getResources().getStringArray(R.array.pref_sortorder_keystore)[0]);
        return sortOrder;
    }

    public static boolean getOnlyFavoriteOption(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean onlyFavorite = prefs.getBoolean(context.getString(R.string.pref_favorite_key), false);
        return onlyFavorite;
    }


    public static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_ID,
            MovieContract.MovieEntry.COLUMN_IS_FAVORITE,
    };


    // These indices are tied to MOVIE_COLUMNS.  If MOVIE_COLUMNS changes, these
    // must change.
    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_TITLE = 1;
    static final int COL_MOVIE_SYNOPSIS = 2;
    static final int COL_MOVIE_POSTER_PATH = 3;
    static final int COL_MOVIE_RELEASE_DATE = 4;
    static final int COL_MOVIE_POPUlARITY = 5;
    static final int COL_MOVIE_RATING = 6;
    static final int COL_MOVIE_MOVIEID = 7;
    static final int COL_MOVIE_ISFAVORITE = 8;


    public static void watchYoutubeVideo(String id,Context context){
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            context.startActivity(intent);
        }
    }


}
