package me.crazycranberry.streamcraft.actions.entityspawn;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.config.ActionType;
import me.crazycranberry.streamcraft.config.Trigger;
import org.bukkit.entity.EntityType;

import java.util.LinkedHashMap;

import static me.crazycranberry.streamcraft.StreamCraft.logger;
import static me.crazycranberry.streamcraft.config.ActionUtils.anyNull;

@Getter
@Setter
@ToString(callSuper = true)
public class EntitySpawn extends Action {
    private EntityType entity;
    private Integer quantity;
    private Integer radiusFromPlayer;

    @Builder
    private EntitySpawn(ActionType type, Trigger trigger, String target, Boolean sendMessage, EntityType entity, Integer quantity, Integer radiusFromPlayer) {
        super(type, trigger, target, sendMessage);
        this.entity = entity;
        this.quantity = quantity;
        this.radiusFromPlayer = radiusFromPlayer;
    }

    @Override
    public String pollMessage() {
        return String.format("%s %s%s", getQuantity(), getEntity().name(), getQuantity() > 1 ?  "'s" : "");
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, Boolean sendMessage, LinkedHashMap<String, ?> input) {
        EntityType entity = validateEntity(input.get("entity"));
        Integer quantity = validateField(input.get("quantity"), Integer.class, "quantity");
        Integer radiusFromPlayer = validateField(input.get("radius_from_player"), Integer.class, "radius_from_player");
        if (anyNull(actionType, trigger, entity, quantity, radiusFromPlayer)) {
            return null;
        }
        return EntitySpawn.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .sendMessage(sendMessage)
                .entity(entity)
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
