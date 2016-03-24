package com.sprenger.software.movie.app;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.CustomViewHolder> {
    private final SparseBooleanArray mCollapsedStatus;
    private Context context;
    private List<ReviewSpec> reviewList;


    public ReviewListAdapter(Context context, List<ReviewSpec> reviewList) {
        this.reviewList = reviewList;
        this.context = context;
        mCollapsedStatus = new SparseBooleanArray();
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_review_list_element, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        customViewHolder.textView.setText(reviewList.get(i).getAutor());
        customViewHolder.expTv1.setText(reviewList.get(i).getReview(),mCollapsedStatus,i);


//
//        customViewHolder.textView.setOnClickListener(clickListener);
//        customViewHolder.imageView.setOnClickListener(clickListener);
//
//        customViewHolder.textView.setTag(customViewHolder);
//        customViewHolder.imageView.setTag(customViewHolder);

    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ExpandableTextView expTv1;
        protected TextView textView;

        public CustomViewHolder(View view) {
            super(view);
            this.expTv1 = (ExpandableTextView) view.findViewById(R.id.expand_text_view);
            this.textView =(TextView) view.findViewById(R.id.list_item_review_author);
        }
    }

    @Override
    public int getItemCount() {
        return (null != reviewList ? reviewList.size() : 0);
    }

}


