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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements MainRecyclerViewAdapter.ItemClickListener {

    private Context context;
    DatabaseReference menuItemsDatabase;
    List<MenuItem> menuItemList;
    ListView listView;
    RecyclerView recyclerView;
    HomeListViewAdapter homeListViewAdapter;
    MainRecyclerViewAdapter mainRecyclerViewAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Set up firebase
        menuItemsDatabase = FirebaseDatabase.getInstance().getReference("menuItems");
        menuItemList = new ArrayList<MenuItem>();
        //homeListViewAdapter.menuItemList = menuItemList;

        // set up the RecyclerView
        recyclerView = getView().findViewById(R.id.menuItems_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mainRecyclerViewAdapter = new MainRecyclerViewAdapter(getActivity(), menuItemList);
        recyclerView.setAdapter(mainRecyclerViewAdapter);
        mainRecyclerViewAdapter.setClickListener(this);


        //Set up listView
/*        listView = getView().findViewById(R.id.listView);
        homeListViewAdapter = new HomeListViewAdapter(getActivity());
        listView.setAdapter(homeListViewAdapter);*/


        //Get initial data
/*        menuItemsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getDataAndUpdateListView(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });*/

    }

    public void getDataAndUpdateListView(DataSnapshot dataSnapshot) {
        menuItemList.clear();
        for (DataSnapshot montaditoSnapshot : dataSnapshot.getChildren()) {
            MenuItem menuItem = montaditoSnapshot.getValue(MenuItem.class);
            menuItemList.add(menuItem);
        }

        mainRecyclerViewAdapter.menuItems = menuItemList;
        recyclerView.setAdapter(mainRecyclerViewAdapter);

        //homeListViewAdapter.menuItemList = menuItemList;
        //listView.setAdapter(homeListViewAdapter);
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

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_LONG).show();
    }
}
