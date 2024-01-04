package me.crazycranberry.streamcraft.actions.explosion;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.config.ActionType;
import me.crazycranberry.streamcraft.config.Trigger;

import java.util.LinkedHashMap;

import static me.crazycranberry.streamcraft.StreamCraft.logger;

@Getter
@Setter
@ToString(callSuper = true)
public class Explosion extends Action {
    private Integer power;

    @Builder
    private Explosion(ActionType type, Trigger trigger, String target, Integer power) {
        super(type, trigger, target);
        this.power = power;
    }

    @Override
    public String pollMessage() {
        return String.format("Power %s Explosion", getPower());
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, LinkedHashMap<String, ?> input) {
        Integer power = validateField(input.get("power"), Integer.class, "power", false);
        if (power == null) {
            logger().warning("The power field was blank for an Explosion action");
            return null;
        }
        return Explosion.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .power(power)
                .build();
    }
}
