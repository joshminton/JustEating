package com.example.justeating;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private String query;
    private boolean listed;
    private ArrayList<Establishment> establishments;
    private ArrayAdapter establishmentsAdpt;
    public static final String EXTRA_QUERY = "query";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        query = getIntent().getStringExtra("query");
        establishments = new ArrayList<>();
        establishmentsAdpt = new ArrayAdapter(this, android.R.layout.simple_selectable_list_item, establishments);
        ListView searchResultsList = findViewById(R.id.searchResultsList);
        searchResultsList.setAdapter(establishmentsAdpt);
        searchEstablishments();
    }

    public void searchEstablishments(){
        System.out.println("SEARCH");
        listed = true;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final String establishmentQuery = "http://api.ratings.food.gov.uk/establishments?name=" + query;
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
        try{
            for(int i = 0; i<items.length(); i++){
                JSONObject jo = items.getJSONObject(i);
                establishments.add(new Establishment(jo.getString("BusinessName")));
            }
        }
        catch(JSONException err){}
        establishmentsAdpt.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
