package com.example.clovergrimoire;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

public class CreateCharacterActivity extends AppCompatActivity {

    private EditText etName;
    private Spinner spRace, spHierarchy, spMagic;
    private ImageView ivAvatar;
    private Button btnSelectImage, btnSaveCharacter;
    private Uri selectedImageUri = null;

    private CharacterViewModel characterViewModel;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    if (ivAvatar != null) {
                        ivAvatar.setImageURI(uri);
                    }

                    // Garante que o app mantenha permissão de acesso à imagem salva no SQLite
                    try {
                        getContentResolver().takePersistableUriPermission(
                                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_character);

        // Inicializa o ViewModel do Room
        characterViewModel = new ViewModelProvider(this).get(CharacterViewModel.class);

        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        initViews();
        setupSpinners();
    }

    private void initViews() {
        etName = findViewById(R.id.et_character_name);
        spRace = findViewById(R.id.sp_race);
        spHierarchy = findViewById(R.id.sp_hierarchy);
        spMagic = findViewById(R.id.sp_magic);
        ivAvatar = findViewById(R.id.iv_avatar_preview);
        btnSelectImage = findViewById(R.id.btn_select_image);
        btnSaveCharacter = findViewById(R.id.btn_save_character);

        if (btnSelectImage != null) {
            btnSelectImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        }

        if (btnSaveCharacter != null) {
            btnSaveCharacter.setOnClickListener(v -> saveAndReturnCharacter());
        }
    }

    private void setupSpinners() {
        String[] races = new String[]{"Humano", "Elfo", "Anão", "Demônio", "Espírito Elemental", "Mutante"};
        String[] hierarchies = new String[]{"Camponeses", "Plebeus", "Nobreza", "Alta Nobreza", "Realeza"};

        String[] magics = new String[]{
                "Fogo", "Água", "Vento", "Terra", "Raio", "Gelo", "Névoa", "Neve", "Cinzas", "Nuvem", "Planta", "Madeira",
                "Metal", "Cristal", "Vidro", "Borracha", "Espelho", "Tinta", "Cera", "Lodo", "Fumaça", "Sucata",
                "Sangue", "Corpo", "Cabelo", "Ossos", "Pele", "Metabolismo", "Cura", "Comida",
                "Luz", "Sombra", "Gravidade", "Espaço", "Explosão", "Tremor", "Vácuo", "Maldição",
                "Ilusão", "Hipnose", "Emoções", "Memória", "Linguagem", "Destino", "Causa e Efeito", "Percepção", "Controle de Marionetes",
                "Barreira", "Fortalecimento", "Invisibilidade", "Teleporte", "Voo", "Comunicação", "Invocação", "Criação de Objetos", "Fios", "Portais",
                "Magia de Espíritos", "Magia de Elfos", "Magia de Demônio", "Magia de Alma", "Magia de Mutação", "Magia de Cópia", "Magia de Livro", "Antimagia", "Tempo"
        };

        ArrayAdapter<String> raceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, races);
        ArrayAdapter<String> hierarchyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, hierarchies);
        ArrayAdapter<String> magicAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, magics);

        if (spRace != null) spRace.setAdapter(raceAdapter);
        if (spHierarchy != null) spHierarchy.setAdapter(hierarchyAdapter);
        if (spMagic != null) spMagic.setAdapter(magicAdapter);
    }

    private void saveAndReturnCharacter() {
        String name = etName != null ? etName.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Por favor, digite o nome do personagem.", Toast.LENGTH_SHORT).show();
            return;
        }

        String race = spRace != null && spRace.getSelectedItem() != null ? spRace.getSelectedItem().toString() : "Humano";
        String hierarchy = spHierarchy != null && spHierarchy.getSelectedItem() != null ? spHierarchy.getSelectedItem().toString() : "Plebeus";
        String magic = spMagic != null && spMagic.getSelectedItem() != null ? spMagic.getSelectedItem().toString() : "Nenhum";

        // 1. Cria a nova instância do personagem
        Character character = new Character(name, race, hierarchy, magic, 1, selectedImageUri);

        // 2. Insere no banco SQLite através do Room
        characterViewModel.insert(character);

        Toast.makeText(this, "Personagem " + name + " criado e salvo!", Toast.LENGTH_SHORT).show();

        // 3. Finaliza a Activity (A MainActivity atualizará a lista sozinha via LiveData)
        finish();
    }
}