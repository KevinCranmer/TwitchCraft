package me.crazycranberry.twitchcraft.actions.pinatachickens;

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
public class PinataChickens extends Action {
    private Integer numChickens;
    private Boolean useTriggerQuantity;
    private Double quantityFactor;

    @Builder
    private PinataChickens(ActionType type, Trigger trigger, String target, String actionMessage, Boolean sendMessage, Integer numChickens, Boolean useTriggerQuantity, Double quantityFactor) {
        super(type, trigger, target, actionMessage, sendMessage);
        this.numChickens = numChickens;
        this.useTriggerQuantity = useTriggerQuantity;
        this.quantityFactor = quantityFactor;
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
        Boolean useTriggerQuantity = validateField(input.get("use_trigger_quantity"), Boolean.class, "use_trigger_quantity", false);
        if (useTriggerQuantity == null) useTriggerQuantity = false;
        Double quantityFactor = validateField(input.get("quantity_factor"), Double.class, "quantity_factor", false);
        if (useTriggerQuantity) {
            if (!List.of(SUB_GIFT, CHANNEL_CHEER).contains(trigger.getType())) {
                logger().warning("use_trigger_quantity cannot be true unless the trigger type is SUB_GIFT or CHANNEL_CHEER.");
                return null;
            }
            if (quantityFactor == null) {
                logger().warning("use_trigger_quantity cannot be true unless quantity_factor is set.");
                return null;
            }
        } else if (numChickens == null) {
            logger().warning("The num_chickens field must be set or use_trigger_quantity, and quantity_factor must be set.");
            return null;
        }
        return PinataChickens.builder()
            .type(actionType)
            .trigger(trigger)
            .target(target)
            .sendMessage(sendMessage)
            .actionMessage(actionMessage)
            .numChickens(numChickens)
            .useTriggerQuantity(useTriggerQuantity)
            .quantityFactor(quantityFactor)
            .build();
    }
}
