package me.crazycranberry.streamcraft.actions.raid;

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
public class Raid extends Action {
    private Integer badOmenLevel;

    @Builder
    private Raid(ActionType type, Trigger trigger, String target, Boolean sendMessage, Integer badOmenLevel) {
        super(type, trigger, target, sendMessage);
        this.badOmenLevel = badOmenLevel;
    }

    @Override
    public String pollMessage() {
        if (this.getTrigger().getPollMessage() != null) {
            return this.getTrigger().getPollMessage();
        }
        return String.format("Level %s Raid", badOmenLevel);
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, Boolean sendMessage, LinkedHashMap<String, ?> input) {
        Integer badOmenLevel = validateBadOmenLevel(input.get("bad_omen_level"));
        if (badOmenLevel == null) {
            return null;
        }
        return Raid.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .sendMessage(sendMessage)
                .badOmenLevel(badOmenLevel)
                .build();
    }

    public static <T> Integer validateBadOmenLevel(T field) {
        if (!(field instanceof Integer)) {
            logger().warning("bad_omen_level was not an integer.");
            return null;
        }
        Integer badOmenLevel = (Integer) field;
        if (badOmenLevel < 1) {
            logger().warning("bad_omen_level cannot be lower than 1. Defaulting to 1.");
            badOmenLevel = 1;
        }
        if (badOmenLevel > 5) {
            logger().warning("bad_omen_level cannot be greater than 5. Defaulting to 5.");
            badOmenLevel = 5;
        }
        return badOmenLevel;
    }
}
