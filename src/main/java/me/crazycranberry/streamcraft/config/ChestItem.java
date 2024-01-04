package me.crazycranberry.streamcraft.config;

import lombok.Getter;
import org.bukkit.Material;

import java.util.LinkedHashMap;
import java.util.List;

import static me.crazycranberry.streamcraft.StreamCraft.logger;

@Getter
public class ChestItem {
    private final Material material;
    private final Double chance;
    private final Integer min;
    private final Integer max;

    public static ChestItem fromYaml(LinkedHashMap<String, ?> input) {
        boolean anyFieldsMissing = reportAnyMissingFields(input);
        if (anyFieldsMissing) {
            return null;
        }
        Material material = validateMaterial(input.get("name"));
        Double chance = validateChance(input.get("chance"));
        Integer min = validateMin(input.get("min"));
        Integer max = validateMax(input.get("min"));
        if (material == null || chance == null || min == null || max == null) {
            return null;
        }
        if (max < min) {
            logger().warning("The max was less than the min. Defaulting both to the min value.");
            max = min;
        }
        return new ChestItem(material, chance, min, max);
    }

    private static <T> Integer validateMin(T inputMin) {
        if (!(inputMin instanceof Integer)) {
            logger().warning("A chest_items min was not an Integer.");
            return null;
        }
        return (Integer) inputMin;
    }

    private static <T> Integer validateMax(T inputMax) {
        if (!(inputMax instanceof Integer)) {
            logger().warning("A chest_items max was not an Integer.");
            return null;
        }
        return (Integer) inputMax;
    }

    private static <T> Double validateChance(T inputChance) {
        if (!(inputChance instanceof Double)) {
            logger().warning("A chest_items chance was not a Double.");
            return null;
        }
        return (Double) inputChance;
    }

    private static <T> Material validateMaterial(T name) {
        if (!(name instanceof String)) {
            logger().warning("A chest_items name was not a String.");
            return null;
        }
        Material material = Material.matchMaterial((String) name);
        if (material == null) {
            logger().warning("chest_items item '" + name + "' is not a valid Material name.");
            return null;
        }
        return material;
    }

    private static boolean reportAnyMissingFields(LinkedHashMap<String, ?> input) {
        boolean anyMissing = false;
        for (String fieldName : List.of("name", "chance", "min", "max")) {
            if (!input.containsKey(fieldName)) {
                logger().warning("The '" + fieldName + "' field for a chest_items item is missing.");
                anyMissing = true;
            }
        }
        return anyMissing;
    }

    public ChestItem(Material material, Double chance, Integer min, Integer max) {
        this.material = material;
        this.chance = chance;
        this.min = min;
        this.max = max;
    }

    @Override
    public String toString() {
        return String.format("Material: %s, chance: %s, min: %s, max: %s", material, chance, min, max);
    }
}