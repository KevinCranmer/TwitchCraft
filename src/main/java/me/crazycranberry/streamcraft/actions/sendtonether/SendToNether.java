package me.crazycranberry.streamcraft.actions.sendtonether;

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
public class SendToNether extends Action {
    private Integer netherPortalPossibleRadius;

    @Builder
    private SendToNether(ActionType type, Trigger trigger, String target, Boolean sendMessage, Integer netherPortalPossibleRadius) {
        super(type, trigger, target, sendMessage);
        this.netherPortalPossibleRadius = netherPortalPossibleRadius;
    }

    @Override
    public String pollMessage() {
        return "Trip to the Nether";
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, Boolean sendMessage, LinkedHashMap<String, ?> input) {
        Integer netherPortalPossibleRadius = validateField(input.get("nether_portal_possible_radius"), Integer.class, "nether_portal_possible_radius");
        if (netherPortalPossibleRadius == null) {
            return null;
        }
        return SendToNether.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .sendMessage(sendMessage)
                .netherPortalPossibleRadius(netherPortalPossibleRadius)
                .build();
    }
}
