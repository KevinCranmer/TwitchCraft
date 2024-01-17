package me.crazycranberry.streamcraft.actions.nojumping;

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
public class NoJumping extends Action {
    private Integer durationSeconds;
    private String endMessage;

    @Builder
    private NoJumping(ActionType type, Trigger trigger, String target, String actionMessage, Boolean sendMessage, Integer durationSeconds, String endMessage) {
        super(type, trigger, target, actionMessage, sendMessage);
        this.durationSeconds = durationSeconds;
        this.endMessage = endMessage;
    }

    @Override
    public String pollMessage() {
        if (this.getTrigger().getPollMessage() != null) {
            return this.getTrigger().getPollMessage();
        }
        return "No Jumping";
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, String actionMessage, Boolean sendMessage, LinkedHashMap<String, ?> input) {
        Integer durationSeconds = validateField(input.get("duration_seconds"), Integer.class, "duration_seconds");
        String endMessage = validateField(input.get("end_message"), String.class, "end_message", false);
        if (durationSeconds == null) {
            logger().warning("duration_seconds cannot be null for a NO_JUMPING action");
            return null;
        }
        return NoJumping.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .sendMessage(sendMessage)
                .actionMessage(actionMessage)
                .endMessage(endMessage)
                .durationSeconds(durationSeconds)
                .build();
    }
}
