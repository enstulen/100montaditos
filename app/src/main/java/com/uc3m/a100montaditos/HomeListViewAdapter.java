package com.uc3m.a100montaditos;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.uc3m.a100montaditos.R;


class HomeListViewAdapter extends BaseAdapter {

    private Context context;

    String [] MONTADITOS = {"#sandwich1", "#sandwich2", "#3", "#4", "#5", "#6"};
    String [] MONTADITOS_DESCRIPTIONS = {"Chicken and ham", "Cheese and tomato", "Pie and organge", "Stuff here", "Stuff here", "Stuff here"};

    int [] IMAGES = {R.drawable.sandwich1,R.drawable.sandwich2,R.drawable.sandwich1,R.drawable.sandwich2,R.drawable.sandwich1, R.drawable.sandwich2,};

    public HomeListViewAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return IMAGES.length;
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

        convertView = inflater.inflate(R.layout.montaditos_listview, null);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
        TextView textView_name = (TextView) convertView.findViewById(R.id.textView_name);
        TextView textView_description = (TextView) convertView.findViewById(R.id.textView2_description);

        textView_name.setTextSize(1, 23);
        textView_name.setTextColor(Color.BLACK);

        imageView.setImageResource(IMAGES[position]);
        textView_name.setText(MONTADITOS[position]);
        textView_description.setText(MONTADITOS_DESCRIPTIONS[position]);

        return convertView;
    }
}
