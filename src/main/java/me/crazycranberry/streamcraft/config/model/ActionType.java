package me.crazycranberry.streamcraft.config.model;

import lombok.AllArgsConstructor;
import me.crazycranberry.streamcraft.actionexecutors.EntitySpawnExecutor;
import me.crazycranberry.streamcraft.actionexecutors.Executor;
import me.crazycranberry.streamcraft.actionexecutors.PotionEffectExecutor;
import me.crazycranberry.streamcraft.actionexecutors.RandomItemRemovalExecutor;
import me.crazycranberry.streamcraft.config.model.actions.EntitySpawn;
import me.crazycranberry.streamcraft.config.model.actions.PotionEffect;
import me.crazycranberry.streamcraft.config.model.actions.RandomItemRemoval;

@AllArgsConstructor
public enum ActionType {
    ENTITY_SPAWN("ENTITY_SPAWN", EntitySpawn.class, EntitySpawnExecutor.class),
    POTION_EFFECT("POTION_EFFECT", PotionEffect.class, PotionEffectExecutor.class),
    RANDOM_ITEM_REMOVAL("RANDOM_ITEM_REMOVAL", RandomItemRemoval.class, RandomItemRemovalExecutor.class);

    private String value;
    private Class<? extends Action> actionDefinition;
    private Class<? extends Executor> executor;

    public String value() {
        return value;
    }

    public Class<? extends Action> actionDefinition() {
        return actionDefinition;
    }

    public Class<? extends Executor> executor() {
        return executor;
    }

    public static ActionType fromValue(String actionType) {
        String a = actionType.toUpperCase().replaceAll("[^a-zA-Z]+", "_");

        for (ActionType b : ActionType.values()) {
            if (b.value.equals(a)) {
                return b;
            }
        }
        return null;
    }
}
