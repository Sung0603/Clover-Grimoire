package com.example.clovergrimoire;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class CharacterViewModel extends AndroidViewModel {

    private final CharacterRepository repository;
    private final LiveData<List<Character>> allCharacters;

    public CharacterViewModel(@NonNull Application application) {
        super(application);
        repository = new CharacterRepository(application);
        allCharacters = repository.getAllCharacters();
    }

    public LiveData<List<Character>> getAllCharacters() {
        return allCharacters;
    }

    public void insert(Character character) {
        repository.insert(character);
    }

    public void update(Character character) {
        repository.update(character);
    }

    public void delete(Character character) {
        repository.delete(character);
    }
}