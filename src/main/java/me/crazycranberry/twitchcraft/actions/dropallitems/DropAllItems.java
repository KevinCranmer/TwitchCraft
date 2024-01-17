package me.crazycranberry.twitchcraft.actions.dropallitems;

import lombok.Builder;
import me.crazycranberry.twitchcraft.config.Action;
import me.crazycranberry.twitchcraft.config.ActionType;
import me.crazycranberry.twitchcraft.config.Trigger;

import java.util.LinkedHashMap;

public class DropAllItems extends Action {
    @Builder
    private DropAllItems(ActionType type, Trigger trigger, String target, String actionMessage, Boolean sendMessage) {
        super(type, trigger, target, actionMessage, sendMessage);
    }

    @Override
    public String pollMessage() {
        if (this.getTrigger().getPollMessage() != null) {
            return this.getTrigger().getPollMessage();
        }
        return "Drop All Items";
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, String actionMessage, Boolean sendMessage, LinkedHashMap<String, ?> input) {
        return DropAllItems.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .sendMessage(sendMessage)
                .actionMessage(actionMessage)
                .build();
    }
}
