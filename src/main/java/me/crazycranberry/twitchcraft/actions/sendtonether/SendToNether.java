package me.crazycranberry.twitchcraft.actions.sendtonether;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.crazycranberry.twitchcraft.config.Action;
import me.crazycranberry.twitchcraft.config.ActionType;
import me.crazycranberry.twitchcraft.config.Trigger;

import java.util.LinkedHashMap;

@Getter
@Setter
@ToString(callSuper = true)
public class SendToNether extends Action {
    private Integer netherPortalPossibleRadius;

    @Builder
    private SendToNether(ActionType type, Trigger trigger, String target, String actionMessage, Boolean sendMessage, Integer netherPortalPossibleRadius) {
        super(type, trigger, target, actionMessage, sendMessage);
        this.netherPortalPossibleRadius = netherPortalPossibleRadius;
    }

    @Override
    public String pollMessage() {
        if (this.getTrigger().getPollMessage() != null) {
            return this.getTrigger().getPollMessage();
        }
        return "Trip to the Nether";
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, String actionMessage, Boolean sendMessage, LinkedHashMap<String, ?> input) {
        Integer netherPortalPossibleRadius = validateField(input.get("nether_portal_possible_radius"), Integer.class, "nether_portal_possible_radius");
        if (netherPortalPossibleRadius == null) {
            return null;
        }
        return SendToNether.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .sendMessage(sendMessage)
                .actionMessage(actionMessage)
                .netherPortalPossibleRadius(netherPortalPossibleRadius)
                .build();
    }
}
