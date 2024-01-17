package me.crazycranberry.streamcraft.actions.megajump;

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
public class MegaJump extends Action {
    private Integer durationSeconds;
    private Integer numJumps;

    @Builder
    private MegaJump(ActionType type, Trigger trigger, String target, Boolean sendMessage, Integer durationSeconds, Integer numJumps) {
        super(type, trigger, target, sendMessage);
        this.durationSeconds = durationSeconds;
        this.numJumps = numJumps;
    }

    @Override
    public String pollMessage() {
        if (this.getTrigger().getPollMessage() != null) {
            return this.getTrigger().getPollMessage();
        }
        return "Mega Jump";
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, Boolean sendMessage, LinkedHashMap<String, ?> input) {
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
                .sendMessage(sendMessage)
                .durationSeconds(durationSeconds)
                .numJumps(numJumps)
                .build();
    }
}
