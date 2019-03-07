package com.example.justeating;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    FragmentManager fm = getSupportFragmentManager();
    HomeFragment homeFragment = new HomeFragment();
    ExploreFragment exploreFragment = new ExploreFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((BottomNavigationView) findViewById(R.id.bottom_navi)).setOnNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            fm.beginTransaction().add(R.id.frag_frame, homeFragment).commit();
        }
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
}