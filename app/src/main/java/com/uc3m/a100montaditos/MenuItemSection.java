package com.uc3m.a100montaditos;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

class MenuItemSection extends StatelessSection {

    String title;
    List<MenuItem> list;
    SectionedRecyclerViewAdapter sectionAdapter;


    public MenuItemSection(String title, List<MenuItem> list, SectionedRecyclerViewAdapter sectionAdapter) {
        // call constructor with layout resources for this Section header, footer and items
        super(SectionParameters.builder()
                .itemResourceId(R.layout.menuitems_recyclerview_row)
                .headerResourceId(R.layout.menuitems_recyclerview_section_header)
                .build());
        this.title = title;
        this.list = list;
        this.sectionAdapter = sectionAdapter;
    }

    @Override
    public int getContentItemsTotal() {
        return list.size(); // number of items of this section
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // return a custom instance of ViewHolder for the items of this section
        return new MenuItemViewHolder(view, sectionAdapter,list);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        MenuItemViewHolder itemHolder = (MenuItemViewHolder) holder;

        String name = list.get(position).getName();
        String description_spanish = list.get(position).getDescription_spanish();
        String description_english = list.get(position).getDescription_english();
        String price = Double.toString(list.get(position).getPrice());

        itemHolder.textView_name.setText(name);
        itemHolder.textView_description_spanish.setText(description_spanish);
        itemHolder.textView_description_english.setText(description_english);
        itemHolder.textView_price.setText(price + "€");

        Picasso.get()
                .load(list.get(position).getImageUrl())
                .resize(200, 200)
                .centerCrop()
                .transform(new CropCircleTransformation())
                .into(itemHolder.imageView);
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new MenuItemHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        MenuItemHeaderViewHolder headerHolder = (MenuItemHeaderViewHolder) holder;

        // bind your header view here
        headerHolder.header.setText(title);
    }

    public void addRow(MenuItem item) {
        this.list.add(item);
    }

}

class MenuItemViewHolder extends RecyclerView.ViewHolder {
    TextView textView_name;
    TextView textView_description_spanish;
    TextView textView_description_english;
    TextView textView_price;
    ImageView imageView;

    public MenuItemViewHolder(final View itemView, final SectionedRecyclerViewAdapter sectionAdapter, final List<MenuItem> list) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imageView);

        textView_name =  itemView.findViewById(R.id.textView_name);
        textView_description_spanish =  itemView.findViewById(R.id.textView_description_spanish);
        textView_description_english =  itemView.findViewById(R.id.textView_description_english);
        textView_price =  itemView.findViewById(R.id.textView_price);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = sectionAdapter.getPositionInSection(getAdapterPosition());
                MenuItem menuItem = list.get(position);
                updateFavorite(menuItem);

            }
        });

    }

    public void updateFavorite(final MenuItem menuItem) {
        DatabaseReference favoritesDatabase = FirebaseDatabase.getInstance().getReference("favorites");

        favoritesDatabase.child(User.getCurrentUser().getUid()).child(menuItem.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference menuItemsDatabase = FirebaseDatabase.getInstance().getReference("menuItems");
                DatabaseReference favoritesDatabase = FirebaseDatabase.getInstance().getReference("favorites");
                if (dataSnapshot.exists()) {
                    //Remove 1 to menuItem's total favorites
                    menuItem.setFavorites(menuItem.getFavorites() - 1);
                    menuItemsDatabase.child(menuItem.getUid()).setValue(menuItem);
                    //Remove from personal favorite
                    favoritesDatabase.child(User.getCurrentUser().getUid()).child(menuItem.getUid()).removeValue();
                    Toast.makeText(itemView.getContext(), "Removed from favorites", Toast.LENGTH_LONG).show();
                }
                else {
                    //Add 1 to menuItem's total favorites
                    menuItem.setFavorites(menuItem.getFavorites() + 1);
                    menuItemsDatabase.child(menuItem.getUid()).setValue(menuItem);
                    //Add to personal favorite
                    favoritesDatabase.child(User.getCurrentUser().getUid()).child(menuItem.getUid()).setValue(menuItem);
                    Toast.makeText(itemView.getContext(), "Added to favorites", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

class MenuItemHeaderViewHolder extends RecyclerView.ViewHolder {
    public TextView header;

    public MenuItemHeaderViewHolder(View itemView) {
        super(itemView);

        header = (TextView) itemView.findViewById(R.id.textView_header);
    }
}