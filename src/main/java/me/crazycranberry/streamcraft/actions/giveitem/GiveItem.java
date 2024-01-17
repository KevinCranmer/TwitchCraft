package me.crazycranberry.streamcraft.actions.giveitem;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.config.ActionType;
import me.crazycranberry.streamcraft.config.Trigger;
import org.bukkit.Material;

import java.util.LinkedHashMap;

import static me.crazycranberry.streamcraft.StreamCraft.logger;

@Getter
@Setter
@ToString(callSuper = true)
public class GiveItem extends Action {
    private Material item;
    private Integer quantity;

    @Builder
    private GiveItem(ActionType type, Trigger trigger, String target, String actionMessage, Boolean sendMessage, Material item, Integer quantity) {
        super(type, trigger, target, actionMessage, sendMessage);
        this.item = item;
        this.quantity = quantity;
    }

    @Override
    public String pollMessage() {
        if (this.getTrigger().getPollMessage() != null) {
            return this.getTrigger().getPollMessage();
        }
        return String.format("Give %s %s%s", getQuantity(), getItem().name().replace("_", " ").toLowerCase(), getQuantity() > 1 ?  "'s" : "");
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, String actionMessage, Boolean sendMessage, LinkedHashMap<String, ?> input) {
        Material item = validateItem(input.get("item"));
        Integer quantity = validateField(input.get("quantity"), Integer.class, "quantity");
        if (item == null || quantity == null) {
            logger().warning("Either the item or quantity field is missing for a GiveItem action");
            return null;
        }
        return GiveItem.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .sendMessage(sendMessage)
                .actionMessage(actionMessage)
                .item(item)
                .quantity(quantity)
                .build();
    }

    private static <T> Material validateItem(T item) {
        if (!(item instanceof String)) {
            logger().warning("An item value was not a String.");
            return null;
        }
        try {
            return Material.valueOf(((String) item).toUpperCase().replaceAll("[^a-zA-Z]+", "_"));
        } catch (IllegalArgumentException ex) {
            logger().warning("Unable to parse the item name: " + item);
            return null;
        }
    }
}
