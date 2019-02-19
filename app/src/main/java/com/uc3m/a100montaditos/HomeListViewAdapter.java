package com.uc3m.a100montaditos;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;


class HomeListViewAdapter extends BaseAdapter {

    private Context context;
    public List<MenuItem> menuItemList = new ArrayList<MenuItem>();

    //int [] IMAGES = {R.drawable.sandwich1,R.drawable.sandwich2};

    public HomeListViewAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return menuItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        convertView = inflater.inflate(R.layout.menuitems_recyclerview_row, null);
        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView textView_name =  convertView.findViewById(R.id.textView_name);
        TextView textView_description_spanish =  convertView.findViewById(R.id.textView_description_spanish);
        TextView textView_description_english =  convertView.findViewById(R.id.textView_description_english);
        TextView textView_price =  convertView.findViewById(R.id.textView_price);

        textView_name.setTextSize(1, 23);
        textView_name.setTextColor(Color.BLACK);

        if (!menuItemList.isEmpty()) {
            Picasso.get()
                    .load(menuItemList.get(position).getImageUrl())
                    .resize(200, 200)
                    .centerCrop()
                    .transform(new CropCircleTransformation())
                    .into(imageView);
            textView_name.setText(menuItemList.get(position).getName());
            textView_description_spanish.setText(menuItemList.get(position).getDescription_spanish());
            textView_description_english.setText(menuItemList.get(position).getDescription_english());
            textView_price.setText(Double.toString(menuItemList.get(position).getPrice()) + "€");

        }


        return convertView;
    }
}
