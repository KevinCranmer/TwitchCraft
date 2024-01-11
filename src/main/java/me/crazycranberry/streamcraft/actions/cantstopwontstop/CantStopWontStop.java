package me.crazycranberry.streamcraft.actions.cantstopwontstop;

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
public class CantStopWontStop extends Action {
    private Integer durationSeconds;

    @Builder
    private CantStopWontStop(ActionType type, Trigger trigger, String target, Boolean sendMessage, Integer durationSeconds) {
        super(type, trigger, target, sendMessage);
        this.durationSeconds = durationSeconds;
    }

    @Override
    public String pollMessage() {
        return "Can't Stop, Won't Stop";
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, Boolean sendMessage, LinkedHashMap<String, ?> input) {
        Integer durationSeconds = validateField(input.get("duration_seconds"), Integer.class, "duration_seconds");
        if (durationSeconds == null) {
            return null;
        }
        return CantStopWontStop.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .sendMessage(sendMessage)
                .durationSeconds(durationSeconds)
                .build();
    }
}
