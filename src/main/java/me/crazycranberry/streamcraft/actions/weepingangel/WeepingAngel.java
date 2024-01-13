package me.crazycranberry.streamcraft.actions.weepingangel;

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
public class WeepingAngel extends Action {
    private Integer secondsTillDespawn;
    private Integer distanceFromPlayer;

    @Builder
    private WeepingAngel(ActionType type, Trigger trigger, String target, Boolean sendMessage, Integer secondsTillDespawn, Integer distanceFromPlayer) {
        super(type, trigger, target, sendMessage);
        this.secondsTillDespawn = secondsTillDespawn;
        this.distanceFromPlayer = distanceFromPlayer;
    }

    @Override
    public String pollMessage() {
        return "Summon a Weeping Angel";
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, Boolean sendMessage, LinkedHashMap<String, ?> input) {
        Integer secondsTillDespawn = validateField(input.get("seconds_till_despawn"), Integer.class, "seconds_till_despawn");
        Integer distanceFromPlayer = validateField(input.get("distance_from_player"), Integer.class, "distance_from_player");
        if (secondsTillDespawn == null || distanceFromPlayer == null) {
            logger().warning("Either the seconds_till_despawn field or distance_from_player field was blank for a Weeping Angel action");
            return null;
        }
        return WeepingAngel.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .sendMessage(sendMessage)
                .secondsTillDespawn(secondsTillDespawn)
                .distanceFromPlayer(distanceFromPlayer)
                .build();
    }
}
