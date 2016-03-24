package com.sprenger.software.movie.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ReviewListAdapter extends ArrayAdapter<ReviewSpec> {
    private Context context;
    private List<ReviewSpec> reviewList;

    public ReviewListAdapter(Context context, List<ReviewSpec> reviewList) {
        super(context, R.layout.single_review_list_element, reviewList);
        this.context = context;
        this.reviewList = reviewList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.single_review_list_element, parent, false);

        TextView authorTextView = (TextView) rowView.findViewById(R.id.list_item_review_author);
        authorTextView.setText(reviewList.get(position).getAutor());

        TextView reviewTextView = (TextView) rowView.findViewById(R.id.list_item_review_text);
        reviewTextView.setText(reviewList.get(position).getReview());


        return rowView;
    }
}