package com.sprenger.software.movie.app;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;

public class ReviewChildViewHolder extends ChildViewHolder {

    public TextView reviewTextView;

    public ReviewChildViewHolder(View itemView) {
        super(itemView);
        reviewTextView = (TextView) itemView.findViewById(R.id.list_item_review_text);
    }
}