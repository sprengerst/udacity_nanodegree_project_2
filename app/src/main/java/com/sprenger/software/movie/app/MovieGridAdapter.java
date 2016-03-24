/*
 * Created by Stefan Sprenger
 */

package com.sprenger.software.movie.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sprenger.software.movie.app.data.MovieColumns;
import com.squareup.picasso.Picasso;

class MovieGridAdapter extends CursorAdapter {

    public MovieGridAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
//        int viewType = getItemViewType(cursor.getPosition());

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
                .load(cursor.getString(cursor.getColumnIndex(MovieColumns.POSTERPATH)))
                .into(viewHolder.iconView);
    }


    public static class ViewHolder {
        public final ImageView iconView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
        }
    }








}