package com.example.justeating;

import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class EstablishmentListAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<Establishment> establishments;
    private Context context;
    private ImageButton faveButton;
    private boolean isFavouritesList;

    private Favourites db; //In the class definition

    public EstablishmentListAdapter(ArrayList<Establishment> establishments, Context context, boolean isFavouritesList) {
        this.establishments = establishments;
        this.context = context;
        this.isFavouritesList = isFavouritesList;
        db = Room.databaseBuilder(this.context.getApplicationContext(), Favourites.class, "favourites").allowMainThreadQueries().build();

    }

    @Override
    public int getCount() {
        return establishments.size();
    }

    @Override
    public Object getItem(int pos) {
        return establishments.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item, null);
        }

//        view.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                new AlertDialog.Builder(context).setTitle("touched").show();
//            }
//
//        });

        //Handle TextView and display string from your list
        ((TextView) view.findViewById(R.id.listEstabName)).setText(establishments.get(position).toString());
        if(!establishments.get(position).getAddr4().equals("")){
            ((TextView) view.findViewById(R.id.listEstabCity)).setText(establishments.get(position).getAddr4());
        } else if(!establishments.get(position).getAddr3().equals("")){
            ((TextView) view.findViewById(R.id.listEstabCity)).setText(establishments.get(position).getAddr3());
        } else if(!establishments.get(position).getAddr2().equals("")){
            ((TextView) view.findViewById(R.id.listEstabCity)).setText(establishments.get(position).getAddr2());
        } else if(!establishments.get(position).getPostcode().equals("")){
            ((TextView) view.findViewById(R.id.listEstabCity)).setText(establishments.get(position).getPostcode());
        }


        //Handle buttons and add onClickListeners
        faveButton = view.findViewById(R.id.faveBtn);

        if(establishments.get(position).isFavourite()){
            ((ImageButton) view.findViewById(R.id.faveBtn)).setImageResource(R.drawable.round_favorite_36);
            faveButton.setColorFilter(Color.RED);
        } else {
            ((ImageButton) view.findViewById(R.id.faveBtn)).setImageResource(R.drawable.round_favorite_border_36);
            faveButton.setColorFilter(Color.LTGRAY);
        }

        faveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v) {
                final Establishment estab = establishments.get(position);

                if(estab.isFavourite()){
                    new AlertDialog.Builder(context)
//                            .setTitle("Delete entry")
                            .setMessage("Remove favourite?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    estab.removeFavourite();
                                    ((ImageButton) v.findViewById(R.id.faveBtn)).setImageResource(R.drawable.round_favorite_border_36);
                                    ((ImageButton) v.findViewById(R.id.faveBtn)).setColorFilter(Color.LTGRAY);
                                    db.favouriteDao().removeEstablishment(estab);
                                    if(isFavouritesList){
                                        establishments.remove(estab);
                                        notifyDataSetChanged();
                                    }
                                }
                            })

                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    estab.addFavourite();
                    ((ImageButton) v.findViewById(R.id.faveBtn)).setImageResource(R.drawable.round_favorite_36);
                    ((ImageButton) v.findViewById(R.id.faveBtn)).setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
                    estab.setFavourite(true);
                    db.favouriteDao().insertEstablishment(estab);
                }

                notifyDataSetChanged();
            }
        });

        return view;
    }
}