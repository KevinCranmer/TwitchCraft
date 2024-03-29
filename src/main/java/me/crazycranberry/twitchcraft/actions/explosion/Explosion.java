package me.crazycranberry.twitchcraft.actions.explosion;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.crazycranberry.twitchcraft.config.Action;
import me.crazycranberry.twitchcraft.config.ActionType;
import me.crazycranberry.twitchcraft.config.Trigger;

import java.util.LinkedHashMap;

import static me.crazycranberry.twitchcraft.TwitchCraft.logger;

@Getter
@Setter
@ToString(callSuper = true)
public class Explosion extends Action {
    private Integer power;

    @Builder
    private Explosion(ActionType type, Trigger trigger, String target, String actionMessage, Boolean sendMessage, Integer power) {
        super(type, trigger, target, actionMessage, sendMessage);
        this.power = power;
    }

    @Override
    public String pollMessage() {
        if (this.getTrigger().getPollMessage() != null) {
            return this.getTrigger().getPollMessage();
        }
        return String.format("Power %s Explosion", getPower());
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, String actionMessage, Boolean sendMessage, LinkedHashMap<String, ?> input) {
        Integer power = validateField(input.get("power"), Integer.class, "power", false);
        if (power == null) {
            logger().warning("The power field was blank for an Explosion action");
            return null;
        }
        return Explosion.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .sendMessage(sendMessage)
                .actionMessage(actionMessage)
                .power(power)
                .build();
    }
}
