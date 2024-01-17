package me.crazycranberry.streamcraft.actions.dropallitems;

import lombok.Builder;
import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.config.ActionType;
import me.crazycranberry.streamcraft.config.Trigger;

import java.util.LinkedHashMap;

public class DropAllItems extends Action {
    @Builder
    private DropAllItems(ActionType type, Trigger trigger, String target, Boolean sendMessage) {
        super(type, trigger, target, sendMessage);
    }

    @Override
    public String pollMessage() {
        if (this.getTrigger().getPollMessage() != null) {
            return this.getTrigger().getPollMessage();
        }
        return "Drop All Items";
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, Boolean sendMessage, LinkedHashMap<String, ?> input) {
        return DropAllItems.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .sendMessage(sendMessage)
                .build();
    }
}
