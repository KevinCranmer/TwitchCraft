package me.crazycranberry.streamcraft.actions.rotatinghotbar;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.config.ActionType;
import me.crazycranberry.streamcraft.config.Trigger;

import java.util.LinkedHashMap;

@Getter
@Setter
@ToString(callSuper = true)
public class RotatingHotbar extends Action {
    private Integer numRotations;
    private Integer secondsBetweenRotations;

    @Builder
    private RotatingHotbar(ActionType type, Trigger trigger, String target, Boolean sendMessage, Integer numRotations, Integer secondsBetweenRotations) {
        super(type, trigger, target, sendMessage);
        this.numRotations = numRotations;
        this.secondsBetweenRotations = secondsBetweenRotations;
    }

    @Override
    public String pollMessage() {
        return "Rotating Hotbar";
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, Boolean sendMessage, LinkedHashMap<String, ?> input) {
        Integer numRotations = validateField(input.get("num_rotations"), Integer.class, "num_rotations");
        Integer secondsBetweenRotations = validateField(input.get("seconds_between_rotations"), Integer.class, "seconds_between_rotations");
        if (numRotations == null || secondsBetweenRotations == null) {
            return null;
        }
        return RotatingHotbar.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .sendMessage(sendMessage)
                .numRotations(numRotations)
                .secondsBetweenRotations(secondsBetweenRotations)
                .build();
    }
}
