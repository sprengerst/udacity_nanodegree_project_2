package com.sprenger.software.movie.app;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;

public class ReviewListAdapter extends ArrayAdapter<ReviewSpec> {
    private final SparseBooleanArray mCollapsedStatus;
    private Context context;
    private List<ReviewSpec> reviewList;

    public ReviewListAdapter(Context context, List<ReviewSpec> reviewList) {
        super(context, R.layout.single_review_list_element, reviewList);
        this.context = context;
        mCollapsedStatus = new SparseBooleanArray();
        this.reviewList = reviewList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.single_review_list_element, parent, false);

        TextView authorTextView = (TextView) rowView.findViewById(R.id.list_item_review_author);
        authorTextView.setText(reviewList.get(position).getAutor());

//        TextView reviewTextView = (TextView) rowView.findViewById(R.id.list_item_review_text);
//        reviewTextView.setText(reviewList.get(position).getReview());

// sample code snippet to set the text content on the ExpandableTextView
        ExpandableTextView expTv1 = (ExpandableTextView) rowView.findViewById(R.id.expand_text_view);

// IMPORTANT - call setText on the ExpandableTextView to set the text content to display
        expTv1.setText(reviewList.get(position).getReview(),mCollapsedStatus,position);

        return rowView;
    }
}