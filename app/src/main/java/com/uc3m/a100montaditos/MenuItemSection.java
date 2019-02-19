package com.uc3m.a100montaditos;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

class MenuItemSection extends StatelessSection {

    String title;
    List<MenuItem> list;

    public MenuItemSection(String title, List<MenuItem> list) {
        // call constructor with layout resources for this Section header, footer and items
        super(SectionParameters.builder()
                .itemResourceId(R.layout.menuitems_recyclerview_row)
                .headerResourceId(R.layout.menuitems_recyclerview_section_header)
                .build());
        this.title = title;
        this.list = list;
    }

    @Override
    public int getContentItemsTotal() {
        return list.size(); // number of items of this section
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // return a custom instance of ViewHolder for the items of this section
        return new MenuItemViewHolder(view);
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
        itemHolder.textView_price.setText(price);

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

    public MenuItemViewHolder(final View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imageView);

        textView_name =  itemView.findViewById(R.id.textView_name);
        textView_description_spanish =  itemView.findViewById(R.id.textView_description_spanish);
        textView_description_english =  itemView.findViewById(R.id.textView_description_english);
        textView_price =  itemView.findViewById(R.id.textView_price);

        textView_name.setTextSize(1, 23);
        textView_name.setTextColor(Color.BLACK);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(itemView.getContext(), "Clicked: " + getAdapterPosition(), Toast.LENGTH_LONG).show();

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