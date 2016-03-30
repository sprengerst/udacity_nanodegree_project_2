package com.sprenger.software.movie.app.configuration;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.sprenger.software.movie.app.R;

import java.util.List;

public class ReviewExpandableAdapter extends ExpandableRecyclerAdapter<ReviewParentViewHolder, ReviewChildViewHolder> {
    private final LayoutInflater mInflater;
    private Context context;
    private List<ParentObject> reviewList;


    public ReviewExpandableAdapter(Context context, List<ParentObject> reviewList) {
        super(context,reviewList);
        this.reviewList = reviewList;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }



    @Override
    public ReviewParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.single_review_list_element_parent, viewGroup, false);
        return new ReviewParentViewHolder(view);
    }

    @Override
    public ReviewChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.single_review_list_element_child, viewGroup, false);
        return new ReviewChildViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(ReviewParentViewHolder reviewParentViewHolder, int i, Object parentObject) {
        ReviewSpec reviewSpec = (ReviewSpec) parentObject;
        reviewParentViewHolder.reviewAuthorTextView.setText(reviewSpec.getAutor());
    }

    @Override
    public void onBindChildViewHolder(ReviewChildViewHolder reviewChildViewHolder, int i, Object parentObject) {
        String reviewSpecText = (String) parentObject;
        reviewChildViewHolder.reviewTextView.setText(reviewSpecText);
    }

}


