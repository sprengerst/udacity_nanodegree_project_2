package com.sprenger.software.movie.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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


}
