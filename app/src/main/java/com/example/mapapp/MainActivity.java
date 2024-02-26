package com.example.mapapp;
/***************************************************************************************************
 *  Creator: Yasith Dharmasena
 *  Curtin ID: 21214507
 *  Date of completion: 10/02/2024
 *  App: A navigator app on the various locations inside the Curtin Colombo Navam Mawatha campus
 *  ***********************************************************************************************/
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    GroundFloorFragment groundFloorFragment = new GroundFloorFragment();
    FirstFloorFragment firstFloorFragment = new FirstFloorFragment();
    SearchResultFragment searchResultFragment = new SearchResultFragment();
    private List<Locations> locationsList;
    private LocationsDAO locationsDAO;
    private LiveDataClass viewModel;
    private SearchView searchView;
    private ImageButton leadToCCButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Change color of notification panel to Curtin yellow
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.yellow));
        viewModel = new ViewModelProvider(this).get(LiveDataClass.class);

        // Load the ground floor initially
        loadGroundFloorFragment();

        leadToCCButton = findViewById(R.id.leadToCCButton);
        searchView = findViewById(R.id.searchView);
        locationsDAO = LocationsDBInstance.getDataBase(getApplicationContext()).locationsDAO();
        // Retrieve all locations
        locationsList = locationsDAO.getAllLocations();

        // Set up lead to cc button
        leadToCCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMapButtonClicked();
            }
        });

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

    private void viewMapButtonClicked(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Location services are not enabled, prompt the user to enable them
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Location services are disabled. Do you want to enable them?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Open location settings screen
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            // Location services are enabled, proceed with launching Google Maps
            String destination = "Curtin Colombo";
            Uri uri = Uri.parse("google.navigation:q=" + Uri.encode(destination));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        }
    }

}