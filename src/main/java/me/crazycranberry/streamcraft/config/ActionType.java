package me.crazycranberry.streamcraft.config;

import lombok.AllArgsConstructor;
import me.crazycranberry.streamcraft.actions.buildahouse.BuildAHouse;
import me.crazycranberry.streamcraft.actions.buildahouse.BuildAHouseExecutor;
import me.crazycranberry.streamcraft.actions.cantstopwontstop.CantStopWontStop;
import me.crazycranberry.streamcraft.actions.cantstopwontstop.CantStopWontStopExecutor;
import me.crazycranberry.streamcraft.actions.chestofgoodies.ChestOfGoodiesExecutor;
import me.crazycranberry.streamcraft.actions.dropallitems.DropAllItemsExecutor;
import me.crazycranberry.streamcraft.actions.entityspawn.EntitySpawnExecutor;
import me.crazycranberry.streamcraft.actions.Executor;
import me.crazycranberry.streamcraft.actions.explosion.ExplosionExecutor;
import me.crazycranberry.streamcraft.actions.flyingcow.FlyingCowExecutor;
import me.crazycranberry.streamcraft.actions.megajump.MegaJumpExecutor;
import me.crazycranberry.streamcraft.actions.nojumping.NoJumpingExecutor;
import me.crazycranberry.streamcraft.actions.pinatachickens.PinataChickensExecutor;
import me.crazycranberry.streamcraft.actions.potioneffect.PotionEffectExecutor;
import me.crazycranberry.streamcraft.actions.randomitemremoval.RandomItemRemovalExecutor;
import me.crazycranberry.streamcraft.actions.waterlog.WaterLogExecutor;
import me.crazycranberry.streamcraft.actions.chestofgoodies.ChestOfGoodies;
import me.crazycranberry.streamcraft.actions.dropallitems.DropAllItems;
import me.crazycranberry.streamcraft.actions.entityspawn.EntitySpawn;
import me.crazycranberry.streamcraft.actions.explosion.Explosion;
import me.crazycranberry.streamcraft.actions.flyingcow.FlyingCow;
import me.crazycranberry.streamcraft.actions.megajump.MegaJump;
import me.crazycranberry.streamcraft.actions.nojumping.NoJumping;
import me.crazycranberry.streamcraft.actions.pinatachickens.PinataChickens;
import me.crazycranberry.streamcraft.actions.potioneffect.PotionEffect;
import me.crazycranberry.streamcraft.actions.randomitemremoval.RandomItemRemoval;
import me.crazycranberry.streamcraft.actions.waterlog.WaterLog;

@AllArgsConstructor
public enum ActionType {
    BUILD_A_HOUSE("BUILD_A_HOUSE", BuildAHouse.class, BuildAHouseExecutor.class),
    CANT_STOP_WONT_STOP("CANT_STOP_WONT_STOP", CantStopWontStop.class, CantStopWontStopExecutor.class),
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
