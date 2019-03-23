package com.example.justeating;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface EstablishmentDao {

    @Insert
    void insertEstablishment(Establishment establishment);

    @Query("SELECT * FROM establishment")
    List<Establishment> retrieveAll();

    @Query("SELECT id FROM establishment")
    List<Integer> retrieveIds();

    @Delete
    void removeEstablishment(Establishment establishment);

}