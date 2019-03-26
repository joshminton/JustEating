package com.example.justeating;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.util.Pair;
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
    FavouritesFragment favouritesFragment = new FavouritesFragment();
    CameraFragment cameraFragment = new CameraFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((BottomNavigationView) findViewById(R.id.bottom_navi)).setOnNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            fm.beginTransaction().add(R.id.frag_frame, homeFragment).commit();
            ((BottomNavigationView) findViewById(R.id.bottom_navi)).setSelectedItemId(R.id.home_tab);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        FragmentTransaction fT = fm.beginTransaction();
        menuItem.setChecked(true);
        switch (menuItem.getItemId()) {
            case R.id.home_tab:
                fT.replace(R.id.frag_frame, homeFragment);
                break;
            case R.id.favourites_tab:
                fT.replace(R.id.frag_frame, favouritesFragment);
                break;
            case R.id.camera_tab:
                fT.replace(R.id.frag_frame, cameraFragment);
                break;
        }
        fT.commit();
        return false;
    }

    @Override
    public void onFilterOKClick(FilterDialogFragment dialog) {
        homeFragment.onFilterOKClick(dialog);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        System.out.println("Main Activity");
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        homeFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
}