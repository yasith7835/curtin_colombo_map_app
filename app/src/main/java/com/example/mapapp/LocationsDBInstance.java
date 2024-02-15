package com.example.mapapp;

import android.content.Context;

import androidx.room.Room;

public class LocationsDBInstance {
    private static LocationsDataBase dataBase;

    public static LocationsDataBase getDataBase(Context context) {
        if (dataBase == null) {
            dataBase = Room.databaseBuilder(context,
                    LocationsDataBase.class, "app_database")
                    .allowMainThreadQueries()
                    .build();
        }
        return dataBase;
    }
}
