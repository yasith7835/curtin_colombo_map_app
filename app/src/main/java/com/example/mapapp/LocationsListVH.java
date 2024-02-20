package com.example.mapapp;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

public class LocationsListVH extends RecyclerView.ViewHolder {
    public ShapeableImageView image;
    public Button button;
    public LocationsListVH(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.locationImage);
        button = itemView.findViewById(R.id.locationButton);
    }
}
