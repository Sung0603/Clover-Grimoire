
package com.example.clovergrimoire;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Converters {

    private static final Gson gson = new Gson();

    // ==========================================
    // FEITIÇOS
    // ==========================================

    @TypeConverter
    public static String fromSpellsList(List<Character.SpellsItem> list) {
        return gson.toJson(list != null ? list : new ArrayList<>());
    }

    @TypeConverter
    public static List<Character.SpellsItem> toSpellsList(String value) {
        if (value == null || value.isEmpty()) {
            return new ArrayList<>();
        }

        Type listType = new TypeToken<List<Character.SpellsItem>>() {}.getType();

        List<Character.SpellsItem> result = gson.fromJson(value, listType);
        return result != null ? result : new ArrayList<>();
    }

    // ==========================================
    // EQUIPAMENTOS
    // ==========================================

    @TypeConverter
    public static String fromItemsList(List<Character.EquipmentItem> list) {
        return gson.toJson(list != null ? list : new ArrayList<>());
    }

    @TypeConverter
    public static List<Character.EquipmentItem> toItemsList(String value) {
        if (value == null || value.isEmpty()) {
            return new ArrayList<>();
        }

        Type listType = new TypeToken<List<Character.EquipmentItem>>() {}.getType();

        List<Character.EquipmentItem> result = gson.fromJson(value, listType);
        return result != null ? result : new ArrayList<>();
    }

    // ==========================================
    // HABILIDADES
    // ==========================================

    @TypeConverter
    public static String fromSkillsList(List<Character.SkillItem> list) {
        return gson.toJson(list != null ? list : new ArrayList<>());
    }

    @TypeConverter
    public static List<Character.SkillItem> toSkillsList(String value) {
        if (value == null || value.isEmpty()) {
            return new ArrayList<>();
        }

        Type listType = new TypeToken<List<Character.SkillItem>>() {}.getType();

        List<Character.SkillItem> result = gson.fromJson(value, listType);
        return result != null ? result : new ArrayList<>();
    }
}
