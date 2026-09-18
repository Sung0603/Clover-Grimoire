package com.example.clovergrimoire;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

public class CharacterRepository {

    private final CharacterDao characterDao;
    private final LiveData<List<Character>> allCharacters;

    public CharacterRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        characterDao = db.characterDao();
        allCharacters = characterDao.getAllCharacters();
    }

    public LiveData<List<Character>> getAllCharacters() {
        return allCharacters;
    }

    public void insert(Character character) {
        AppDatabase.databaseWriteExecutor.execute(() -> characterDao.insert(character));
    }

    public void update(Character character) {
        AppDatabase.databaseWriteExecutor.execute(() -> characterDao.update(character));
    }

    public void delete(Character character) {
        AppDatabase.databaseWriteExecutor.execute(() -> characterDao.delete(character));
    }
}