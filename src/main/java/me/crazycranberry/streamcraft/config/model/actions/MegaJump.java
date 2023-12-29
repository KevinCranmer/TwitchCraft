package me.crazycranberry.streamcraft.config.model.actions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.crazycranberry.streamcraft.config.model.Action;
import me.crazycranberry.streamcraft.config.model.ActionType;
import me.crazycranberry.streamcraft.config.model.Trigger;

import java.util.LinkedHashMap;

import static me.crazycranberry.streamcraft.StreamCraft.logger;

@Getter
@Setter
@ToString(callSuper = true)
public class MegaJump extends Action {
    private Integer durationSeconds;
    private Integer numJumps;

    @Builder
    private MegaJump(ActionType type, Trigger trigger, String target, Integer durationSeconds, Integer numJumps) {
        super(type, trigger, target);
        this.durationSeconds = durationSeconds;
        this.numJumps = numJumps;
    }

    @Override
    public String pollMessage(Action action) {
        return "Mega Jump";
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, LinkedHashMap<String, ?> input) {
        Integer durationSeconds = validateField(input.get("duration_seconds"), Integer.class, "duration_seconds", false);
        Integer numJumps = validateField(input.get("num_jumps"), Integer.class, "num_jumps", false);
        if (durationSeconds == null && numJumps == null) {
            logger().warning("Both duration_seconds and num_jumps were blank for a MEGA_JUMP action");
            return null;
        }
        return MegaJump.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .durationSeconds(durationSeconds)
                .numJumps(numJumps)
                .build();
    }
}
