package com.uc3m.a100montaditos;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FavoritesFragment extends Fragment {

    private Button mSendData;
    DatabaseReference databaseMontaditos;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView title = getActivity().findViewById(getResources().getIdentifier("action_bar_title", "id", getActivity().getPackageName()));
        title.setText("Favorites");
        mSendData = (Button) getView().findViewById(R.id.addString);
        mSendData.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(), "Added to DB", Toast.LENGTH_LONG).show();
                databaseMontaditos = FirebaseDatabase.getInstance().getReference("menuItems");
                String id = databaseMontaditos.push().getKey();
                MenuItem menuItem = new MenuItem("#1", "Espanol", "english", 2.5, "https://firebasestorage.googleapis.com/v0/b/montaditos-f976b.appspot.com/o/menu-items%2F1.jpg?alt=media&token=72309587-4e9e-486f-8485-8283d9c126bf", 1, "montadito");
                databaseMontaditos.child(id).setValue(menuItem);
            }
        });


    }


}
