package com.example.clovergrimoire;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class CharacterDetailActivity extends AppCompatActivity {

    private Character activeCharacter;
    private CharacterViewModel characterViewModel;

    private TextView tvName, tvInfo, tvLevel, tvHp, tvMp, tvStamina, tvReduction, tvLimits;
    private Button btnLevelUp, btnLevelDown, btnReturnMain;
    private Button btnHpPlus, btnHpMinus, btnMpPlus, btnMpMinus, btnStaminaPlus, btnStaminaMinus;
    private Button btnAddSpell, btnAddItem, btnAddSkill;
    private LinearLayout containerAttributes, containerMp, containerSpells, containerItems, containerSkills;
    private ImageView ivDetailAvatar;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null && activeCharacter != null) {
                        try {
                            getContentResolver().takePersistableUriPermission(
                                    selectedImageUri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );
                        } catch (Exception ignored) { }

                        activeCharacter.setImageUri(selectedImageUri);
                        updateAvatarView();
                        saveToDatabase();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_detail);

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
                finishWithResult();
            }
        });

        initViews();
        loadCharacterFromIntent();
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_detail);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finishWithResult());
        }

        tvName = findViewById(R.id.tv_detail_name);
        tvInfo = findViewById(R.id.tv_detail_info);
        tvLevel = findViewById(R.id.tv_detail_level);
        tvHp = findViewById(R.id.tv_hp_display);
        tvMp = findViewById(R.id.tv_mp_display);
        tvStamina = findViewById(R.id.tv_stamina_display);
        tvReduction = findViewById(R.id.tv_damage_reduction);
        tvLimits = findViewById(R.id.tv_attribute_limits);
        ivDetailAvatar = findViewById(R.id.iv_detail_avatar);
        containerMp = findViewById(R.id.container_mp_controls);

        btnLevelUp = findViewById(R.id.btn_level_up);
        btnLevelDown = findViewById(R.id.btn_level_down);
        btnReturnMain = findViewById(R.id.btn_return_main);

        btnHpPlus = findViewById(R.id.btn_hp_plus);
        btnHpMinus = findViewById(R.id.btn_hp_minus);
        btnMpPlus = findViewById(R.id.btn_mp_plus);
        btnMpMinus = findViewById(R.id.btn_mp_minus);
        btnStaminaPlus = findViewById(R.id.btn_stamina_plus);
        btnStaminaMinus = findViewById(R.id.btn_stamina_minus);

        containerAttributes = findViewById(R.id.container_attributes);
        containerSpells = findViewById(R.id.container_spells);
        containerItems = findViewById(R.id.container_items);
        containerSkills = findViewById(R.id.container_skills);

        btnAddSpell = findViewById(R.id.btn_add_spell);
        btnAddItem = findViewById(R.id.btn_add_item);
        btnAddSkill = findViewById(R.id.btn_add_skill);

        if (ivDetailAvatar != null) {
            ivDetailAvatar.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                imagePickerLauncher.launch(intent);
            });
        }

        if (btnReturnMain != null) btnReturnMain.setOnClickListener(v -> finishWithResult());

        if (btnLevelUp != null) {
            btnLevelUp.setOnClickListener(v -> {
                if (activeCharacter != null && activeCharacter.getLevel() < 10) {
                    activeCharacter.setLevel(activeCharacter.getLevel() + 1);
                    updateUi();
                    saveToDatabase();
                }
            });
        }

        if (btnLevelDown != null) {
            btnLevelDown.setOnClickListener(v -> {
                if (activeCharacter != null && activeCharacter.getLevel() > 1) {
                    activeCharacter.setLevel(activeCharacter.getLevel() - 1);
                    updateUi();
                    saveToDatabase();
                }
            });
        }

        // Modifica Vida, Mana e Estamina gravando os valores atuais em tempo real no SQLite
        if (btnHpPlus != null) btnHpPlus.setOnClickListener(v -> { if (activeCharacter != null) { activeCharacter.modifyHp(1); updateResourceTexts(); saveToDatabase(); } });
        if (btnHpMinus != null) btnHpMinus.setOnClickListener(v -> { if (activeCharacter != null) { activeCharacter.modifyHp(-1); updateResourceTexts(); saveToDatabase(); } });
        if (btnMpPlus != null) btnMpPlus.setOnClickListener(v -> { if (activeCharacter != null) { activeCharacter.modifyMp(1); updateResourceTexts(); saveToDatabase(); } });
        if (btnMpMinus != null) btnMpMinus.setOnClickListener(v -> { if (activeCharacter != null) { activeCharacter.modifyMp(-1); updateResourceTexts(); saveToDatabase(); } });
        if (btnStaminaPlus != null) btnStaminaPlus.setOnClickListener(v -> { if (activeCharacter != null) { activeCharacter.modifyStamina(1); updateResourceTexts(); saveToDatabase(); } });
        if (btnStaminaMinus != null) btnStaminaMinus.setOnClickListener(v -> { if (activeCharacter != null) { activeCharacter.modifyStamina(-1); updateResourceTexts(); saveToDatabase(); } });

        if (btnAddSpell != null) btnAddSpell.setOnClickListener(v -> showAddSpellDialog());
        if (btnAddItem != null) btnAddItem.setOnClickListener(v -> showAddItemDialog());
        if (btnAddSkill != null) btnAddSkill.setOnClickListener(v -> showAddSkillDialog());
    }

    private void loadCharacterFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            long id = intent.getLongExtra("EXTRA_ID", -1);

            characterViewModel.getAllCharacters().observe(this, characters -> {
                if (characters != null) {
                    for (Character c : characters) {
                        if (c.getId() == id) {
                            activeCharacter = c;
                            updateUi();
                            break;
                        }
                    }
                }
            });
        }
    }

    private void confirmDeleteThisCharacter() {
        if (activeCharacter == null) return;
        new AlertDialog.Builder(this)
                .setTitle("Excluir Ficha")
                .setMessage("Tem certeza de que deseja apagar " + activeCharacter.getName() + " permanentemente?")
                .setPositiveButton("Excluir", (dialog, which) -> {
                    characterViewModel.delete(activeCharacter);
                    Toast.makeText(this, "Personagem excluído!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void saveToDatabase() {
        if (activeCharacter != null && characterViewModel != null) {
            characterViewModel.update(activeCharacter);
        }
    }

    private void updateUi() {
        if (activeCharacter == null) return;

        if (tvName != null) tvName.setText(activeCharacter.getName());
        if (tvInfo != null) {
            String infoText = activeCharacter.getRace() + " | " + activeCharacter.getHierarchy();
            if (!activeCharacter.isCampones()) {
                infoText += " | Magia: " + activeCharacter.getMagicType();
            }
            tvInfo.setText(infoText);
        }
        if (tvLevel != null) tvLevel.setText("Nível: " + activeCharacter.getLevel());

        updateAvatarView();
        updateResourceTexts();
        setupAttributeList();
        renderSpells();
        renderItems();
        renderSkills();
    }

    private void updateAvatarView() {
        if (ivDetailAvatar == null || activeCharacter == null) return;

        if (activeCharacter.getImageUri() != null) {
            try {
                getContentResolver().takePersistableUriPermission(
                        activeCharacter.getImageUri(),
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );
            } catch (Exception ignored) { }

            try {
                ivDetailAvatar.setImageURI(null);
                ivDetailAvatar.setImageURI(activeCharacter.getImageUri());
            } catch (Exception e) {
                ivDetailAvatar.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            ivDetailAvatar.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    private void renderSpells() {
        if (containerSpells == null || activeCharacter == null) return;
        containerSpells.removeAllViews();

        for (Character.SpellsItem spell : activeCharacter.getSpellsList()) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(16, 16, 16, 16);

            TextView title = new TextView(this);
            title.setText("✦ " + spell.name);
            title.setTextSize(16);
            title.setTypeface(null, android.graphics.Typeface.BOLD);

            TextView details = new TextView(this);
            details.setVisibility(View.GONE);
            details.setText("- Descrição: " + spell.description + "\n" +
                    "- Tipo: " + spell.type + "\n" +
                    "- Gasto: " + spell.cost + "\n" +
                    "- Alcance: " + spell.range + "\n" +
                    "- Efeito: " + spell.effect);
            details.setPadding(16, 8, 0, 0);

            title.setOnClickListener(v -> {
                if (details.getVisibility() == View.VISIBLE) {
                    details.setVisibility(View.GONE);
                } else {
                    details.setVisibility(View.VISIBLE);
                }
            });

            card.addView(title);
            card.addView(details);
            containerSpells.addView(card);
        }
    }

    private void renderItems() {
        if (containerItems == null || activeCharacter == null) return;
        containerItems.removeAllViews();

        for (Character.EquipmentItem item : activeCharacter.getItemsList()) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(16, 16, 16, 16);

            TextView title = new TextView(this);
            title.setText("⚔ " + item.name);
            title.setTextSize(16);
            title.setTypeface(null, android.graphics.Typeface.BOLD);

            TextView details = new TextView(this);
            details.setVisibility(View.GONE);
            details.setText("- Descrição: " + item.description + "\n" +
                    "- Alcance: " + item.range + "\n" +
                    "- Efeito: " + item.effect);
            details.setPadding(16, 8, 0, 0);

            title.setOnClickListener(v -> {
                if (details.getVisibility() == View.VISIBLE) {
                    details.setVisibility(View.GONE);
                } else {
                    details.setVisibility(View.VISIBLE);
                }
            });

            card.addView(title);
            card.addView(details);
            containerItems.addView(card);
        }
    }

    private void renderSkills() {
        if (containerSkills == null || activeCharacter == null) return;
        containerSkills.removeAllViews();

        for (Character.SkillItem skill : activeCharacter.getSkillsList()) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(16, 16, 16, 16);

            TextView title = new TextView(this);
            title.setText("★ " + skill.name);
            title.setTextSize(16);
            title.setTypeface(null, android.graphics.Typeface.BOLD);

            TextView details = new TextView(this);
            details.setVisibility(View.GONE);
            details.setText("- Descrição: " + skill.description + "\n" +
                    "- Efeito: " + skill.effect + "\n" +
                    "- Tipo: " + skill.type);
            details.setPadding(16, 8, 0, 0);

            title.setOnClickListener(v -> {
                if (details.getVisibility() == View.VISIBLE) {
                    details.setVisibility(View.GONE);
                } else {
                    details.setVisibility(View.VISIBLE);
                }
            });

            card.addView(title);
            card.addView(details);
            containerSkills.addView(card);
        }
    }

    private void showAddSpellDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        final EditText etName = new EditText(this); etName.setHint("Nome"); layout.addView(etName);
        final EditText etDesc = new EditText(this); etDesc.setHint("Descrição"); layout.addView(etDesc);
        final EditText etType = new EditText(this); etType.setHint("Tipo"); layout.addView(etType);
        final EditText etCost = new EditText(this); etCost.setHint("Gasto"); layout.addView(etCost);
        final EditText etRange = new EditText(this); etRange.setHint("Alcance"); layout.addView(etRange);
        final EditText etEffect = new EditText(this); etEffect.setHint("Efeito"); layout.addView(etEffect);

        new AlertDialog.Builder(this)
                .setTitle("Adicionar Feitiço")
                .setView(layout)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    Character.SpellsItem s = new Character.SpellsItem();
                    s.name = etName.getText().toString();
                    s.description = etDesc.getText().toString();
                    s.type = etType.getText().toString();
                    s.cost = etCost.getText().toString();
                    s.range = etRange.getText().toString();
                    s.effect = etEffect.getText().toString();
                    activeCharacter.getSpellsList().add(s);
                    renderSpells();
                    saveToDatabase();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showAddItemDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        final EditText etName = new EditText(this); etName.setHint("Nome"); layout.addView(etName);
        final EditText etDesc = new EditText(this); etDesc.setHint("Descrição"); layout.addView(etDesc);
        final EditText etRange = new EditText(this); etRange.setHint("Alcance"); layout.addView(etRange);
        final EditText etEffect = new EditText(this); etEffect.setHint("Efeito"); layout.addView(etEffect);

        new AlertDialog.Builder(this)
                .setTitle("Adicionar Item")
                .setView(layout)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    Character.EquipmentItem item = new Character.EquipmentItem();
                    item.name = etName.getText().toString();
                    item.description = etDesc.getText().toString();
                    item.range = etRange.getText().toString();
                    item.effect = etEffect.getText().toString();
                    activeCharacter.getItemsList().add(item);
                    renderItems();
                    saveToDatabase();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showAddSkillDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        final EditText etName = new EditText(this); etName.setHint("Nome"); layout.addView(etName);
        final EditText etDesc = new EditText(this); etDesc.setHint("Descrição"); layout.addView(etDesc);
        final EditText etEffect = new EditText(this); etEffect.setHint("Efeito"); layout.addView(etEffect);
        final EditText etType = new EditText(this); etType.setHint("Tipo"); layout.addView(etType);

        new AlertDialog.Builder(this)
                .setTitle("Adicionar Habilidade")
                .setView(layout)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    Character.SkillItem sk = new Character.SkillItem();
                    sk.name = etName.getText().toString();
                    sk.description = etDesc.getText().toString();
                    sk.effect = etEffect.getText().toString();
                    sk.type = etType.getText().toString();
                    activeCharacter.getSkillsList().add(sk);
                    renderSkills();
                    saveToDatabase();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void updateResourceTexts() {
        if (activeCharacter == null) return;

        if (tvHp != null) tvHp.setText("VIDA: " + activeCharacter.getCurrentHp() + "/" + activeCharacter.getMaxHp());
        if (tvStamina != null) tvStamina.setText("ESTAMINA: " + activeCharacter.getCurrentStamina() + "/" + activeCharacter.getMaxStamina());

        if (activeCharacter.isCampones()) {
            if (tvMp != null) tvMp.setVisibility(View.GONE);
            if (containerMp != null) containerMp.setVisibility(View.GONE);
        } else {
            if (tvMp != null) {
                tvMp.setVisibility(View.VISIBLE);
                tvMp.setText("MANA: " + activeCharacter.getCurrentMp() + "/" + activeCharacter.getMaxMp());
            }
            if (containerMp != null) containerMp.setVisibility(View.VISIBLE);
        }

        if (tvReduction != null) tvReduction.setText("Redução Passiva de Dano (Defesa / 2): -" + activeCharacter.getDamageReduction() + " pts");

        int maxAttr = getAttributeCapForLevel(activeCharacter.getLevel());
        if (tvLimits != null) tvLimits.setText("Limite por Atributo neste nível: " + maxAttr);
    }

    private int getAttributeCapForLevel(int level) {
        switch (level) {
            case 1: return 6;
            case 2: return 7;
            case 3: return 8;
            case 4: return 9;
            case 5: return 10;
            case 6: return 11;
            case 7: return 13;
            case 8: return 15;
            case 9: return 17;
            case 10: return 20;
            default: return 6;
        }
    }

    private void setupAttributeList() {
        if (containerAttributes == null || activeCharacter == null) return;

        containerAttributes.removeAllViews();

        addAttributeRow("Agilidade", activeCharacter.getAgilidade(), val -> activeCharacter.setAgilidade(val));
        addAttributeRow("Carisma", activeCharacter.getCarisma(), val -> activeCharacter.setCarisma(val));
        addAttributeRow("Corpo", activeCharacter.getCorpo(), val -> activeCharacter.setCorpo(val));
        addAttributeRow("Defesa", activeCharacter.getDefesa(), val -> activeCharacter.setDefesa(val));
        addAttributeRow("Força", activeCharacter.getForca(), val -> activeCharacter.setForca(val));
        addAttributeRow("Furtividade", activeCharacter.getFurtividade(), val -> activeCharacter.setFurtividade(val));
        addAttributeRow("Intelecto", activeCharacter.getIntelecto(), val -> activeCharacter.setIntelecto(val));

        if (!activeCharacter.isCampones()) {
            addAttributeRow("Magia", activeCharacter.getMagiaAttr(), val -> activeCharacter.setMagiaAttr(val));
        }

        addAttributeRow("Percepção", activeCharacter.getPercepcao(), val -> activeCharacter.setPercepcao(val));
        addAttributeRow("Precisão", activeCharacter.getPrecisao(), val -> activeCharacter.setPrecisao(val));
    }

    private interface AttributeSetter {
        void set(int val);
    }

    private void addAttributeRow(String name, int currentValue, AttributeSetter setter) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 12, 0, 12);
        row.setGravity(Gravity.CENTER_VERTICAL);

        TextView tv = new TextView(this);
        tv.setText(name + ": " + currentValue);
        tv.setTextSize(16);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        tv.setLayoutParams(params);

        int buttonSize = (int) (48 * getResources().getDisplayMetrics().density);

        MaterialButton btnMinus = new MaterialButton(
                this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        btnMinus.setIconResource(android.R.drawable.ic_menu_delete);
        btnMinus.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
        btnMinus.setIconPadding(0);
        btnMinus.setPadding(0, 0, 0, 0);
        LinearLayout.LayoutParams btnParamsMinus = new LinearLayout.LayoutParams(buttonSize, buttonSize);
        btnMinus.setLayoutParams(btnParamsMinus);

        btnMinus.setOnClickListener(v -> {
            if (currentValue > 1) {
                setter.set(currentValue - 1);
                updateUi();
                saveToDatabase();
            }
        });

        MaterialButton btnPlus = new MaterialButton(
                this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        btnPlus.setIconResource(android.R.drawable.ic_input_add);
        btnPlus.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
        btnPlus.setIconPadding(0);
        btnPlus.setPadding(0, 0, 0, 0);
        LinearLayout.LayoutParams btnParamsPlus = new LinearLayout.LayoutParams(buttonSize, buttonSize);
        btnParamsPlus.setMarginStart((int) (8 * getResources().getDisplayMetrics().density));
        btnPlus.setLayoutParams(btnParamsPlus);

        btnPlus.setOnClickListener(v -> {
            int cap = getAttributeCapForLevel(activeCharacter.getLevel());
            if (currentValue < cap) {
                setter.set(currentValue + 1);
                updateUi();
                saveToDatabase();
            } else {
                Toast.makeText(this, "Atributo atingiu o limite do Nível " + activeCharacter.getLevel() + " (" + cap + ")", Toast.LENGTH_SHORT).show();
            }
        });

        row.addView(tv);
        row.addView(btnMinus);
        row.addView(btnPlus);

        containerAttributes.addView(row);
    }

    private void finishWithResult() {
        saveToDatabase();
        finish();
    }
}