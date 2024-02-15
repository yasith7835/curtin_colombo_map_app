package com.example.mapapp;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Locations.class}, version = 1)
public abstract class LocationsDataBase extends RoomDatabase {
    public abstract LocationsDAO locationsDAO();
}
