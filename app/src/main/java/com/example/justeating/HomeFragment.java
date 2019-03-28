package com.example.justeating;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements FilterDialogFragment.FilterDialogListener, View.OnClickListener {

    ArrayList<BusinessType> businessTypes = new ArrayList<>();
    ArrayList<Authority> authorities = new ArrayList<>();
    ArrayList<String> regions = new ArrayList<>();

    Integer estabFilter = -1, regionFilter = -1, authorityFilter = -1;
    String ratingFilterQuery = "";

    private final int FINE_LOCATION_PERMISSION = 1;
    private double latitude, longitude;
    private Integer range;
    LocationManager locationManager;
    LocationListener locationListener;
    private boolean waitingForPermission;
    private FilterDialogFragment filterDialogFragment;
    private Fragment.SavedState filterState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        importFilters();
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button searchBtn = getActivity().findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(this);

        Button filterBtn = getActivity().findViewById(R.id.filterBtn);
        filterBtn.setOnClickListener(this);

        Button locationSearchBtn = getActivity().findViewById(R.id.locationSearchBtn);
        locationSearchBtn.setOnClickListener(this);

        SeekBar rangeBar = getActivity().findViewById(R.id.rangeSeekBar);
        rangeBar.setMax(7);
        rangeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String rangeStr = "";
                switch(progress){
                    case 0:
                        rangeStr = "1 mile";
                        range = 1;
                        break;
                    case 1:
                        rangeStr = "2 miles";
                        range = 2;
                        break;
                    case 2:
                        rangeStr = "3 miles";
                        range = 3;
                        break;
                    case 3:
                        rangeStr = "4 miles";
                        range = 4;
                        break;
                    case 4:
                        rangeStr = "5 miles";
                        range = 5;
                        break;

                    case 5:
                        rangeStr = "10 miles";
                        range = 10;
                        break;

                    case 6:
                        rangeStr = "20 miles";
                        range = 20;
                        break;

                    case 7:
                        rangeStr = "50 miles";
                        range = 50;
                        break;
                }

                ((TextView) getActivity().findViewById(R.id.rangeText)).setText(rangeStr);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        rangeBar.setProgress(0);

//        ((TextView) getActivity().findViewById(R.id.rangeText)).setText(rangeStr);

    }

    protected void importFilters(){
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        final String typesQuery = "http://api.ratings.food.gov.uk/BusinessTypes";
        JsonObjectRequest businessTypeRequest = new JsonObjectRequest(Request.Method.GET, typesQuery, null,
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
        requestQueue.add(businessTypeRequest);

        final String authoritiesQuery = "http://api.ratings.food.gov.uk/Authorities";
        JsonObjectRequest authorityRequest = new JsonObjectRequest(Request.Method.GET, authoritiesQuery, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Got response.");
                        try {
                            importAuthorities(response.getJSONArray("authorities"));
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
        requestQueue.add(authorityRequest);

        final String regionsQuery = "http://api.ratings.food.gov.uk/Regions";
        JsonObjectRequest regionRequest = new JsonObjectRequest(Request.Method.GET, regionsQuery, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Got response.");
                        try {
                            importRegions(response.getJSONArray("regions"));
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
        requestQueue.add(regionRequest);
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    public void onSearchPress(View view){
        EditText searchBox = getActivity().findViewById(R.id.searchBox);
        String query = searchBox.getText().toString();

        Intent searchIntent = new Intent(this.getContext(), SearchActivity.class);
        searchIntent.putExtra(SearchActivity.EXTRA_QUERY, query);

        searchIntent.putExtra(SearchActivity.EXTRA_TYPEFILTER, estabFilter);
        searchIntent.putExtra(SearchActivity.EXTRA_REGIONFILTER, regionFilter);
        searchIntent.putExtra(SearchActivity.EXTRA_AUTHORITYFILTER, authorityFilter);
        searchIntent.putExtra(SearchActivity.EXTRA_IS_LOCATION_SEARCH, false);
        searchIntent.putExtra(SearchActivity.EXTRA_RATINGSQUERY, ratingFilterQuery);

        startActivity(searchIntent);
    }

    public void onLocationSearchPress(View view){

        EditText searchBox = getActivity().findViewById(R.id.searchBox);

        Intent searchIntent = new Intent(this.getContext(), SearchActivity.class);

        searchIntent.putExtra(SearchActivity.EXTRA_TYPEFILTER, estabFilter);
        searchIntent.putExtra(SearchActivity.EXTRA_REGIONFILTER, regionFilter);
        searchIntent.putExtra(SearchActivity.EXTRA_AUTHORITYFILTER, authorityFilter);
        searchIntent.putExtra(SearchActivity.EXTRA_IS_LOCATION_SEARCH, true);
        searchIntent.putExtra(SearchActivity.EXTRA_RANGE, range);
        searchIntent.putExtra(SearchActivity.EXTRA_RATINGSQUERY, ratingFilterQuery);

        startActivity(searchIntent);
    }

    public void onFilterPress(View view){
        if(filterDialogFragment == null){
            filterDialogFragment = new FilterDialogFragment();
            filterDialogFragment.setFilterLists(businessTypes, authorities, regions);
        }
        filterDialogFragment.setInitialSavedState(filterState);
        filterDialogFragment.show(getFragmentManager(), "filter");
    }

    public void onFilterOKClick(FilterDialogFragment dialog){
        estabFilter = dialog.getSelectedEstab().getId();
        authorityFilter = dialog.getSelectedAuthority().getId();
        ratingFilterQuery = dialog.getRatingsQuery();
        filterState = getFragmentManager().saveFragmentInstanceState(dialog);

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

    public void importAuthorities(JSONArray authoritiesList){
        try{
            for(int i = 0; i<authoritiesList.length(); i++){
                JSONObject jo = authoritiesList.getJSONObject(i);
                authorities.add(new Authority(jo.getInt("LocalAuthorityId"), jo.getString("Name"), jo.getString("RegionName")));
            }
        }
        catch(JSONException err){}
    }

    public void importRegions(JSONArray regionsList){
        regions.add("None");
        try{
            for(int i = 0; i<regionsList.length(); i++){
                JSONObject jo = regionsList.getJSONObject(i);
                regions.add(jo.getString("name"));
            }
        }
        catch(JSONException err){}
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.searchBtn:
                onSearchPress(v);
                break;

            case R.id.filterBtn:
                onFilterPress(v);
                break;

            case R.id.locationSearchBtn:
                onLocationSearchPress(v);
                break;
        }
    }

    public Double mileConversion(Double kilometres){
        return kilometres * 0.621371;
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        System.out.println("onRequestPermissionsResult");
//        switch(requestCode){
//            case FINE_LOCATION_PERMISSION: {
//                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    waitingForPermission = false;
//                    attachLocManager();
//                } else {
//                }
//                return;
//            }
//        }
//    }
}
