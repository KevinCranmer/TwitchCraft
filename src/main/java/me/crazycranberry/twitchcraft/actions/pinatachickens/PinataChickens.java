package me.crazycranberry.twitchcraft.actions.pinatachickens;

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
public class PinataChickens extends Action {
    private Integer numChickens;

    @Builder
    private PinataChickens(ActionType type, Trigger trigger, String target, String actionMessage, Boolean sendMessage, Integer numChickens) {
        super(type, trigger, target, actionMessage, sendMessage);
        this.numChickens = numChickens;
    }

    @Override
    public String pollMessage() {
        if (this.getTrigger().getPollMessage() != null) {
            return this.getTrigger().getPollMessage();
        }
        return String.format("%s Piñata Chickens", numChickens);
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, String actionMessage, Boolean sendMessage, LinkedHashMap<String, ?> input) {
        Integer numChickens = validateField(input.get("num_chickens"), Integer.class, "num_chickens", false);
        if (numChickens == null) {
            logger().warning("The num_chickens field was blank for an Pinata Chicken action");
            return null;
        }
        return PinataChickens.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .sendMessage(sendMessage)
                .actionMessage(actionMessage)
                .numChickens(numChickens)
                .build();
    }
}
