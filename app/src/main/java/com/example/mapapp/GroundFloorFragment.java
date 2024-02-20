package com.example.mapapp;

import android.app.Dialog;
import android.content.res.AssetManager;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class GroundFloorFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public GroundFloorFragment() {
        // Required empty public constructor
    }
    public static GroundFloorFragment newInstance(String param1, String param2) {
        GroundFloorFragment fragment = new GroundFloorFragment();
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
    private List<Locations> locationList;
    private Dialog dialog;
    private LocationsDAO locationsDAO;
    private float scaleFactor = 1.0f;
    private float lastX, lastY;
    private Matrix matrix = new Matrix();
    private ScaleGestureDetector scaleGestureDetector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ground_floor, container, false);
        locationsDAO = LocationsDBInstance.getDataBase(getActivity().getApplicationContext()).locationsDAO();
        LiveDataClass viewModel = new ViewModelProvider(getActivity()).get(LiveDataClass.class);

        mapImageView = view.findViewById(R.id.mapImageView);
        dialog = new Dialog(getContext());
        mapImageView.setImageMatrix(matrix);

        LinearLayout floorButtonLayout = view.findViewById(R.id.floorButtonLayout);
        Button groundFloorButton = floorButtonLayout.findViewById(R.id.groundFloorButton);
        Button firstFloorButton = floorButtonLayout.findViewById(R.id.firstFloorButton);

        /** Check if the database has already been created **/
        if (!isDataBaseCreated()) {
            // Create location pins with hardcoded values
            createLocationDatabase();
        }

        /** Get list of locations **/
        locationList = locationsDAO.getAllLocations();

        /** Set up gesture detectors **/
        scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener(view));

        /** Set initial positions of location pins **/
        setInitialPinPositions(view);

        /** Set click listeners for all ground floor pins **/
        setClickListeners(view);

        /*** Transition to First Floor ***/
        firstFloorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setClickedValue(1);
                viewModel.setLastClickedValue(1);
            }
        });
        groundFloorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Already on the ground floor!", Toast.LENGTH_SHORT).show();
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
                /** Blackbox ai **/
//                for (int i = 0; i <= 12; i++) {
//                    Locations location = locationList.get(i);
//                    float x = location.getXCoordinate();
//                    float y = location.getYCoordinate();
//                    float[] point = new float[] {x, y};
//                    matrix.mapPoints(point);
//                    location.setXCoordinate(point[0]);
//                    location.setYCoordinate(point[1]);
//                    ConstraintLayout layout = view.findViewById(location.getLayoutId());
//                    layout.setX(point[0] - layout.getWidth() / 2);
//                    layout.setY(point[1] - layout.getHeight() / 2);
//                }


//                updatePinPositions();
//                float[] allValues = new float[9];
//                matrix.getValues(allValues);
//                float bx = allValues[2];
//                float by = allValues[5];
//                lobbyConstraint.setX(bx + 400 * scaleFactor - lobbyConstraint.getWidth() / 2);
//                lobbyConstraint.setY(by + 600 * scaleFactor - lobbyConstraint.getHeight() / 2);
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

    /** Used only in GroundFloorFragment **/
    private void createLocationDatabase() {
        try {
            // Read CSV file from assets folder
            AssetManager assetManager = getActivity().getAssets();
            InputStream inputStream = assetManager.open("locations.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into individual values
                String[] values = line.split("/");
                // Create a new Locations object and populate its attributes
                Locations location = new Locations();

                int layoutResourceId = getResources().getIdentifier(values[1], "id", getActivity().getPackageName());
                int imageButtonResourceId = getResources().getIdentifier(values[2], "id", getActivity().getPackageName());
                int textViewResourceId = getResources().getIdentifier(values[3], "id", getActivity().getPackageName());
                int imageResourceId = getResources().getIdentifier(values[5], "drawable", getActivity().getPackageName());

                location.setName(values[0]);
                location.setLayoutId(layoutResourceId);
                location.setImageButtonId(imageButtonResourceId);
                location.setTextViewId(textViewResourceId);
                location.setDescription(values[4]);
                location.setImageResourceId(imageResourceId);
                location.setXCoordinate(Float.parseFloat(values[6]));
                location.setYCoordinate(Float.parseFloat(values[7]));
                locationsDAO.insert(location);
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /** Used only in GroundFloorFragment **/
    private boolean isDataBaseCreated() {
        // Check if the database exists or has been populated
        boolean isCreated = false;
        if (locationsDAO.getAllLocations().size() > 0) {
            isCreated = true;
        }
        return isCreated;
    }

    private void setClickListeners(View view) {
        for (int i = 0; i <= 12; i++) {
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
        TextView locationDescription = dialog.findViewById(R.id.description);

        locationName.setText(pin.getName());
        locationImage.setImageResource(pin.getImageResourceId());
        locationDescription.setText(pin.getDescription());

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void disappearLocationPinText(View view)  {
        for (int i = 0; i <= 12; i++) {
            Locations location = locationList.get(i);
            TextView text = view.findViewById(location.getTextViewId());
            text.setVisibility(View.INVISIBLE);
        }
    }

    private void appearLocationPinText(View view)  {
        for (int i = 0; i <= 12; i++) {
            Locations location = locationList.get(i);
            TextView text = view.findViewById(location.getTextViewId());
            text.setVisibility(View.VISIBLE);
        }
    }

    /** Blackbox ai **/
//    private void calculateRelativePositions(View view) {
//        for (int i = 0; i <= 12; i++) {
//            Locations location = locationList.get(i);
//            ConstraintLayout layout = view.findViewById(location.getLayoutId());
//            int[] layoutLocation = new int[2];
//            layout.getLocationOnScreen(layoutLocation);
//            float x = layoutLocation[0] - mapImageView.getLeft();
//            float y = layoutLocation[1] - mapImageView.getTop();
//            location.setXCoordinate(x);
//            location.setYCoordinate(y);
//        }
//    }

    private void setInitialPinPositions(View view) {
        float[] allValues = new float[9];
        matrix.getValues(allValues);
        float bx = allValues[2];
        float by = allValues[5];
        for (int i = 0; i <= 12; i++) {
            Locations location = locationList.get(i);

            ConstraintLayout layout = view.findViewById(location.getLayoutId());
            /** Measure layout width and height to use in pinpoint location **/
            layout.measure(0, 0);
            int layoutWidth = layout.getMeasuredWidth();
            int layoutHeight = layout.getMeasuredHeight();

            layout.setX(bx + location.getXCoordinate() * scaleFactor - layoutWidth / 2);
            layout.setY(by + location.getYCoordinate() * scaleFactor - layoutHeight / 2);

            Log.d("GROUND FLOOR Matrix cords", String.valueOf(matrix));
            Log.d("GROUND FLOOR ScaleFactor", String.valueOf(scaleFactor));
            Log.d("GROUND FLOOR Layout cords", layout.getX() + " " + layout.getY());
        }
    }

    private void updatePinPositions(View view) {
        float[] allValues = new float[9];
        matrix.getValues(allValues);
        float bx = allValues[2];
        float by = allValues[5];
        for (int i = 0; i <= 12; i++) {
            Locations location = locationList.get(i);

            ConstraintLayout layout = view.findViewById(location.getLayoutId());

            layout.setX(bx + location.getXCoordinate() * scaleFactor - layout.getWidth() / 2);
            layout.setY(by + location.getYCoordinate() * scaleFactor - layout.getHeight() / 2);

            Log.d("GROUND FLOOR Matrix cords", String.valueOf(matrix));
            Log.d("GROUND FLOOR ScaleFactor", String.valueOf(scaleFactor));
            Log.d("GROUND FLOOR Layout cords", layout.getX() + " " + layout.getY());
        }
    }
}