package com.uc3m.a100montaditos;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder>  {

    public List<MenuItem> menuItems;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    MainRecyclerViewAdapter(Context context, List<MenuItem> menuItems) {
        this.mInflater = LayoutInflater.from(context);
        this.menuItems = menuItems;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.menuitems_recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = menuItems.get(position).getName();
        String description_spanish = menuItems.get(position).getDescription_spanish();
        String description_english = menuItems.get(position).getDescription_english();
        String price = Double.toString(menuItems.get(position).getPrice());

        holder.textView_name.setText(name);
        holder.textView_description_spanish.setText(description_spanish);
        holder.textView_description_english.setText(description_english);
        holder.textView_price.setText(price);

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return menuItems.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView_name;
        TextView textView_description_spanish;
        TextView textView_description_english;
        TextView textView_price;
        ViewHolder(View itemView) {
            super(itemView);
            textView_name =  itemView.findViewById(R.id.textView_name);
            textView_description_spanish =  itemView.findViewById(R.id.textView_description_spanish);
            textView_description_english =  itemView.findViewById(R.id.textView_description_english);
            textView_price =  itemView.findViewById(R.id.textView_price);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return menuItems.get(id).getName();
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
