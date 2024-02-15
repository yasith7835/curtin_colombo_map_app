package com.example.mapapp;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "locations")
public class Locations {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = "location_name")
    private String name;
    @ColumnInfo(name = "location_layoutId")
    private int layoutId;
    @ColumnInfo(name = "location_imageButtonId")
    private int imageButtonId;
    @ColumnInfo(name = "location_textViewId")
    private int textViewId;
    @ColumnInfo(name = "location_description")
    private String description;
    @ColumnInfo(name = "location_imageResourceId")
    private int imageResourceId;
    @ColumnInfo(name = "location_xCoordinate")
    private float xCoordinate;
    @ColumnInfo(name = "location_yCoordinate")
    private float yCoordinate;

    // Accessors
    public long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getLayoutId() {
        return layoutId;
    }
    public int getImageButtonId() {
        return imageButtonId;
    }
    public int getTextViewId() {
        return textViewId;
    }
    public String getDescription() {
        return description;
    }
    public int getImageResourceId() {
        return imageResourceId;
    }
    public float getXCoordinate() {
        return xCoordinate;
    }
    public float getYCoordinate() {
        return yCoordinate;
    }

    // Mutators
    public void setId(long id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }
    public void setImageButtonId(int imageButtonId) {
        this.imageButtonId = imageButtonId;
    }
    public void setTextViewId(int textViewId) {
        this.textViewId = textViewId;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }
    public void setXCoordinate(float xCoordinate) {
        this.xCoordinate = xCoordinate;
    }
    public void setYCoordinate(float yCoordinate) {
        this.yCoordinate = yCoordinate;
    }
}
