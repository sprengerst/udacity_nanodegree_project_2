/*
 * Created by Stefan Sprenger
 */

package com.sprenger.software.movie.app.configuration;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sprenger.software.movie.app.R;
import com.sprenger.software.movie.app.utilities.Utility;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailerGridAdapter extends RecyclerView.Adapter<TrailerGridAdapter.CustomViewHolder> {

    private final List<String> trailerItemList;
    private final List<String> trailerLinkList;
    private final Context mContext;

    public TrailerGridAdapter(Context context, List<String> trailerItemList,List<String> trailerLinkList) {
        this.trailerItemList = trailerItemList;
        this.trailerLinkList = trailerLinkList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_trailer_grid_element, null);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        customViewHolder.textView.setText(trailerItemList.get(i));
                Picasso
                .with(mContext)
                .load(R.drawable.ic_launcher)
                .into(customViewHolder.imageView);


        customViewHolder.textView.setOnClickListener(clickListener);
        customViewHolder.imageView.setOnClickListener(clickListener);

        customViewHolder.textView.setTag(customViewHolder);
        customViewHolder.imageView.setTag(customViewHolder);

    }

    @Override
    public int getItemCount() {
        return (null != trailerItemList ? trailerItemList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final TextView textView;

        public CustomViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.list_item_trailer_icon);
            this.textView = (TextView) view.findViewById(R.id.list_item_trailer_text);
        }
    }


    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CustomViewHolder holder = (CustomViewHolder) view.getTag();
            int position = holder.getAdapterPosition();
            Utility.watchYoutubeVideo(trailerLinkList.get(position), mContext);
        }
    };
}