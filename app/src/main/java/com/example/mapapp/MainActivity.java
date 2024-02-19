package com.example.mapapp;
/***************************************************************************************************
 *  Creator: Yasith Dharmasena
 *  Curtin ID: 21214507
 *  Date of completion: 10/02/2024
 *  App: A navigator app on the various locations inside the Curtin Colombo Navam Mawatha campus
 *  ***********************************************************************************************/
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    GroundFloorFragment groundFloorFragment = new GroundFloorFragment();
    FirstFloorFragment firstFloorFragment = new FirstFloorFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.yellow));

//        TextView marqueeText = findViewById(R.id.marqueeText);
//        marqueeText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//        marqueeText.setSelected(true);
        LiveDataClass viewModel = new ViewModelProvider(this).get(LiveDataClass.class);
        loadGroundFloorFragment();
        viewModel.clickedValue.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (viewModel.getClickedValue() == 1) {
                    loadFirstFloorFragment();
                } else if (viewModel.getClickedValue() == 2) {
                    loadGroundFloorFragment();
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
}