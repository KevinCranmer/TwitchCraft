package me.crazycranberry.twitchcraft.actions.chestofgoodies;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.crazycranberry.twitchcraft.config.Action;
import me.crazycranberry.twitchcraft.config.ActionType;
import me.crazycranberry.twitchcraft.config.Trigger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static me.crazycranberry.twitchcraft.TwitchCraft.logger;

@Getter
@Setter
@ToString(callSuper = true)
public class ChestOfGoodies extends Action {
    private List<ChestItem> chestItems;

    @Builder
    private ChestOfGoodies(ActionType type, Trigger trigger, String target, String actionMessage, Boolean sendMessage, List<ChestItem> chestItems) {
        super(type, trigger, target, actionMessage, sendMessage);
        this.chestItems = chestItems;
    }

    @Override
    public String pollMessage() {
        if (this.getTrigger().getPollMessage() != null) {
            return this.getTrigger().getPollMessage();
        }
        return "Chest of Goodies";
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, String actionMessage, Boolean sendMessage, LinkedHashMap<String, ?> input) {
        if (input.get("chest_items") == null || !(input.get("chest_items") instanceof ArrayList<?>)) {
            logger().warning("The chest_items in a Chest Of Goodies actions was not a valid list");
            return null;
        }
        List<ChestItem> chestItems = ((ArrayList<?>) input.get("chest_items")).stream().map(i -> ChestItem.fromYaml((LinkedHashMap<String, ?>) i)).toList();
        return ChestOfGoodies.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .sendMessage(sendMessage)
                .actionMessage(actionMessage)
                .chestItems(chestItems)
                .build();
    }
}
