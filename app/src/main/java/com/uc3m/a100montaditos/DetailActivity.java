package com.uc3m.a100montaditos;

import android.app.ActionBar;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    MenuItem menuItem;

    /**
     * Fills inn details for textviews and image.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        MenuItem menuItem = (MenuItem) intent.getSerializableExtra("menuItem");
        this.menuItem = menuItem;

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setTitle(menuItem.getName());

        TextView english_textview = findViewById(R.id.detail_english_texview);
        TextView spanish_textview = findViewById(R.id.detail_spanish_texview);
        TextView type_textview = findViewById(R.id.detail_type);
        TextView favorites_textview = findViewById(R.id.detail_favorites);
        TextView price_textview = findViewById(R.id.detail_price);

        ImageView imageView = findViewById(R.id.detail_imageview);
        ImageButton favoriteButton = findViewById(R.id.detail_favoriteButton);
        ImageButton shareButton = findViewById(R.id.detail_shareButton);

        favoriteButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);

        english_textview.setText(menuItem.getDescription_english());
        spanish_textview.setText(menuItem.getDescription_spanish());
        String upperString = menuItem.getType().substring(0,1).toUpperCase() + menuItem.getType().substring(1);
        type_textview.setText(upperString);
        favorites_textview.setText(String.valueOf(menuItem.getFavorites()));
        price_textview.setText(String.valueOf(menuItem.getPrice() + "€"));

        Picasso.get()
                .load(menuItem.getImageUrl())
                .resize(500, 500)
                .centerCrop()
                .transform(new CropCircleTransformation())
                .into(imageView);

        updateFavorite(menuItem, true);


    }

    /**
     * Back button
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Functions for the two buttons, favorite and share.
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_favoriteButton:
                updateFavorite(menuItem, false);
                break;
            case R.id.detail_shareButton:
                share();
                break;
                default:
                    System.out.println("default");

        }

    }

    /**
     * Share sheet for sharing a menuItem
     */
    public void share(){
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, "You have to try this from 100 Montaditos! " + menuItem.getName() + " is the best.");
        startActivity(Intent.createChooser(share, "Choose app to share"));
    }

    /**
     * Favorite or unfavorite a menuItem. Icon will be updated and server will be updated. CheckStatus is for
     * checking if it's already favorited or not (the first time) to display the correct icon.
     * @param menuItem
     * @param checkStatus
     */
    public void updateFavorite(final MenuItem menuItem, final boolean checkStatus) {
        DatabaseReference favoritesDatabase = FirebaseDatabase.getInstance().getReference("favorites");

        favoritesDatabase.child(User.getCurrentUser().getUid()).child(menuItem.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference menuItemsDatabase = FirebaseDatabase.getInstance().getReference("menuItems");
                DatabaseReference favoritesDatabase = FirebaseDatabase.getInstance().getReference("favorites");
                if (dataSnapshot.exists()) {
                    if (checkStatus) {
                        //Update icon
                        ImageButton favoriteButton = findViewById(R.id.detail_favoriteButton);
                        favoriteButton.setImageResource(R.drawable.ic_favorite_pink_24dp);

                    } else {
                        //Remove 1 to menuItem's total favorites
                        menuItem.setFavorites(menuItem.getFavorites() - 1);
                        menuItemsDatabase.child(menuItem.getUid()).setValue(menuItem);
                        //Remove from personal favorite
                        favoritesDatabase.child(User.getCurrentUser().getUid()).child(menuItem.getUid()).removeValue();

                        //Update icon
                        ImageButton favoriteButton = findViewById(R.id.detail_favoriteButton);
                        favoriteButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);

                        TextView favorites_textview = findViewById(R.id.detail_favorites);
                        favorites_textview.setText(String.valueOf(menuItem.getFavorites()));

                        //Toast
                        Toast.makeText(getApplicationContext(), "Removed from favorites", Toast.LENGTH_LONG).show();

                    }


                }
                else {
                    if (checkStatus) {
                        //Update icon
                        ImageButton favoriteButton = findViewById(R.id.detail_favoriteButton);
                        favoriteButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    } else {
                        //Add 1 to menuItem's total favorites
                        menuItem.setFavorites(menuItem.getFavorites() + 1);
                        menuItemsDatabase.child(menuItem.getUid()).setValue(menuItem);
                        //Add to personal favorite
                        favoritesDatabase.child(User.getCurrentUser().getUid()).child(menuItem.getUid()).setValue(menuItem);

                        //Update icon
                        ImageButton favoriteButton = findViewById(R.id.detail_favoriteButton);
                        favoriteButton.setImageResource(R.drawable.ic_favorite_pink_24dp);

                        TextView favorites_textview = findViewById(R.id.detail_favorites);
                        favorites_textview.setText(String.valueOf(menuItem.getFavorites()));

                        //Toast
                        Toast.makeText(getApplicationContext(), "Added to favorites", Toast.LENGTH_LONG).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
