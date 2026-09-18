package com.example.clovergrimoire;

import android.net.Uri;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "characters")
public class Character {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private String race;
    private String hierarchy;
    private String magicType;
    private int level;
    private String imageUriString; // Room salva como String no SQLite

    // 10 Atributos Principais
    private int agilidade, carisma, corpo, defesa, forca, furtividade, intelecto, magiaAttr, percepcao, precisao;

    // Recursos de Combate
    private int currentHp, currentStamina, currentMp;

    // Listas para Feitiços, Itens e Habilidades
    private List<SpellsItem> spellsList;
    private List<EquipmentItem> itemsList;
    private List<SkillItem> skillsList;

    public static class SpellsItem {
        public String name = "Novo Feitiço";
        public String description = "";
        public String type = "";
        public String cost = "";
        public String range = "";
        public String effect = "";
    }

    public static class EquipmentItem {
        public String name = "Novo Item";
        public String description = "";
        public String range = "";
        public String effect = "";
    }

    public static class SkillItem {
        public String name = "Nova Habilidade";
        public String description = "";
        public String effect = "";
        public String type = "";
    }

    // Construtor principal utilizado pelo Room
    public Character(String name, String race, String hierarchy, String magicType, int level, String imageUriString) {
        this.name = name != null ? name : "Desconhecido";
        this.race = race != null ? race : "Humano";
        this.hierarchy = hierarchy != null ? hierarchy : "Plebeus";
        this.magicType = magicType != null ? magicType : "Nenhum";
        this.level = Math.max(1, level);
        this.imageUriString = imageUriString;

        this.spellsList = new ArrayList<>();
        this.itemsList = new ArrayList<>();
        this.skillsList = new ArrayList<>();

        rollAllAttributes();
        resetStatusToMax();
    }

    // Construtor secundário para compatibilidade com Uri (ignorado pelo Room)
    @Ignore
    public Character(String name, String race, String hierarchy, String magicType, int level, Uri imageUri) {
        this(name, race, hierarchy, magicType, level, imageUri != null ? imageUri.toString() : null);
    }

    public boolean isCampones() {
        return "Camponeses".equalsIgnoreCase(hierarchy) || "Camponês".equalsIgnoreCase(hierarchy);
    }

    public void rollAllAttributes() {
        this.agilidade = CharacterRules.rollD6();
        this.carisma = CharacterRules.rollD6();
        this.corpo = CharacterRules.rollD6();
        this.defesa = CharacterRules.rollD6();
        this.forca = CharacterRules.rollD6();
        this.furtividade = CharacterRules.rollD6();
        this.intelecto = CharacterRules.rollD6();
        this.magiaAttr = CharacterRules.rollD6();
        this.percepcao = CharacterRules.rollD6();
        this.precisao = CharacterRules.rollD6();
    }

    public int getDamageReduction() { return defesa / 2; }

    public int getMaxHp() {
        int baseHp = corpo + 4;
        if (level > 1) {
            baseHp += Math.min(level - 1, 4) * 3 + Math.max(0, level - 5) * 4;
        }
        return baseHp;
    }

    public int getMaxStamina() {
        int baseStamina = corpo;
        if (level > 1) {
            baseStamina += Math.min(level - 1, 4) * 1 + Math.max(0, level - 5) * 2;
        }
        if (isCampones()) { baseStamina *= 2; }
        return baseStamina;
    }

    public int getMaxMp() {
        if (isCampones()) return 0;
        int baseMp = magiaAttr;
        if (level > 1) {
            baseMp += Math.min(level - 1, 4) * 1 + Math.max(0, level - 5) * 2;
        }
        baseMp += CharacterRules.getHierarchyManaBonus(hierarchy);
        baseMp += CharacterRules.getRaceManaBonus(race, level);
        return baseMp;
    }

    public void resetStatusToMax() {
        this.currentHp = getMaxHp();
        this.currentStamina = getMaxStamina();
        this.currentMp = getMaxMp();
    }

    public void setLevel(int newLevel) {
        this.level = Math.max(1, Math.min(10, newLevel));
        normalizeCurrentResources();
    }

    private void normalizeCurrentResources() {
        this.currentHp = Math.min(this.currentHp, getMaxHp());
        this.currentStamina = Math.min(this.currentStamina, getMaxStamina());
        this.currentMp = Math.min(this.currentMp, getMaxMp());
    }

    public void modifyHp(int amount) { this.currentHp = Math.max(0, Math.min(getMaxHp(), this.currentHp + amount)); }
    public void modifyStamina(int amount) { this.currentStamina = Math.max(0, Math.min(getMaxStamina(), this.currentStamina + amount)); }
    public void modifyMp(int amount) {
        if (isCampones()) { this.currentMp = 0; return; }
        this.currentMp = Math.max(0, Math.min(getMaxMp(), this.currentMp + amount));
    }

    // Getters e Setters para o Room
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRace() { return race; }
    public void setRace(String race) { this.race = race; }

    public String getHierarchy() { return hierarchy; }
    public void setHierarchy(String hierarchy) { this.hierarchy = hierarchy; }

    public String getMagicType() { return magicType; }
    public void setMagicType(String magicType) { this.magicType = magicType; }

    public int getLevel() { return level; }

    public String getImageUriString() { return imageUriString; }
    public void setImageUriString(String imageUriString) { this.imageUriString = imageUriString; }

    // Getter/Setter utilitário para Uri
    public Uri getImageUri() {
        return imageUriString != null ? Uri.parse(imageUriString) : null;
    }
    public void setImageUri(Uri imageUri) {
        this.imageUriString = imageUri != null ? imageUri.toString() : null;
    }

    public int getCurrentHp() { return currentHp; }
    public void setCurrentHp(int currentHp) { this.currentHp = currentHp; }

    public int getCurrentStamina() { return currentStamina; }
    public void setCurrentStamina(int currentStamina) { this.currentStamina = currentStamina; }

    public int getCurrentMp() { return isCampones() ? 0 : currentMp; }
    public void setCurrentMp(int currentMp) { this.currentMp = currentMp; }

    public List<SpellsItem> getSpellsList() { return spellsList; }
    public void setSpellsList(List<SpellsItem> spellsList) { this.spellsList = spellsList; }

    public List<EquipmentItem> getItemsList() { return itemsList; }
    public void setItemsList(List<EquipmentItem> itemsList) { this.itemsList = itemsList; }

    public List<SkillItem> getSkillsList() { return skillsList; }
    public void setSkillsList(List<SkillItem> skillsList) { this.skillsList = skillsList; }

    public int getAgilidade() { return agilidade; }
    public void setAgilidade(int v) { this.agilidade = v; }
    public int getCarisma() { return carisma; }
    public void setCarisma(int v) { this.carisma = v; }
    public int getCorpo() { return corpo; }
    public void setCorpo(int v) { this.corpo = v; normalizeCurrentResources(); }
    public int getDefesa() { return defesa; }
    public void setDefesa(int v) { this.defesa = v; }
    public int getForca() { return forca; }
    public void setForca(int v) { this.forca = v; }
    public int getFurtividade() { return furtividade; }
    public void setFurtividade(int v) { this.furtividade = v; }
    public int getIntelecto() { return intelecto; }
    public void setIntelecto(int v) { this.intelecto = v; }
    public int getMagiaAttr() { return magiaAttr; }
    public void setMagiaAttr(int v) { this.magiaAttr = v; normalizeCurrentResources(); }
    public int getPercepcao() { return percepcao; }
    public void setPercepcao(int v) { this.percepcao = v; }
    public int getPrecisao() { return precisao; }
    public void setPrecisao(int v) { this.precisao = v; }
}