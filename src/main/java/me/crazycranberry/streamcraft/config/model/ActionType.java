package me.crazycranberry.streamcraft.config.model;

import lombok.AllArgsConstructor;
import me.crazycranberry.streamcraft.actionexecutors.ChestOfGoodiesExecutor;
import me.crazycranberry.streamcraft.actionexecutors.DropAllItemsExecutor;
import me.crazycranberry.streamcraft.actionexecutors.EntitySpawnExecutor;
import me.crazycranberry.streamcraft.actionexecutors.Executor;
import me.crazycranberry.streamcraft.actionexecutors.ExplosionExecutor;
import me.crazycranberry.streamcraft.actionexecutors.FlyingCowExecutor;
import me.crazycranberry.streamcraft.actionexecutors.MegaJumpExecutor;
import me.crazycranberry.streamcraft.actionexecutors.NoJumpingExecutor;
import me.crazycranberry.streamcraft.actionexecutors.PinataChickensExecutor;
import me.crazycranberry.streamcraft.actionexecutors.PotionEffectExecutor;
import me.crazycranberry.streamcraft.actionexecutors.RandomItemRemovalExecutor;
import me.crazycranberry.streamcraft.actionexecutors.WaterLogExecutor;
import me.crazycranberry.streamcraft.config.model.actions.ChestOfGoodies;
import me.crazycranberry.streamcraft.config.model.actions.DropAllItems;
import me.crazycranberry.streamcraft.config.model.actions.EntitySpawn;
import me.crazycranberry.streamcraft.config.model.actions.Explosion;
import me.crazycranberry.streamcraft.config.model.actions.FlyingCow;
import me.crazycranberry.streamcraft.config.model.actions.MegaJump;
import me.crazycranberry.streamcraft.config.model.actions.NoJumping;
import me.crazycranberry.streamcraft.config.model.actions.PinataChickens;
import me.crazycranberry.streamcraft.config.model.actions.PotionEffect;
import me.crazycranberry.streamcraft.config.model.actions.RandomItemRemoval;
import me.crazycranberry.streamcraft.config.model.actions.WaterLog;

@AllArgsConstructor
public enum ActionType {
    CHEST_OF_GOODIES("CHEST_OF_GOODIES", ChestOfGoodies.class, ChestOfGoodiesExecutor.class),
    DROP_ALL_ITEMS("DROP_ALL_ITEMS", DropAllItems.class, DropAllItemsExecutor.class),
    ENTITY_SPAWN("ENTITY_SPAWN", EntitySpawn.class, EntitySpawnExecutor.class),
    EXPLOSION("EXPLOSION", Explosion.class, ExplosionExecutor.class),
    FLYING_COW("FLYING_COW", FlyingCow.class, FlyingCowExecutor.class),
    MEGA_JUMP("MEGA_JUMP", MegaJump.class, MegaJumpExecutor.class),
    NO_JUMPING("NO_JUMPING", NoJumping.class, NoJumpingExecutor.class),
    PINATA_CHICKENS("PINATA_CHICKENS", PinataChickens.class, PinataChickensExecutor.class),
    POTION_EFFECT("POTION_EFFECT", PotionEffect.class, PotionEffectExecutor.class),
    RANDOM_ITEM_REMOVAL("RANDOM_ITEM_REMOVAL", RandomItemRemoval.class, RandomItemRemovalExecutor.class),
    WATERLOG("WATERLOG", WaterLog.class, WaterLogExecutor.class);

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
