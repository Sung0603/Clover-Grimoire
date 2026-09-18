package com.example.clovergrimoire;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CharacterDao {

    @Insert
    void insert(Character character);

    @Update
    void update(Character character);

    @Delete
    void delete(Character character);

    @Query("SELECT * FROM characters ORDER BY name ASC")
    LiveData<List<Character>> getAllCharacters();
}