package com.example.clovergrimoire;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Converters {

    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromSpellsList(List<Character.SpellsItem> list) {
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<Character.SpellsItem> toSpellsList(String value) {
        if (value == null) return new ArrayList<>();
        Type listType = new TypeToken<List<Character.SpellsItem>>() {}.getType();
        return gson.fromJson(value, listType);
    }

    @TypeConverter
    public static String fromItemsList(List<Character.EquipmentItem> list) {
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<Character.EquipmentItem> toItemsList(String value) {
        if (value == null) return new ArrayList<>();
        Type listType = new TypeToken<List<Character.EquipmentItem>>() {}.getType();
        return gson.fromJson(value, listType);
    }

    @TypeConverter
    public static String fromSkillsList(List<Character.SkillItem> list) {
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<Character.SkillItem> toSkillsList(String value) {
        if (value == null) return new ArrayList<>();
        Type listType = new TypeToken<List<Character.SkillItem>>() {}.getType();
        return gson.fromJson(value, listType);
    }
}