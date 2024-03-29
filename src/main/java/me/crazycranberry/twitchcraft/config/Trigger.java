package me.crazycranberry.twitchcraft.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashMap;

import static me.crazycranberry.twitchcraft.TwitchCraft.logger;
import static me.crazycranberry.twitchcraft.config.Action.validateField;
import static me.crazycranberry.twitchcraft.config.TriggerType.POLL;

@Getter
@Setter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Trigger {
    private Integer min;
    private Integer max;
    private TriggerType type;
    private Double weight;
    private String pollMessage;

    public static Trigger fromYaml(LinkedHashMap<String, ?> input) {
        TriggerType type = validateType(input.get("type"));
        if (type == null) {
            return null;
        }
        Double weight = null;
        if (type.equals(POLL)) {
            weight = input.get("weight") == null ? null : validateWeight(input.get("weight"));
        }
        Integer min = validateMin(input.get("min"));
        Integer max = validateMax(input.get("max"));
        min = min == null ? 0 : Math.max(min, 0);
        max = max == null ? Integer.MAX_VALUE : Math.max(min, max);
        String pollMessage = validateField(input.get("poll_message"), String.class, "poll_message", false);
        return new Trigger(min, max, type, weight, pollMessage);
    }

    private static <T> Integer validateMin(T min) {
        if (!(min instanceof Integer)) {
            if (min != null) {
                logger().warning("An Action's Trigger min was not an Integer.");
            }
            return null;
        }
        return (Integer) min;
    }

    private static <T> Integer validateMax(T max) {
        if (!(max instanceof Integer)) {
            if (max != null) {
                logger().warning("An Action's Trigger max was not an Integer.");
            }
            return null;
        }
        return (Integer) max;
    }

    private static <T> Double validateWeight(T weight) {
        if (!(weight instanceof Double)) {
            logger().warning("An Action's Trigger weight was not a Double.");
            return null;
        }
        return (Double) weight;
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
