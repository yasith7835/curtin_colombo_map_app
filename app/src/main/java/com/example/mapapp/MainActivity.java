package com.example.mapapp;
/***************************************************************************************************
 *  Creator: Yasith Dharmasena
 *  Curtin ID: 21214507
 *  Date of completion: 10/02/2024
 *  App: A navigator app on the various locations inside the Curtin Colombo Navam Mawatha campus
 *  ***********************************************************************************************/
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    GroundFloorFragment groundFloorFragment = new GroundFloorFragment();
    FirstFloorFragment firstFloorFragment = new FirstFloorFragment();
    SearchResultFragment searchResultFragment = new SearchResultFragment();
    private List<Locations> locationsList;
    private LocationsDAO locationsDAO;
    private LiveDataClass viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Change color of notification panel to Curtin yellow
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.yellow));
        viewModel = new ViewModelProvider(this).get(LiveDataClass.class);

//        TextView marqueeText = findViewById(R.id.marqueeText);
//        marqueeText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//        marqueeText.setSelected(true);

        // Load the ground floor initially
        loadGroundFloorFragment();

        SearchView searchView = findViewById(R.id.searchView);
        locationsDAO = LocationsDBInstance.getDataBase(getApplicationContext()).locationsDAO();
        // Retrieve all locations
        locationsList = locationsDAO.getAllLocations();

        // Set up the SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Not needed for your case
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Load searchResultFragment when the user starts typing
                if (!TextUtils.isEmpty(newText)) {
                    filterText(newText);
                }
                // If text is empty, load the last viewed floor fragment
                else {
                    viewModel.setClickedValue(viewModel.getLastClickedValue());
                }
                return true;
            }
        });

        viewModel.clickedValue.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (viewModel.getClickedValue() == 1) {
                    loadFirstFloorFragment();
                } else if (viewModel.getClickedValue() == 2) {
                    loadGroundFloorFragment();
                } else if (viewModel.getClickedValue() == 3) {
                    loadSearchResultFragment();
                }
            }
        });
    }

    private void loadGroundFloorFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.mainFrame);
        if (frag == null) {
            fm.beginTransaction().add(R.id.mainFrame, groundFloorFragment).commit();
        }
        else {
            fm.beginTransaction().replace(R.id.mainFrame, groundFloorFragment).commit();
        }
    }
    private void loadFirstFloorFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.mainFrame);
        if (frag == null) {
            fm.beginTransaction().add(R.id.mainFrame, firstFloorFragment).commit();
        }
        else {
            fm.beginTransaction().replace(R.id.mainFrame, firstFloorFragment).commit();
        }
    }
    private void loadSearchResultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.mainFrame);
        SearchResultFragment newSearchResultFragment = new SearchResultFragment();
        if (frag == null) {
            fm.beginTransaction().add(R.id.mainFrame, newSearchResultFragment).commit();
        }
        else {
            fm.beginTransaction().replace(R.id.mainFrame, newSearchResultFragment).commit();
        }
    }

    private void filterText(String text) {
        ArrayList<Locations> filteredList = new ArrayList<>();
        // Filter out the location list
        for (Locations location : locationsList) {
            if (location.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(location);
            }
        }

        // If no matches found
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }
        viewModel.setFilteredList(filteredList);
        loadSearchResultFragment();
    }
}