package me.crazycranberry.streamcraft.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashMap;

import static me.crazycranberry.streamcraft.StreamCraft.logger;
import static me.crazycranberry.streamcraft.config.TriggerType.POLL;

@Getter
@Setter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Trigger {
    private String predicate;
    private TriggerType type;
    private Double chance;

    public static Trigger fromYaml(LinkedHashMap<String, ?> input) {
        TriggerType type = validateType(input.get("type"));
        if (type == null) {
            return null;
        }
        Double chance = null;
        if (type.equals(POLL)) {
            chance = validateChance(input.get("chance"));
        }
        String predicate = validatePredicate(input.get("predicate"), type);
        if (predicate == null && type.requirePredicate()) {
            logger().warning("A predicate is required for a " + type.value() + " action.");
            return null;
        }
        return new Trigger(predicate, type, chance);
    }

    private static <T> String validatePredicate(T predicate, TriggerType triggerType) {
        if (!(predicate instanceof String)) {
            if (triggerType.requirePredicate()) {
                logger().warning("An Action's Trigger type was not a String.");
            }
            return null;
        }
        return (String) predicate;
    }

    private static <T> Double validateChance(T chance) {
        if (!(chance instanceof Double)) {
            logger().warning("An Action's Trigger chance was not a Double.");
            return null;
        }
        return (Double) chance;
    }

    private static <T> TriggerType validateType(T type) {
        if (!(type instanceof String)) {
            logger().warning("An Action's Trigger type was not a String.");
            return null;
        }
        TriggerType t = TriggerType.fromValue((String) type);
        if (t == null) {
            logger().warning("The following Trigger type is invalid: " + type);
        }
        return t;
    }
}
