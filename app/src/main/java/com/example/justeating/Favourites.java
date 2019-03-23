package com.example.justeating;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {Establishment.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class Favourites extends RoomDatabase {
    public abstract EstablishmentDao favouriteDao();
}
