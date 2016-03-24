package com.sprenger.software.movie.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailerGridAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> listTrailerName;
    private List<String> listTrailerLink;

    public TrailerGridAdapter(Context context, List<String> listTrailerName,List<String> listTrailerLink) {
        super(context, R.layout.single_trailer_grid_element, listTrailerLink);
        this.context = context;
        this.listTrailerName = listTrailerName;
        this.listTrailerLink = listTrailerLink;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.single_trailer_grid_element, parent, false);

        TextView textViewMessage = (TextView) rowView.findViewById(R.id.list_item_trailer_text);
        textViewMessage.setText(listTrailerName.get(position));

        ImageView imageView = (ImageView) rowView.findViewById(R.id.list_item_trailer_icon);

        Picasso
                .with(context)
                .load(R.drawable.ic_launcher)
                .into(imageView);

        return rowView;
    }
}