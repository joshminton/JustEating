package com.example.justeating;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.StrictMode;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PhotoActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE = "image";

    private ArrayList<Establishment> establishments;
    private ArrayAdapter<Establishment> establishmentArrayAdapter;
    private Spinner estabSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        String imagePath = getIntent().getStringExtra(EXTRA_IMAGE);
        File imgFile = new File(imagePath);

        if (imgFile.exists()) {

            Bitmap passedImage = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            ImageView myImage = findViewById(R.id.userPhoto);
            myImage.setImageBitmap(passedImage);
        }

        estabSpinner = findViewById(R.id.establishmentSpinner);

        hideRating();

        estabSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView parentView, View selectedItemView, int position, long id) {
                if(((Establishment) estabSpinner.getSelectedItem()).getId().equals(-1)){
                    hideRating();
                } else {
                    showRating((Establishment) estabSpinner.getSelectedItem());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //i wonder how this works
            }

        });


        establishments = new ArrayList<>();
        establishmentArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, establishments);
        estabSpinner.setAdapter(establishmentArrayAdapter);
        establishments.add(new Establishment("Where are you?", -1));
        establishmentArrayAdapter.notifyDataSetChanged();
        getLocalEstablishments();
    }

    @Override
    public void onResume(){
        super.onResume();
        estabSpinner.setSelection(0);
    }


    public void getLocalEstablishments(){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String localEstablishmentQuery = "http://api.ratings.food.gov.uk/establishments?";

        localEstablishmentQuery = localEstablishmentQuery.concat("schemeTypeKey=FHRS");


        localEstablishmentQuery = localEstablishmentQuery.concat("&longitude=" + "-1.93177951");
        localEstablishmentQuery = localEstablishmentQuery.concat("&latitude=" + "52.43959729");
        localEstablishmentQuery = localEstablishmentQuery.concat("&maxDistanceLimit=" + 1);
        localEstablishmentQuery = localEstablishmentQuery.concat("&pageSize=" + 20);
        localEstablishmentQuery = localEstablishmentQuery.concat("&sortOptionKey=distance");



        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, localEstablishmentQuery, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Got response.");
                        try {
                            populateList(response.getJSONArray("establishments"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("x-api-version", "2");
                return headers;
            }
        };
        requestQueue.add(getRequest);
    }

    public void populateList(JSONArray items){
        if(items.length() == 0){
            ((TextView) findViewById(R.id.noResultsTxt)).setText(R.string.noResults);
        }else{
            try{
                for(int i = 0; i<items.length(); i++){
                    JSONObject jo = items.getJSONObject(i);
                    Establishment est = new Establishment(jo.getString("BusinessName"), jo.getInt("FHRSID"));
                    est.setType(jo.getString("BusinessType"));
                    est.setAddr1(jo.getString("AddressLine1"));
                    est.setAddr2(jo.getString("AddressLine2"));
                    est.setAddr3(jo.getString("AddressLine3"));
                    est.setAddr4(jo.getString("AddressLine4"));
                    est.setPostcode(jo.getString("PostCode"));
                    est.setPhoneNo(jo.getString("Phone"));
                    est.setRating(jo.getString("RatingValue"));
                    est.setHygieneScore(jo.getJSONObject("scores").getString("Hygiene"));
                    est.setStructuralScore(jo.getJSONObject("scores").getString("Structural"));
                    est.setConfidenceScore(jo.getJSONObject("scores").getString("ConfidenceInManagement"));
                    est.setLongitude(jo.getJSONObject("geocode").getString("longitude"));
                    est.setLatitude(jo.getJSONObject("geocode").getString("latitude"));
                    est.setDateRated(jo.getString("RatingDate"));
                    est.setSchemeType(jo.getString("SchemeType"));
                    establishments.add(est);
                }
            }
            catch(JSONException err){}
        }
        establishmentArrayAdapter.notifyDataSetChanged();
    }

    public void hideRating(){
        (findViewById(R.id.ratingSection)).setVisibility(View.INVISIBLE);
    }

    public void showRating(Establishment establishment){

        (findViewById(R.id.ratingSection)).setVisibility(View.VISIBLE);

//        ((TextView) findViewById(R.id.establishmentTitle)).setText(establishment.getName());

        TextView ratingDescription = (findViewById(R.id.ratingDescriptorText));

        if(establishment.getRating().equals("AwaitingInspection") || establishment.getRating().equals("Exempt")){
            ((TextView) findViewById(R.id.rating)).setText(establishment.getRating());
            ((TextView) findViewById(R.id.rating)).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            ((TextView) findViewById(R.id.rating)).setTextSize(20);
            ((TextView) findViewById(R.id.riskHeading)).setVisibility(View.INVISIBLE);
            ((GradientDrawable) findViewById(R.id.ratingBox).getBackground()).setColor(getResources().getColor(R.color.awaitingInspection));
            ratingDescription.setText("");
            ((TextView) findViewById(R.id.establishmentName)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.establishmentName)).setText(establishment.getName());
        } else {
            ((TextView) findViewById(R.id.rating)).setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            ((TextView) findViewById(R.id.rating)).setTextSize(56);
            ((TextView) findViewById(R.id.rating)).setText(establishment.getRating());
            ((TextView) findViewById(R.id.establishmentName)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.establishmentName)).setText(establishment.getName());
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

        }
    }

    public void onDoneClicked(View view){
        View v = findViewById(R.id.innerView);
        v.setDrawingCacheEnabled(true);

// this is the important code :)
// Without it the view will have a dimension of 0,0 and the bitmap will be null
//        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());

        v.buildDrawingCache();
        Bitmap outImage = v.getDrawingCache();
//        v.setDrawingCacheEnabled(false); // clear drawing cache






//        File imageFile = new File(mPath);
//
//        FileOutputStream outputStream = new FileOutputStream(imageFile);
//        int quality = 100;
//        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
//        try{
//            outputStream.flush();
//            outputStream.close();
//        }catch(IOException e, FileNotFoundException f){}

        File out = new File(getExternalCacheDir(), "savedCapture.jpg");

        try (FileOutputStream fOut = new FileOutputStream(out)) {
            outImage.compress(Bitmap.CompressFormat.PNG, 100, fOut); // bmp is your Bitmap instance
        } catch (IOException e) {
            e.printStackTrace();
        }


        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(out));
        shareIntent.setType("image/png");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.shareImageText)));




    }


}
