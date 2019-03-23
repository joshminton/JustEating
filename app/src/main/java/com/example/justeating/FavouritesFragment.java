package com.example.justeating;


import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouritesFragment extends Fragment {

    private ArrayList<Establishment> favourites;
    private EstablishmentListAdapter favouritesAdpt;
    private ArrayList<Integer> favouriteIds;

    private Favourites db; //In the class definition


    public FavouritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        favourites = new ArrayList<>();
        favouritesAdpt = new EstablishmentListAdapter(favourites, getContext(), true);
        ListView favouritesList = getActivity().findViewById(R.id.favouritesList);

        final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Establishment clickedItem = (Establishment) favouritesAdpt.getItem(position);
                Intent intent = new Intent(getContext(), EstablishmentActivity.class);
                intent.putExtra("establishment", clickedItem);
                startActivity(intent);
            }
        };

        favouritesList.setOnItemClickListener(itemClickListener);

        favouritesList.setAdapter(favouritesAdpt);

        db = Room.databaseBuilder(getContext().getApplicationContext(), Favourites.class, "favourites").allowMainThreadQueries().build();

        importFavourites();
    }

    public void importFavourites(){
        favourites.addAll(db.favouriteDao().retrieveAll());
        favouritesAdpt.notifyDataSetChanged();
        if(favourites.isEmpty()){
            ((TextView) getActivity().findViewById(R.id.noFavouritesTxt)).setText(getResources().getText(R.string.noFavourites));
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        favouriteIds = new ArrayList<>(db.favouriteDao().retrieveIds());

        Iterator<Establishment> favouritesIt = favourites.iterator();

        while(favouritesIt.hasNext()){
            Establishment estab = favouritesIt.next();
            if(favouriteIds.contains(estab.getId())){
                estab.setFavourite(true);
            } else {
                estab.setFavourite(false);
                favouritesIt.remove();
            }
        }
        favouritesAdpt.notifyDataSetChanged();
    }
}