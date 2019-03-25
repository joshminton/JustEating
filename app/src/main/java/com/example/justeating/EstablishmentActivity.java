package com.example.justeating;

import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class EstablishmentActivity extends AppCompatActivity {

    Establishment establishment;

    private Favourites db;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establishment);
        this.establishment = (Establishment) getIntent().getSerializableExtra("establishment");

        fab = findViewById(R.id.favouriteFab);
        toggleFAB();

        db = Room.databaseBuilder(getApplicationContext(), Favourites.class, "favourites").allowMainThreadQueries().build();

        ((TextView) findViewById(R.id.establishmentTitle)).setText(establishment.getName());
        ((TextView) findViewById(R.id.establishmentType)).setText(establishment.getType());

        TextView ratingDescription = (findViewById(R.id.ratingDescriptorText));

        if(establishment.getRating().equals("AwaitingInspection") || establishment.getRating().equals("Exempt")){
            ((TextView) findViewById(R.id.rating)).setText(establishment.getRating());
            ((TextView) findViewById(R.id.rating)).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            ((TextView) findViewById(R.id.rating)).setTextSize(20);
            ((TextView) findViewById(R.id.riskHeading)).setVisibility(View.INVISIBLE);
            ((GradientDrawable) findViewById(R.id.ratingBox).getBackground()).setColor(getResources().getColor(R.color.awaitingInspection));
            ratingDescription.setText("");
            ((TextView) findViewById(R.id.dateRatedText)).setText("");
        } else {
            ((TextView) findViewById(R.id.rating)).setText(establishment.getRating());
            switch (establishment.getRating()) {
                case "0":
                    ((GradientDrawable) findViewById(R.id.ratingBox).getBackground()).setColor(getResources().getColor(R.color.rating0));
                    ratingDescription.setText(R.string.rate0Desc);
                    break;
                case "1":
                    ((GradientDrawable) findViewById(R.id.ratingBox).getBackground()).setColor(getResources().getColor(R.color.rating1));
                    ratingDescription.setText(R.string.rate1Desc);
                    break;
                case "2":
                    ((GradientDrawable) findViewById(R.id.ratingBox).getBackground()).setColor(getResources().getColor(R.color.rating2));
                    ratingDescription.setText(R.string.rate2Desc);
                    break;
                case "3":
                    ((GradientDrawable) findViewById(R.id.ratingBox).getBackground()).setColor(getResources().getColor(R.color.rating3));
                    ratingDescription.setText(R.string.rate3Desc);
                    break;
                case "4":
                    ((GradientDrawable) findViewById(R.id.ratingBox).getBackground()).setColor(getResources().getColor(R.color.rating4));
                    ratingDescription.setText(R.string.rate4Desc);
                    break;
                case "5":
                    ((GradientDrawable) findViewById(R.id.ratingBox).getBackground()).setColor(getResources().getColor(R.color.rating5));
                    ratingDescription.setText(R.string.rate5Desc);
                    break;
            }

            ((TextView) findViewById(R.id.hygieneLabel)).setText("Hygiene: " + establishment.getHygieneScore() + "/25");
            ((TextView) findViewById(R.id.structuralLabel)).setText("Structural: " + establishment.getStructuralScore() + "/25");
            ((TextView) findViewById(R.id.confMangmntLabel)).setText("Management: " + establishment.getConfidenceScore() + "/30");

            DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
            ((TextView) findViewById(R.id.dateRatedText)).setText("Date rated: " + dateFormat.format(establishment.getDateRated()));
        }

        String address = "";
        if(establishment.getAddr1() != ""){
            address += establishment.getAddr1() + "\n";
        }
        if(establishment.getAddr2() != ""){
            address += establishment.getAddr2() + "\n";
        }
        if(establishment.getAddr3() != ""){
            address += establishment.getAddr3() + "\n";
        }
        if(establishment.getAddr4() != ""){
            address += establishment.getAddr4() + "\n";
        }
        if(establishment.getPostcode() != ""){
            address += establishment.getPostcode() + "\n";
        }
        if(address == ""){
            ((TextView) findViewById(R.id.addressLabel)).setText(R.string.noAddress);
        } else {
            ((TextView) findViewById(R.id.addressFullLabel)).setText(address);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.establishment_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.mapMenuBtn:

                return true;
        }
        return true;
    }

    public void onFavouritePress(View view){
        if(establishment.isFavourite()){
            new AlertDialog.Builder(this)
//                            .setTitle("Delete entry")
                    .setMessage("Remove favourite?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            establishment.setFavourite(false);
                            db.favouriteDao().removeEstablishment(establishment);
                            toggleFAB();
                        }
                    })

                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            establishment.addFavourite();
            db.favouriteDao().insertEstablishment(establishment);
        }
        toggleFAB();
    }

    public void onMapPress(View view){

    }

    private void toggleFAB(){
        if(establishment.isFavourite()){
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.favourite)));
        } else {
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.notFavourite)));
            fab.setColorFilter(null);
        }
    }

}
