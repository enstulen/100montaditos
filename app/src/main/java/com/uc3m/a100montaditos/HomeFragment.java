package com.uc3m.a100montaditos;


import android.content.Context;
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
import android.widget.ListView;
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
import java.util.Comparator;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class HomeFragment extends Fragment {

    DatabaseReference menuItemsDatabase;
    List<MenuItem> montaditosList, drinksList, otherList;
    RecyclerView recyclerView;
    SectionedRecyclerViewAdapter sectionAdapter;
    Query menuItemsQuery;


    /**
     * Get items from firebase and setup recyclerView
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Set title
        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText("Menu");

        //Set up firebase
        menuItemsDatabase = FirebaseDatabase.getInstance().getReference("menuItems");

        menuItemsDatabase = FirebaseDatabase.getInstance().getReference("menuItems");
        menuItemsQuery = menuItemsDatabase.orderByChild("name").limitToFirst(250);

        montaditosList = new ArrayList<MenuItem>();
        drinksList = new ArrayList<MenuItem>();
        otherList = new ArrayList<MenuItem>();

        // Create an instance of SectionedRecyclerViewAdapter
        sectionAdapter = new SectionedRecyclerViewAdapter();

        // Set up RecyclerView with the SectionedRecyclerViewAdapter
        recyclerView = getView().findViewById(R.id.menuItems_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sectionAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.computeVerticalScrollOffset() == 0) {
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setElevation(0);
                } else {
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setElevation(4 * getContext().getResources().getDisplayMetrics().density);
                }
            }
        });


    }
    /**
     * Get data from firebase and update the list view
     * @param dataSnapshot
     */
    public void getDataAndUpdateListView(DataSnapshot dataSnapshot) {
        montaditosList.clear();
        drinksList.clear();
        otherList.clear();

        for (DataSnapshot montaditoSnapshot : dataSnapshot.getChildren()) {
            MenuItem menuItem = montaditoSnapshot.getValue(MenuItem.class);
            menuItem.setUid(montaditoSnapshot.getKey());
            if (menuItem.getType().equals("montadito")) {
                montaditosList.add(menuItem);
            } else if (menuItem.getType().equals("drink")) {
                drinksList.add(menuItem);
            }else if (menuItem.getType().equals("other")) {
                otherList.add(menuItem);
            }
        }

        Collections.sort(montaditosList, new Comparator<MenuItem>() {
            public int compare(MenuItem o1, MenuItem o2) {
                return extractInt(o1.name) - extractInt(o2.name);
            }

            int extractInt(String s) {
                String num = s.replaceAll("\\D", "");
                // return 0 if no digits found
                return num.isEmpty() ? 0 : Integer.parseInt(num);
            }
        });

        sectionAdapter.removeAllSections();
        if (!montaditosList.isEmpty()) {
            MenuItemSection menuItemSection = new MenuItemSection("MONTADITOS", montaditosList, sectionAdapter);
            sectionAdapter.addSection(menuItemSection);
        }
        if (!drinksList.isEmpty()) {
            MenuItemSection menuItemSection = new MenuItemSection("DRINKS", drinksList, sectionAdapter);
            sectionAdapter.addSection(menuItemSection);
        }
        if (!otherList.isEmpty()) {
            MenuItemSection menuItemSection = new MenuItemSection("OTHER", otherList, sectionAdapter);
            sectionAdapter.addSection(menuItemSection);
        }
        recyclerView.setAdapter(sectionAdapter);

    }


    /**
     * Run this when starting
     */
    @Override
    public void onStart() {
        super.onStart();

        menuItemsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getDataAndUpdateListView(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, null);
    }

}
