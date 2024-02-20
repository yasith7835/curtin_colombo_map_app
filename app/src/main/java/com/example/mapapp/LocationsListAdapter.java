package com.example.mapapp;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LocationsListAdapter extends RecyclerView.Adapter<LocationsListVH> {
    private ArrayList<Locations> locationsList;
    private Dialog dialog;
    public LocationsListAdapter(ArrayList<Locations> locationsList) {
        this.locationsList = locationsList;
    }

    @NonNull
    @Override
    public LocationsListVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.location_item, parent, false);
        LocationsListVH dataHolder = new LocationsListVH(view);
        dialog = new Dialog(parent.getContext());
        return dataHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LocationsListVH holder, int position) {
        Locations singleData = locationsList.get(position);
        holder.image.setImageResource(singleData.getImageResourceId());
        holder.button.setText(singleData.getName());

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(singleData);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationsList.size();
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
}
