package me.crazycranberry.twitchcraft.actions.deleteachunk;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.crazycranberry.twitchcraft.config.Action;
import me.crazycranberry.twitchcraft.config.ActionType;
import me.crazycranberry.twitchcraft.config.Trigger;

import java.util.LinkedHashMap;
import java.util.List;

import static me.crazycranberry.twitchcraft.TwitchCraft.logger;
import static me.crazycranberry.twitchcraft.config.TriggerType.CHANNEL_CHEER;
import static me.crazycranberry.twitchcraft.config.TriggerType.SUB_GIFT;


@Getter
@Setter
@ToString(callSuper = true)
public class DeleteAChunk extends Action {
    private Integer radius;
    private Double rowsPerTick;

    @Builder
    private DeleteAChunk(ActionType type, Trigger trigger, String target, String actionMessage, Boolean sendMessage, Integer radius, Double rowsPerTick) {
        super(type, trigger, target, actionMessage, sendMessage);
        this.radius = radius;
        this.rowsPerTick = rowsPerTick;
    }

    @Override
    public String pollMessage() {
        if (this.getTrigger().getPollMessage() != null) {
            return this.getTrigger().getPollMessage();
        }
        return "Delete a Nearby Chunk";
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, String actionMessage, Boolean sendMessage, LinkedHashMap<String, ?> input) {
        Integer radius = validateField(input.get("radius"), Integer.class, "radius", true);
        Double rowsPerTick = validateField(input.get("rows_per_tick"), Double.class, "rows_per_tick", true);
        return DeleteAChunk.builder()
            .type(actionType)
            .trigger(trigger)
            .target(target)
            .sendMessage(sendMessage)
            .actionMessage(actionMessage)
            .radius(radius)
            .rowsPerTick(rowsPerTick)
            .build();
    }
}
