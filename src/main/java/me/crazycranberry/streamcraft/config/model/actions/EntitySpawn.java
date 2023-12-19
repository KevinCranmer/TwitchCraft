package me.crazycranberry.streamcraft.config.model.actions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.crazycranberry.streamcraft.config.model.Action;
import me.crazycranberry.streamcraft.config.model.ActionType;
import me.crazycranberry.streamcraft.config.model.Trigger;
import org.bukkit.entity.EntityType;

import java.util.LinkedHashMap;

import static me.crazycranberry.streamcraft.StreamCraft.logger;
import static me.crazycranberry.streamcraft.config.model.ActionUtils.anyNull;

@Getter
@Setter
@ToString(callSuper = true)
public class EntitySpawn extends Action {
    private EntityType entity;
    private Integer quantity;
    private Integer radiusFromPlayer;

    @Builder
    private EntitySpawn(ActionType type, Trigger trigger, String target, EntityType entity, Integer quantity, Integer radiusFromPlayer) {
        super(type, trigger, target);
        this.entity = entity;
        this.quantity = quantity;
        this.radiusFromPlayer = radiusFromPlayer;
    }

    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, LinkedHashMap<String, ?> input) {
        EntityType entity = validateEntity(input.get("entity"));
        Integer quantity = validateQuantity(input.get("quantity"));
        Integer radiusFromPlayer = validateRadius(input.get("radius_from_player"));
        if (anyNull(actionType, trigger, entity, quantity, radiusFromPlayer)) {
            return null;
        }
        EntitySpawn e = EntitySpawn.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .entity(entity)
                .quantity(quantity)
                .radiusFromPlayer(radiusFromPlayer)
                .build();
        return e;
    }

    private static <T> Integer validateQuantity(T quantity) {
        if (!(quantity instanceof Integer)) {
            logger().warning("A quantity was not an Integer.");
            return null;
        }
        return (Integer) quantity;
    }

    private static <T> Integer validateRadius(T radius) {
        if (!(radius instanceof Integer)) {
            logger().warning("A radius_from_player was not an Integer.");
            return null;
        }
        return (Integer) radius;
    }

    private static <T> EntityType validateEntity(T entity) {
        if (!(entity instanceof String)) {
            logger().warning("An entity value was not a String.");
            return null;
        }
        try {
            EntityType type = EntityType.valueOf(((String) entity).toUpperCase().replaceAll("[^a-zA-Z]+", "_"));
            return type;
        } catch (IllegalArgumentException ex) {
            logger().warning("Unable to parse the entity name: " + entity);
            return null;
        }
    }
}
