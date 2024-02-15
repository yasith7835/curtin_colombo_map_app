package com.example.mapapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface LocationsDAO {
    @Insert
    void insert(Locations locations);
    @Update
    void update(Locations locations);
    @Delete
    void delete(Locations locations);
    @Query("SELECT * FROM locations")
    List<Locations> getAllLocations();
    @Query("SELECT * FROM locations WHERE id = :locationID")
    Locations getLocationByID(long locationID);

}
