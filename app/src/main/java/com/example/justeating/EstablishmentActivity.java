package com.example.justeating;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class EstablishmentActivity extends AppCompatActivity {

    Establishment establishment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establishment);
        this.establishment = (Establishment) getIntent().getSerializableExtra("establishment");
        ((TextView) findViewById(R.id.establishmentTitle)).setText(establishment.getName());
        ((TextView) findViewById(R.id.establishmentType)).setText(establishment.getType());
        ((TextView) findViewById(R.id.rating)).setText(establishment.getRating());

        TextView ratingDescription = (findViewById(R.id.ratingDescriptorText));

        switch(establishment.getRating()){
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

        ((TextView) findViewById(R.id.hygieneLabel)).setText("Hygiene: " + establishment.getHygieneScore() + "/25");
        ((TextView) findViewById(R.id.structuralLabel)).setText("Structural: " + establishment.getStructuralScore() + "/25");
        ((TextView) findViewById(R.id.confMangmntLabel)).setText("Management: " + establishment.getConfidenceScore() + "/30");


    }

}
