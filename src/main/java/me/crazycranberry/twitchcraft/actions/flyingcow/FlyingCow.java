package me.crazycranberry.twitchcraft.actions.flyingcow;

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
public class FlyingCow extends Action {
    private Integer numCows;
    private Integer secondsBetweenCows;
    private Integer distanceFromPlayer;
    private Double cowVelocity;

    @Builder
    private FlyingCow(ActionType type, Trigger trigger, String target, String actionMessage, Boolean sendMessage, Integer numCows, Integer secondsBetweenCows, Integer distanceFromPlayer, Double cowVelocity) {
        super(type, trigger, target, actionMessage, sendMessage);
        this.numCows = numCows;
        this.secondsBetweenCows = secondsBetweenCows;
        this.distanceFromPlayer = distanceFromPlayer;
        this.cowVelocity = cowVelocity;
    }

    @Override
    public String pollMessage() {
        if (this.getTrigger().getPollMessage() != null) {
            return this.getTrigger().getPollMessage();
        }
        return "Flying Cows";
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, String actionMessage, Boolean sendMessage, LinkedHashMap<String, ?> input) {
        Integer numCows = validateField(input.get("num_cows"), Integer.class, "num_cows");
        Integer secondsBetweenCows = validateField(input.get("seconds_between_cows"), Integer.class, "seconds_between_cows");
        Integer distanceFromPlayer = validateField(input.get("distance_from_player"), Integer.class, "distance_from_player");
        Double cowVelocity = validateField(input.get("cow_velocity"), Double.class, "cow_velocity");
        if (numCows == null || secondsBetweenCows == null || distanceFromPlayer == null || cowVelocity == null) {
            logger().warning("Either num_cows, distance_from_player, cow_velocity or seconds_between_cows was invalid for a FLYING_COW action.");
            return null;
        }
        return FlyingCow.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .sendMessage(sendMessage)
                .actionMessage(actionMessage)
                .numCows(numCows)
                .secondsBetweenCows(secondsBetweenCows)
                .distanceFromPlayer(distanceFromPlayer)
                .cowVelocity(cowVelocity)
                .build();
    }
}
