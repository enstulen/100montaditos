package com.uc3m.a100montaditos;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class HomeFragment extends Fragment {

    DatabaseReference menuItemsDatabase;
    List<MenuItem> montaditosList, drinksList;
    RecyclerView recyclerView;
    SectionedRecyclerViewAdapter sectionAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Set title
        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText("Menu");

        //Set up firebase
        menuItemsDatabase = FirebaseDatabase.getInstance().getReference("menuItems");
        montaditosList = new ArrayList<MenuItem>();
        drinksList = new ArrayList<MenuItem>();

        // Create an instance of SectionedRecyclerViewAdapter
        sectionAdapter = new SectionedRecyclerViewAdapter();

        // Create sections with the list of data
        MenuItemSection favoritesSection = new MenuItemSection("Montaditos", montaditosList);
        MenuItemSection contactsSection = new MenuItemSection("Drinks", drinksList);

        // Add Sections to the adapter
        sectionAdapter.addSection(favoritesSection);
        sectionAdapter.addSection(contactsSection);

        // Set up RecyclerView with the SectionedRecyclerViewAdapter
        recyclerView = getView().findViewById(R.id.menuItems_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sectionAdapter);


    }

    public void getDataAndUpdateListView(DataSnapshot dataSnapshot) {
        montaditosList.clear();
        drinksList.clear();

        for (DataSnapshot montaditoSnapshot : dataSnapshot.getChildren()) {
            MenuItem menuItem = montaditoSnapshot.getValue(MenuItem.class);
            if (menuItem.getType().equals("montadito")) {
                montaditosList.add(menuItem);
            } else if (menuItem.getType().equals("drink")) {
                drinksList.add(menuItem);
            }
        }

        sectionAdapter.removeAllSections();
        MenuItemSection favoritesSection = new MenuItemSection("Montaditos", montaditosList);
        MenuItemSection contactsSection = new MenuItemSection("Drinks", drinksList);

        sectionAdapter.addSection(favoritesSection);
        sectionAdapter.addSection(contactsSection);
        recyclerView.setAdapter(sectionAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();

        menuItemsDatabase.addValueEventListener(new ValueEventListener() {
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
