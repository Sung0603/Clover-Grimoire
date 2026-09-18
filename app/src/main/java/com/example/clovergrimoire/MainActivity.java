package com.example.clovergrimoire;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvCharacters;
    private CharacterAdapter adapter;
    private List<Character> characterList = new ArrayList<>();
    private CharacterViewModel characterViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View mainView = findViewById(R.id.main_layout);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        rvCharacters = findViewById(R.id.rv_characters);
        Button btnCreate = findViewById(R.id.btn_create_new);

        setupRecyclerView();

        characterViewModel = new ViewModelProvider(this).get(CharacterViewModel.class);

        characterViewModel.getAllCharacters().observe(this, characters -> {
            if (characters != null) {
                characterList.clear();
                characterList.addAll(characters);
                adapter.notifyDataSetChanged();
            }
        });

        btnCreate.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateCharacterActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        adapter = new CharacterAdapter(
                characterList,
                character -> { // Clique simples: abre a ficha
                    Intent intent = new Intent(MainActivity.this, CharacterDetailActivity.class);
                    intent.putExtra("EXTRA_ID", character.getId());
                    startActivity(intent);
                },
                character -> { // Clique longo: confirma exclusão do personagem
                    confirmDeleteCharacter(character);
                }
        );

        rvCharacters.setLayoutManager(new LinearLayoutManager(this));
        rvCharacters.setAdapter(adapter);
    }

    private void confirmDeleteCharacter(Character character) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Personagem")
                .setMessage("Deseja realmente excluir o personagem " + character.getName() + "? Esta ação não pode ser desfeita.")
                .setPositiveButton("Excluir", (dialog, which) -> {
                    characterViewModel.delete(character);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}