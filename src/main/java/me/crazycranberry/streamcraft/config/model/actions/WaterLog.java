package me.crazycranberry.streamcraft.config.model.actions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.crazycranberry.streamcraft.config.model.Action;
import me.crazycranberry.streamcraft.config.model.ActionType;
import me.crazycranberry.streamcraft.config.model.Trigger;

import java.util.LinkedHashMap;

@Getter
@Setter
@ToString(callSuper = true)
public class WaterLog extends Action {
    private Integer durationSeconds;

    @Builder
    private WaterLog(ActionType type, Trigger trigger, String target, Integer durationSeconds) {
        super(type, trigger, target);
        this.durationSeconds = durationSeconds;
    }

    @Override
    public String pollMessage(Action action) {
        return "Waterlog";
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, LinkedHashMap<String, ?> input) {
        Integer durationSeconds = validateField(input.get("duration_seconds"), Integer.class, "duration_seconds");
        if (durationSeconds == null) {
            return null;
        }
        return WaterLog.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .durationSeconds(durationSeconds)
                .build();
    }
}
