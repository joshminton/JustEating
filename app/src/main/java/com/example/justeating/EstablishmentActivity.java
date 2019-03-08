package com.example.justeating;

import android.content.Intent;
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
        ((TextView) findViewById(R.id.rating)).setText(establishment.getRating());

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

    public void onCallPress(View view){
        System.out.println(establishment.getPhoneNo());
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + establishment.getPhoneNo()));
        startActivity(intent);
    }

}
