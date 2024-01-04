package me.crazycranberry.streamcraft.actions.potioneffect;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.config.ActionType;
import me.crazycranberry.streamcraft.config.Trigger;
import org.bukkit.potion.PotionEffectType;

import java.util.LinkedHashMap;

import static me.crazycranberry.streamcraft.StreamCraft.logger;
import static me.crazycranberry.streamcraft.config.ActionUtils.allNull;
import static me.crazycranberry.streamcraft.config.ActionUtils.anyNull;

@Getter
@Setter
@ToString(callSuper = true)
public class PotionEffect extends Action {
    private PotionEffectType potionType;
    private PotionRandom potionRandom;
    private Integer level;
    private Integer durationSeconds;

    @Builder
    private PotionEffect(ActionType type, Trigger trigger, String target, PotionEffectType potionType, PotionRandom potionRandom, Integer level, Integer durationSeconds) {
        super(type, trigger, target);
        this.potionType = potionType;
        this.potionRandom = potionRandom;
        this.level = level;
        this.durationSeconds = durationSeconds;
    }

    @Override
    public String pollMessage() {
        String pollMessage = String.format("%s", getPotionType() == null ? getPotionRandom().name().toLowerCase().replace("_", " ") + " potion effect" : getPotionType().getName().toLowerCase().replace("_", " "));
        return pollMessage.substring(0, 1).toUpperCase() + pollMessage.substring(1);
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, LinkedHashMap<String, ?> input) {
        PotionEffectType potionType = validatePotionType(input.get("potion_type"));
        PotionRandom potionRandom = validatePotionRandom(input.get("potion_type"));
        Integer level = validateField(input.get("level"), Integer.class, "level");
        Integer durationSeconds = validateField(input.get("duration_seconds"), Integer.class, "duration_seconds");
        if (anyNull(actionType, trigger, level, durationSeconds) || allNull(potionType, potionRandom)) {
            return null;
        }
        return PotionEffect.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .potionType(potionType)
                .potionRandom(potionRandom)
                .level(level)
                .durationSeconds(durationSeconds)
                .build();
    }

    private static <T> PotionRandom validatePotionRandom(T potionType) {
        if (!(potionType instanceof String)) {
            return null;
        }
        String typeString = (String) potionType;
        switch (typeString) {
            case "RANDOM":
                return PotionRandom.RANDOM;
            case "RANDOM_GOOD":
                return PotionRandom.RANDOM_GOOD;
            case "RANDOM_BAD":
                return PotionRandom.RANDOM_BAD;
            default:
                return null;
        }
    }

    private static <T> PotionEffectType validatePotionType(T potionType) {
        if (!(potionType instanceof String)) {
            logger().warning("A potion_type value was not a String.");
            return null;
        }
        String typeString = (String) potionType;
        PotionEffectType type = PotionEffectType.getByName((typeString).toLowerCase().trim().replaceAll("[^a-zA-Z]+", "_"));
        if (type == null && !"RANDOM_GOOD".equals(typeString) && !"RANDOM_BAD".equals(typeString) && !"RANDOM".equals(typeString)) {
            logger().warning("Unable to parse the potion type: " + typeString);
            return null;
        }
        return type;
    }

    public enum PotionRandom {
        RANDOM_GOOD,
        RANDOM_BAD,
        RANDOM;
    }
}
