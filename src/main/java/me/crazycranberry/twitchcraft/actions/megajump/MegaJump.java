package me.crazycranberry.twitchcraft.actions.megajump;

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
public class MegaJump extends Action {
    private Integer durationSeconds;
    private Integer numJumps;
    private String endMessage;

    @Builder
    private MegaJump(ActionType type, Trigger trigger, String target, String actionMessage, Boolean sendMessage, Integer durationSeconds, Integer numJumps, String endMessage) {
        super(type, trigger, target, actionMessage, sendMessage);
        this.durationSeconds = durationSeconds;
        this.numJumps = numJumps;
        this.endMessage = endMessage;
    }

    @Override
    public String pollMessage() {
        if (this.getTrigger().getPollMessage() != null) {
            return this.getTrigger().getPollMessage();
        }
        return "Mega Jump";
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, String actionMessage, Boolean sendMessage, LinkedHashMap<String, ?> input) {
        Integer durationSeconds = validateField(input.get("duration_seconds"), Integer.class, "duration_seconds", false);
        Integer numJumps = validateField(input.get("num_jumps"), Integer.class, "num_jumps", false);
        String endMessage = validateField(input.get("end_message"), String.class, "end_message", false);
        if (durationSeconds == null && numJumps == null) {
            logger().warning("Both duration_seconds and num_jumps were blank for a MEGA_JUMP action");
            return null;
        }
        return MegaJump.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .sendMessage(sendMessage)
                .actionMessage(actionMessage)
                .durationSeconds(durationSeconds)
                .numJumps(numJumps)
                .endMessage(endMessage)
                .build();
    }
}
