package me.crazycranberry.streamcraft.actions.buildahouse;

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
public class BuildAHouse extends Action {
    @Builder
    private BuildAHouse(ActionType type, Trigger trigger, String target) {
        super(type, trigger, target);
    }

    @Override
    public String pollMessage() {
        return "Build a House";
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, LinkedHashMap<String, ?> input) {
        return BuildAHouse.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .build();
    }
}
