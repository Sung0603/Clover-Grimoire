
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
import android.app.AlertDialog;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.io.IOException;

public class CreateCharacterActivity extends AppCompatActivity {

    private EditText etName;
    private Spinner spRace, spHierarchy, spMagic;
    private ImageView ivAvatar;
    private Button btnSelectImage, btnSaveCharacter;

    private Uri selectedImageUri = null;
    private Uri cameraImageUri = null;

    private CharacterViewModel characterViewModel;

    // Selecionar imagem da galeria
    private final ActivityResultLauncher<String[]> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.OpenDocument(),
                    uri -> {

                        if (uri != null) {

                            selectedImageUri = uri;

                            if (ivAvatar != null) {
                                ivAvatar.setImageURI(uri);
                            }

                            // Mantém a permissão de acesso à imagem
                            try {
                                getContentResolver().takePersistableUriPermission(
                                        uri,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                );
                            } catch (SecurityException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );

    // Abrir câmera e capturar fotografia
    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.TakePicture(),
                    success -> {

                        if (success && cameraImageUri != null) {

                            selectedImageUri = cameraImageUri;

                            if (ivAvatar != null) {
                                ivAvatar.setImageURI(selectedImageUri);
                            }

                            Toast.makeText(
                                    this,
                                    "Foto capturada com sucesso!",
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else {

                            Toast.makeText(
                                    this,
                                    "Captura cancelada.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_character);

        // Inicializa o ViewModel do Room
        characterViewModel = new ViewModelProvider(this)
                .get(CharacterViewModel.class);

        View rootView = findViewById(android.R.id.content);

        if (rootView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {

                Insets systemBars = insets.getInsets(
                        WindowInsetsCompat.Type.systemBars()
                );

                v.setPadding(
                        systemBars.left,
                        systemBars.top,
                        systemBars.right,
                        systemBars.bottom
                );

                return insets;
            });
        }

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                }
        );

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

        // Abre o menu de escolha entre câmera e galeria
        if (btnSelectImage != null) {
            btnSelectImage.setOnClickListener(v -> showImageOptions());
        }

        if (btnSaveCharacter != null) {
            btnSaveCharacter.setOnClickListener(
                    v -> saveAndReturnCharacter()
            );
        }
    }

    // Menu para escolher a origem da imagem
    private void showImageOptions() {

        String[] options = {
                "Tirar foto",
                "Selecionar imagem da galeria"
        };

        new AlertDialog.Builder(this)
                .setTitle("Foto do personagem")
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {
                        openCamera();

                    } else if (which == 1) {
                        imagePickerLauncher.launch(
                                new String[]{"image/*"}
                        );
                    }

                })
                .show();
    }

    // Preparar arquivo e abrir a câmera
    private void openCamera() {

        try {

            File directory = new File(
                    getFilesDir(),
                    "character_photos"
            );

            if (!directory.exists() && !directory.mkdirs()) {
                throw new IOException(
                        "Não foi possível criar a pasta."
                );
            }

            File photoFile = File.createTempFile(
                    "avatar_" + System.currentTimeMillis(),
                    ".jpg",
                    directory
            );

            cameraImageUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    photoFile
            );

            cameraLauncher.launch(cameraImageUri);

        } catch (IOException e) {

            Toast.makeText(
                    this,
                    "Erro ao preparar a câmera.",
                    Toast.LENGTH_SHORT
            ).show();

            e.printStackTrace();
        }
    }

    private void setupSpinners() {

        String[] races = {
                "Humano", "Elfo", "Anão", "Demônio",
                "Espírito Elemental", "Mutante"
        };

        String[] hierarchies = {
                "Camponeses", "Plebeus", "Nobreza",
                "Alta Nobreza", "Realeza"
        };

        String[] magics = {
                "Fogo", "Água", "Vento", "Terra", "Raio",
                "Gelo", "Névoa", "Neve", "Cinzas", "Nuvem",
                "Planta", "Madeira", "Metal", "Cristal",
                "Vidro", "Borracha", "Espelho", "Tinta",
                "Cera", "Lodo", "Fumaça", "Sucata",
                "Sangue", "Corpo", "Cabelo", "Ossos",
                "Pele", "Metabolismo", "Cura", "Comida",
                "Luz", "Sombra", "Gravidade", "Espaço",
                "Explosão", "Tremor", "Vácuo", "Maldição",
                "Ilusão", "Hipnose", "Emoções", "Memória",
                "Linguagem", "Destino", "Causa e Efeito",
                "Percepção", "Controle de Marionetes",
                "Barreira", "Fortalecimento", "Invisibilidade",
                "Teleporte", "Voo", "Comunicação", "Invocação",
                "Criação de Objetos", "Fios", "Portais",
                "Magia de Espíritos", "Magia de Elfos",
                "Magia de Demônio", "Magia de Alma",
                "Magia de Mutação", "Magia de Cópia",
                "Magia de Livro", "Antimagia", "Tempo"
        };

        ArrayAdapter<String> raceAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                races
        );

        ArrayAdapter<String> hierarchyAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                hierarchies
        );

        ArrayAdapter<String> magicAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                magics
        );

        if (spRace != null) {
            spRace.setAdapter(raceAdapter);
        }

        if (spHierarchy != null) {
            spHierarchy.setAdapter(hierarchyAdapter);
        }

        if (spMagic != null) {
            spMagic.setAdapter(magicAdapter);
        }
    }

    private void saveAndReturnCharacter() {

        String name = etName != null
                ? etName.getText().toString().trim()
                : "";

        if (TextUtils.isEmpty(name)) {

            Toast.makeText(
                    this,
                    "Por favor, digite o nome do personagem.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String race = spRace != null
                && spRace.getSelectedItem() != null
                ? spRace.getSelectedItem().toString()
                : "Humano";

        String hierarchy = spHierarchy != null
                && spHierarchy.getSelectedItem() != null
                ? spHierarchy.getSelectedItem().toString()
                : "Plebeus";

        String magic = spMagic != null
                && spMagic.getSelectedItem() != null
                ? spMagic.getSelectedItem().toString()
                : "Nenhum";

        // Cria a nova instância do personagem
        Character character = new Character(
                name,
                race,
                hierarchy,
                magic,
                1,
                selectedImageUri
        );

        // Insere no banco SQLite através do Room
        characterViewModel.insert(character);

        Toast.makeText(
                this,
                "Personagem " + name + " criado e salvo!",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }
}
