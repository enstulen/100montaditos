package com.uc3m.a100montaditos;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class TopListRecyclerViewAdapter extends RecyclerView.Adapter<TopListRecyclerViewAdapter.ViewHolder> {

    private List<MenuItem> menuItems;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    TopListRecyclerViewAdapter(Context context, List<MenuItem> menuItems) {
        this.mInflater = LayoutInflater.from(context);
        this.menuItems = menuItems;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.toplist_recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = menuItems.get(position).getName();
        int favorites = menuItems.get(position).getFavorites();

        holder.toplistNumberTextView.setText(Integer.toString(position + 1));
        holder.toplistNameTextView.setText(name);
        holder.toplistFavoritesTextView.setText(Integer.toString(favorites) + " favorites");
        Picasso.get()
                .load(menuItems.get(position).getImageUrl())
                .resize(200, 200)
                .centerCrop()
                .transform(new CropCircleTransformation())
                .into(holder.toplistImageView);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return menuItems.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView toplistNumberTextView;
        TextView toplistNameTextView;
        TextView toplistFavoritesTextView;
        ImageView toplistImageView;

        ViewHolder(View itemView) {
            super(itemView);
            toplistNumberTextView = itemView.findViewById(R.id.toplist_number);
            toplistNameTextView = itemView.findViewById(R.id.toplist_name);
            toplistFavoritesTextView = itemView.findViewById(R.id.toplist_favorites);
            toplistImageView = itemView.findViewById(R.id.toplist_imageView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
