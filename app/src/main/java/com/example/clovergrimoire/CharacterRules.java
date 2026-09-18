package com.example.clovergrimoire;

import java.util.Random;

public class CharacterRules {

    private static final Random random = new Random();

    public static int rollD100() {
        return random.nextInt(100) + 1;
    }

    public static int rollD6() {
        return random.nextInt(6) + 1;
    }

    // Determina a Hierarquia Social por d100
    public static String getSocialHierarchy(int roll) {
        if (roll <= 5) return "Realeza";
        if (roll <= 15) return "Alta Nobreza";
        if (roll <= 30) return "Baixa Nobreza";
        if (roll <= 85) return "Plebeus";
        return "Camponeses";
    }

    // Determina a Raça por d100
    public static String getRaceByRoll(int roll) {
        if (roll <= 70) return "Humano";
        if (roll <= 90) return "Mutante";
        if (roll <= 96) return "Espírito Elemental";
        if (roll <= 99) return "Demônio";

        // Rolagem 100: 1d2 para definir Elfo ou Abençoado
        int subRoll = random.nextInt(2) + 1;
        return (subRoll == 1) ? "Abençoado pela Mana" : "Elfo";
    }

    // Bônus de Mana por Hierarquia (com tratamento flexível para nomes similares)
    public static int getHierarchyManaBonus(String hierarchy) {
        if (hierarchy == null) return 0;

        String cleanHierarchy = hierarchy.trim();

        if (cleanHierarchy.equalsIgnoreCase("Realeza") || cleanHierarchy.equalsIgnoreCase("Grande Espírito")) {
            return 8;
        } else if (cleanHierarchy.equalsIgnoreCase("Alta Nobreza") || cleanHierarchy.equalsIgnoreCase("Espíritos Ancestrais") || cleanHierarchy.equalsIgnoreCase("Nobreza")) {
            return 6;
        } else if (cleanHierarchy.equalsIgnoreCase("Baixa Nobreza") || cleanHierarchy.equalsIgnoreCase("Espíritos Nobres")) {
            return 4;
        } else if (cleanHierarchy.equalsIgnoreCase("Plebeus") || cleanHierarchy.equalsIgnoreCase("Plebeu") || cleanHierarchy.equalsIgnoreCase("Espíritos Comuns")) {
            return 2;
        }

        return 0; // Camponeses / Padrão
    }

    // Bônus de Mana por Raça (com escalonamento e validação de nulo)
    public static int getRaceManaBonus(String race, int level) {
        if (race == null) return 0;

        String lowerRace = race.toLowerCase();

        if (lowerRace.contains("espírito elemental") || lowerRace.contains("espirito elemental")) {
            return 5 + (Math.max(0, level - 1) * 3);
        }
        if (lowerRace.contains("demônio") || lowerRace.contains("demonio")) {
            return 10;
        }
        if (lowerRace.contains("elfo") || lowerRace.contains("abençoado") || lowerRace.contains("abencoado")) {
            return 12;
        }

        return 0;
    }
}