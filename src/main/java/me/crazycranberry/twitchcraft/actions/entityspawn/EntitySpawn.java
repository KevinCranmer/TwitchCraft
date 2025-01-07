package me.crazycranberry.twitchcraft.actions.entityspawn;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.crazycranberry.twitchcraft.config.Action;
import me.crazycranberry.twitchcraft.config.ActionType;
import me.crazycranberry.twitchcraft.config.Trigger;
import me.crazycranberry.twitchcraft.config.TriggerType;
import org.bukkit.entity.EntityType;

import java.util.LinkedHashMap;

import static me.crazycranberry.twitchcraft.TwitchCraft.logger;
import static me.crazycranberry.twitchcraft.config.ActionUtils.anyNull;

@Getter
@Setter
@ToString(callSuper = true)
public class EntitySpawn extends Action {
    private EntityType entity;
    private Integer quantity;
    private boolean useTriggerQuantity;
    private Double quantityFactor;
    private Integer radiusFromPlayer;
    private boolean isBaby;

    @Builder
    private EntitySpawn(ActionType type, Trigger trigger, String target, String actionMessage, Boolean sendMessage, EntityType entity, boolean useTriggerQuantity, Double quantityFactor, boolean isBaby, Integer quantity, Integer radiusFromPlayer) {
        super(type, trigger, target, actionMessage, sendMessage);
        this.entity = entity;
        this.quantity = quantity;
        this.useTriggerQuantity = useTriggerQuantity;
        this.quantityFactor = quantityFactor;
        this.isBaby = isBaby;
        this.radiusFromPlayer = radiusFromPlayer;
    }

    @Override
    public String pollMessage() {
        if (this.getTrigger().getPollMessage() != null) {
            return this.getTrigger().getPollMessage();
        }
        return String.format("%s %s%s", getQuantity(), getEntity().name().toLowerCase().replace("_", " "), getQuantity() > 1 ?  "'s" : "");
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, String actionMessage, Boolean sendMessage, LinkedHashMap<String, ?> input) {
        EntityType entity = validateEntity(input.get("entity"));
        Integer quantity = validateField(input.get("quantity"), Integer.class, "quantity", false);
        Boolean useTriggerQuantity = validateField(input.get("use_trigger_quantity"), Boolean.class, "use_trigger_quantity", false);
        Double quantityFactor = validateField(input.get("quantity_factor"), Double.class, "quantity_factor", false);
        Boolean isBaby = validateField(input.get("is_baby"), Boolean.class, "is_baby", false);
        Integer radiusFromPlayer = validateField(input.get("radius_from_player"), Integer.class, "radius_from_player");
        if (anyNull(actionType, trigger, entity, radiusFromPlayer)) {
            return null;
        }
        if (useTriggerQuantity != null && useTriggerQuantity && !(trigger.getType().equals(TriggerType.CHANNEL_CHEER) || trigger.getType().equals(TriggerType.SUB_GIFT))) {
            logger().warning(String.format("Cannot create action %s because use_trigger_quantity was true but trigger.type was not CHANNEL_CHEER or SUB_GIFT", actionType));
            return null;
        }
        if ((useTriggerQuantity == null || !useTriggerQuantity) && quantity == null) {
            logger().warning(String.format("Cannot create action %s because use_trigger_quantity was false but quantity was empty", actionType));
            return null;
        }
        return EntitySpawn.builder()
            .type(actionType)
            .trigger(trigger)
            .target(target)
            .sendMessage(sendMessage)
            .actionMessage(actionMessage)
            .entity(entity)
            .useTriggerQuantity(useTriggerQuantity != null && useTriggerQuantity)
            .quantityFactor(quantityFactor)
            .isBaby(isBaby != null && isBaby)
            .quantity(quantity)
            .radiusFromPlayer(radiusFromPlayer)
            .build();
    }

    private static <T> EntityType validateEntity(T entity) {
        if (!(entity instanceof String)) {
            logger().warning("An entity value was not a String.");
            return null;
        }
        try {
            return EntityType.valueOf(((String) entity).toUpperCase().replaceAll("[^a-zA-Z]+", "_"));
        } catch (IllegalArgumentException ex) {
            logger().warning("Unable to parse the entity name: " + entity);
            return null;
        }
    }
}
