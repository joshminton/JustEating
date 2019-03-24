package com.example.justeating;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private String query;
    private boolean listed;
    private ArrayList<Establishment> establishments;
    private EstablishmentListAdapter establishmentsAdpt;
    private ArrayList<Integer> favouriteIds;
    private boolean locationSearch;
    private double latitude, longitude;
    private Integer range;
    private String sortOptionKey;
    private boolean sortMode;
    private FloatingActionButton switchSortFab;
    private Menu menu;

    public static final String EXTRA_QUERY = "query";
    public static final String EXTRA_TYPEFILTER = "type";
    public static final String EXTRA_REGIONFILTER = "region";
    public static final String EXTRA_AUTHORITYFILTER = "authority";
    public static final String EXTRA_IS_LOCATION_SEARCH = "loc_search";
    public static final String EXTRA_LATITUDE = "lat";
    public static final String EXTRA_LONGITUDE = "lon";
    public static final String EXTRA_RANGE = "range";
    public static final String EXTRA_RATINGSQUERY = "ratings_query";

    private Favourites db;

    ProgressBar searchProgressBar;
    ListView searchResultsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchProgressBar = findViewById(R.id.searchProgressBar);
        searchProgressBar.setIndeterminate(true);

        sortOptionKey = "";
        switchSortFab = findViewById(R.id.switchSortBtn);
        switchSortFab.hide();

        query = getIntent().getStringExtra(EXTRA_QUERY);
        locationSearch = getIntent().getBooleanExtra(EXTRA_IS_LOCATION_SEARCH, false);
        if(locationSearch){
            latitude = getIntent().getDoubleExtra(EXTRA_LATITUDE, 0.0);
            longitude = getIntent().getDoubleExtra(EXTRA_LONGITUDE, 0.0);
            range = getIntent().getIntExtra(EXTRA_RANGE, 0);
        }
        establishments = new ArrayList<>();
        establishmentsAdpt = new EstablishmentListAdapter(establishments, this, false);
        searchResultsList = findViewById(R.id.searchResultsList);

        db = Room.databaseBuilder(getApplicationContext(), Favourites.class, "favourites").allowMainThreadQueries().build();
        favouriteIds = new ArrayList<>(db.favouriteDao().retrieveIds());

        final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Establishment clickedItem = (Establishment) establishmentsAdpt.getItem(position);
                Intent intent = new Intent(SearchActivity.this, EstablishmentActivity.class);
                intent.putExtra("establishment", clickedItem);
                startActivity(intent);
            }
        };

        searchResultsList.setOnItemClickListener(itemClickListener);

        searchResultsList.setAdapter(establishmentsAdpt);
        searchEstablishments();
    }

    public void searchEstablishments(){
        searchProgressBar.setVisibility(View.VISIBLE);
        searchResultsList.setVisibility(View.INVISIBLE);
        listed = true;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String establishmentQuery = "http://api.ratings.food.gov.uk/establishments?";

        establishmentQuery = establishmentQuery.concat("schemeTypeKey=FHRS");


        if(locationSearch){
            establishmentQuery = establishmentQuery.concat("&longitude=" + longitude);
            establishmentQuery = establishmentQuery.concat("&latitude=" + latitude);
            establishmentQuery = establishmentQuery.concat("&maxDistanceLimit=" + range);
        } else {
            establishmentQuery = establishmentQuery.concat("&name=" + query);
        }

        if(getIntent().getIntExtra(EXTRA_TYPEFILTER, -1) != -1){
            establishmentQuery = establishmentQuery.concat("&businessTypeId=" + getIntent().getIntExtra(EXTRA_TYPEFILTER, -1));
        }

        if(getIntent().getIntExtra(EXTRA_AUTHORITYFILTER, -1) != -1){
            establishmentQuery = establishmentQuery.concat("&localAuthorityId=" + getIntent().getIntExtra(EXTRA_AUTHORITYFILTER, -1));
        }

//        if(getIntent().getIntExtra(EXTRA_REGIONFILTER, -1) != -1){
//            establishmentQuery.concat("&establishmentType=" + getIntent().getIntExtra("query", -1));
//        }
//        if(getIntent().getIntExtra(EXTRA_AUTHORITYFILTER, -1) != -1){
//            establishmentQuery.concat("&establishmentType=" + getIntent().getIntExtra("query", -1));
//        }

        if(sortOptionKey != ""){
            establishmentQuery = establishmentQuery.concat("&sortOptionKey=" + sortOptionKey);
        }

        if(getIntent().getStringExtra(EXTRA_RATINGSQUERY) != null){
            establishmentQuery = establishmentQuery.concat(getIntent().getStringExtra(EXTRA_RATINGSQUERY));
        }


        System.out.println(establishmentQuery);
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, establishmentQuery, null,
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
        establishments.clear();
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
                    if(favouriteIds.contains(jo.getInt("FHRSID"))){
                        est.setFavourite(true);
                    }else{
                        est.setFavourite(false);
                    }
                    establishments.add(est);
                }
            }
            catch(JSONException err){}
        }
        establishmentsAdpt.notifyDataSetChanged();
        ((ProgressBar) findViewById(R.id.searchProgressBar)).setVisibility(View.INVISIBLE);
        searchResultsList.setVisibility(View.VISIBLE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem sortDistance = menu.findItem(R.id.sortDistance);
        if(!locationSearch){
            sortDistance.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rated_0:
               item.setChecked(true);
                return true;
            case R.id.rated_1:
                item.setChecked(true);
                return true;
            case R.id.rated_2:
                item.setChecked(true);
                return true;
            case R.id.rated_3:
                item.setChecked(true);
                return true;
            case R.id.rated_4:
                item.setChecked(true);
                return true;
            case R.id.rated_5:
                item.setChecked(true);
                return true;
            case R.id.sortRating:
                sortOptionKey = "rating";
                searchEstablishments();
                switchSortFab.show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        favouriteIds = new ArrayList<>(db.favouriteDao().retrieveIds());
        for(Establishment estab : establishments){
            if(favouriteIds.contains(estab.getId())){
                estab.setFavourite(true);
            } else {
                estab.setFavourite(false);
            }
        }
        establishmentsAdpt.notifyDataSetChanged();
        ((ListView) findViewById(R.id.searchResultsList)).invalidateViews();
    }

    public void onSwitchSortPress(View view){
        Collections.reverse(establishments);
        establishmentsAdpt.notifyDataSetChanged();
    }

//    public Comparator<Establishment> ratingComparison(){
//
//    }

//    public class RatingComparator implements Comparator<Establishment> {
//
//        @Override
//        public int compare(Establishment a, Establishment b) {
//            if(a.getSchemeType() == "FHRS" && b.getSchemeType() == "FHRS"){
//                return Integer.parseInt(a.getRating()) - (Integer.parseInt(b.getRating());
//            }
//        }
//    }

}
