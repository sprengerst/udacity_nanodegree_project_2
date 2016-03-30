/*
 * Created by Stefan Sprenger
 */

package com.sprenger.software.movie.app.configuration;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.sprenger.software.movie.app.R;

public class ReviewParentViewHolder extends ParentViewHolder {

    public final TextView reviewAuthorTextView;

    public ReviewParentViewHolder(View itemView) {
        super(itemView);

        reviewAuthorTextView = (TextView) itemView.findViewById(R.id.list_item_review_author);
    }
}