/*
 * Created by Stefan Sprenger
 */

package com.sprenger.software.movie.app.configuration;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sprenger.software.movie.app.R;
import com.sprenger.software.movie.app.utilities.Utility;
import com.squareup.picasso.Picasso;

public class MovieGridAdapter extends CursorAdapter {

    public MovieGridAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.single_movie_grid_element, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        Picasso
                .with(context)
                .load(cursor.getString(Utility.COL_MOVIE_POSTER_PATH))
                .into(viewHolder.iconView);
    }


    public static class ViewHolder {
        public final ImageView iconView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
        }
    }
}