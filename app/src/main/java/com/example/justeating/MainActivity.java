package com.example.justeating;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

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
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, FilterDialogFragment.FilterDialogListener {

    FragmentManager fm = getSupportFragmentManager();
    HomeFragment homeFragment = new HomeFragment();
    ExploreFragment exploreFragment = new ExploreFragment();

    ArrayList<BusinessType> businessTypes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((BottomNavigationView) findViewById(R.id.bottom_navi)).setOnNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            fm.beginTransaction().add(R.id.frag_frame, homeFragment).commit();
        }
        importFilters();
    }

    protected void importFilters(){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final String typesQuery = "http://api.ratings.food.gov.uk/BusinessTypes";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, typesQuery, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Got response.");
                        try {
                            importEstablishmentTypes(response.getJSONArray("businessTypes"));
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        FragmentTransaction fT = fm.beginTransaction();
        switch (menuItem.getItemId()) {
            case R.id.home_tab:
                fT.replace(R.id.frag_frame, homeFragment);
                break;
            case R.id.favourites_tab:
                fT.replace(R.id.frag_frame, homeFragment);
                break;
            case R.id.explore_tab:
                fT.replace(R.id.frag_frame, exploreFragment);
                break;
        }
        fT.commit();
        return false;
    }

    public void onSearchPress(View view){
        EditText searchBox = findViewById(R.id.searchBox);
        String query = searchBox.getText().toString();

        Intent searchIntent = new Intent(this, SearchActivity.class);
        searchIntent.putExtra(SearchActivity.EXTRA_QUERY, query);
        startActivity(searchIntent);
    }

    public void onFilterPress(View view){
        FilterDialogFragment filterDialogFragment = new FilterDialogFragment();
        filterDialogFragment.setFilterLists(businessTypes);
        filterDialogFragment.show(getSupportFragmentManager(), "filter");
    }

    public void onFilterOKClick(FilterDialogFragment dialog){
        String estabFilter = dialog.getSelectedEstab();
        String regionFilter = dialog.getSelectedRegion();
        String authorityFilter = dialog.getSelectedAuthority();
        System.out.println(estabFilter);
    }

    public void importEstablishmentTypes(JSONArray establishmentTypes){
        try{
            for(int i = 0; i<establishmentTypes.length(); i++){
                JSONObject jo = establishmentTypes.getJSONObject(i);
                businessTypes.add(new BusinessType(jo.getString("BusinessTypeName"), jo.getInt("BusinessTypeId")));
            }
        }
        catch(JSONException err){}
    }
}