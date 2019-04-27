package com.uc3m.a100montaditos;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ToplistFragment extends Fragment implements TopListRecyclerViewAdapter.ItemClickListener {

    DatabaseReference menuItemsDatabase;
    TopListRecyclerViewAdapter adapter;
    ArrayList<MenuItem> menuItems = new ArrayList<>();
    RecyclerView recyclerView;
    Query topListQuery;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_toplist, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText("Top List");

        // set up the RecyclerView
        recyclerView = getActivity().findViewById(R.id.toplist_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TopListRecyclerViewAdapter(getContext(), menuItems);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        menuItemsDatabase = FirebaseDatabase.getInstance().getReference("menuItems");
        topListQuery = menuItemsDatabase.orderByChild("favorites").limitToFirst(100);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.computeVerticalScrollOffset() == 0) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setElevation(0);
                } else {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setElevation(4 * getContext().getResources().getDisplayMetrics().density);
                }
            }
        });

    }

    public void getDataAndUpdateListView(DataSnapshot dataSnapshot) {
        menuItems.clear();

        for (DataSnapshot montaditoSnapshot : dataSnapshot.getChildren()) {
            MenuItem menuItem = montaditoSnapshot.getValue(MenuItem.class);
            menuItem.setUid(montaditoSnapshot.getKey());
            menuItems.add(menuItem);
        }
        Collections.reverse(menuItems);

        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();

        topListQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getDataAndUpdateListView(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {

        MenuItem menuItem = menuItems.get(position);
        Context context = getActivity().getApplicationContext();

        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("menuItem", menuItem);
        context.startActivity(intent);


    }
}



