package com.example.mapapp;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ortiz.touchview.TouchImageView;

import java.util.List;

public class FirstFloorFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public FirstFloorFragment() {
        // Required empty public constructor
    }
    public static FirstFloorFragment newInstance(String param1, String param2) {
        FirstFloorFragment fragment = new FirstFloorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private ImageView mapImageView;
    private Dialog dialog;
    private List<Locations> locationList;
    private LocationsDAO locationsDAO;
    private float scaleFactor = 1.0f;
    private float lastX, lastY;
    private Matrix matrix = new Matrix();
    private ScaleGestureDetector scaleGestureDetector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first_floor, container, false);
        locationsDAO = LocationsDBInstance.getDataBase(getActivity().getApplicationContext()).locationsDAO();
        LiveDataClass viewModel = new ViewModelProvider(getActivity()).get(LiveDataClass.class);

        mapImageView = view.findViewById(R.id.mapImageView);
        dialog = new Dialog(getContext());
        mapImageView.setImageMatrix(matrix);

        LinearLayout floorButtonLayout = view.findViewById(R.id.floorButtonLayout);
        Button groundFloorButton = floorButtonLayout.findViewById(R.id.groundFloorButton);
        Button firstFloorButton = floorButtonLayout.findViewById(R.id.firstFloorButton);

        /** Get list of locations **/
        locationList = locationsDAO.getAllLocations();

        /** Set up gesture detectors **/
        scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener(view));

        /** Set initial positions of location pins **/
        setInitialPinPositions(view);

        /** Set click listeners for all first floor pins **/
        setClickListeners(view);

        /*** Transition to Ground Floor ***/
        groundFloorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.setClickedValue(2);
                viewModel.setLastClickedValue(2);
            }
        });
        firstFloorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Already on the first floor!", Toast.LENGTH_SHORT).show();
            }
        });

        /** Set touch listener for mapImageView **/
        mapImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scaleGestureDetector.onTouchEvent(event);

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = event.getX();
                        lastY = event.getY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        /** Self **/
                        // Difference between the new tapped cords and last tapped cords
                        float deltaX = event.getX() - lastX;
                        float deltaY = event.getY() - lastY;

                        // Move the map a distance equal to the difference in both directions
                        matrix.postTranslate(deltaX, deltaY);
                        // Register the final tapped point in the screen before removing finger
                        lastX = event.getX();
                        lastY = event.getY();
                        updatePinPositions(view);
                        break;
                }
                mapImageView.setImageMatrix(matrix);

                // Appear and disappear texts based on scale factor
                if (scaleFactor < 0.3) {
                    disappearLocationPinText(view);
                }
                else {
                    appearLocationPinText(view);
                }
                return true;
            }
        });
        return view;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        View mView;
        public ScaleListener(View view) {
            mView = view;
        }
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.d("TAG", "onScale: im here");
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.2f, Math.min(scaleFactor, 1.0f));
            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();

            matrix.postScale(scaleFactor, scaleFactor, focusX, focusY);

            // Adjust translation to ensure focus point remains stationary
            float focusShiftX = (1 - scaleFactor) * focusX;
            float focusShiftY = (1 - scaleFactor) * focusY;
            matrix.setScale(scaleFactor, scaleFactor, focusShiftX, focusShiftY);

            // Update button positions
            updatePinPositions(mView);

            mapImageView.setImageMatrix(matrix);
            return true;
        }
    }

    /** OLD onScale method **/
//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            scaleFactor *= detector.getScaleFactor();
//            scaleFactor = Math.max(0.2f, Math.min(scaleFactor, 1.0f));
//            matrix.setScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
//            mapImageView.setImageMatrix(matrix);
//            return true;
//
//        }
//    }

    private void setClickListeners(View view) {
        for (int i = 13; i < locationList.size(); i++) {
            Locations location = locationList.get(i);
            ImageButton button = view.findViewById(location.getImageButtonId());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(location);
                }
            });
        }
    }

    private void showDialog(Locations pin) {
        dialog.setContentView(R.layout.fragment_description);
        TextView locationName = dialog.findViewById(R.id.name);
        ImageView locationImage = dialog.findViewById(R.id.image);
        TouchImageView locationZoomableImage = dialog.findViewById(R.id.imageZoomable);
        TextView locationDescription = dialog.findViewById(R.id.description);
        Button button = dialog.findViewById(R.id.button);

        locationName.setText(pin.getName());
        locationImage.setImageResource(pin.getImageResourceId());
        locationZoomableImage.setImageResource(pin.getImageResourceId());
        locationDescription.setText(pin.getDescription());

        // Disappear zoomable image initially
        locationZoomableImage.setVisibility(View.GONE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button.getText().equals("+")) {
                    locationImage.setVisibility(View.GONE);
                    locationZoomableImage.setVisibility(View.VISIBLE);
                    button.setText("-");
                }
                else if (button.getText().equals("-")) {
                    locationImage.setVisibility(View.VISIBLE);
                    locationZoomableImage.setVisibility(View.GONE);
                    button.setText("+");
                }
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void disappearLocationPinText(View view)  {
        for (int i = 13; i < locationList.size(); i++) {
            Locations location = locationList.get(i);
            TextView text = view.findViewById(location.getTextViewId());
            text.setVisibility(View.INVISIBLE);
        }
    }

    private void appearLocationPinText(View view)  {
        for (int i = 13; i < locationList.size(); i++) {
            Locations location = locationList.get(i);
            TextView text = view.findViewById(location.getTextViewId());
            text.setVisibility(View.VISIBLE);
        }
    }

    private void setInitialPinPositions(View view) {
        float[] allValues = new float[9];
        matrix.getValues(allValues);
        float bx = allValues[2];
        float by = allValues[5];
        for (int i = 13; i < locationList.size(); i++) {
            Locations location = locationList.get(i);

            ConstraintLayout layout = view.findViewById(location.getLayoutId());
            /** Measure layout width and height to use in pinpoint location **/
            layout.measure(0, 0);
            int layoutWidth = layout.getMeasuredWidth();
            int layoutHeight = layout.getMeasuredHeight();

            layout.setX(bx + location.getXCoordinate() * scaleFactor - layoutWidth / 2);
            layout.setY(by + location.getYCoordinate() * scaleFactor - layoutHeight / 2);

            Log.d("FIRST FLOOR Matrix cords", String.valueOf(matrix));
            Log.d("FIRST FLOOR ScaleFactor", String.valueOf(scaleFactor));
            Log.d("FIRST FLOOR Layout cords", layout.getX() + " " + layout.getY());
        }
    }

    private void updatePinPositions(View view) {
        float[] allValues = new float[9];
        matrix.getValues(allValues);
        float bx = allValues[2];
        float by = allValues[5];
        for (int i = 13; i < locationList.size(); i++) {
            Locations location = locationList.get(i);

            ConstraintLayout layout = view.findViewById(location.getLayoutId());

            layout.setX(bx + location.getXCoordinate() * scaleFactor - layout.getWidth() / 2);
            layout.setY(by + location.getYCoordinate() * scaleFactor - layout.getHeight() / 2);

            Log.d("FIRST FLOOR Matrix cords", String.valueOf(matrix));
            Log.d("FIRST FLOOR ScaleFactor", String.valueOf(scaleFactor));
            Log.d("FIRST FLOOR Layout cords", layout.getX() + " " + layout.getY());
        }
    }
}