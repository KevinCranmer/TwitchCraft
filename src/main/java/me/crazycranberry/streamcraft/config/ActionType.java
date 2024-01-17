package me.crazycranberry.streamcraft.config;

import lombok.AllArgsConstructor;
import me.crazycranberry.streamcraft.actions.buildahouse.BuildAHouse;
import me.crazycranberry.streamcraft.actions.buildahouse.BuildAHouseExecutor;
import me.crazycranberry.streamcraft.actions.cantstopwontstop.CantStopWontStop;
import me.crazycranberry.streamcraft.actions.cantstopwontstop.CantStopWontStopExecutor;
import me.crazycranberry.streamcraft.actions.chestofgoodies.ChestOfGoodiesExecutor;
import me.crazycranberry.streamcraft.actions.customcommand.CustomCommand;
import me.crazycranberry.streamcraft.actions.customcommand.CustomCommandExecutor;
import me.crazycranberry.streamcraft.actions.dropallitems.DropAllItemsExecutor;
import me.crazycranberry.streamcraft.actions.entityspawn.EntitySpawnExecutor;
import me.crazycranberry.streamcraft.actions.Executor;
import me.crazycranberry.streamcraft.actions.explosion.ExplosionExecutor;
import me.crazycranberry.streamcraft.actions.flyingcow.FlyingCowExecutor;
import me.crazycranberry.streamcraft.actions.giveitem.GiveItem;
import me.crazycranberry.streamcraft.actions.giveitem.GiveItemExecutor;
import me.crazycranberry.streamcraft.actions.megajump.MegaJumpExecutor;
import me.crazycranberry.streamcraft.actions.nojumping.NoJumpingExecutor;
import me.crazycranberry.streamcraft.actions.pinatachickens.PinataChickensExecutor;
import me.crazycranberry.streamcraft.actions.potioneffect.PotionEffectExecutor;
import me.crazycranberry.streamcraft.actions.raid.Raid;
import me.crazycranberry.streamcraft.actions.raid.RaidExecutor;
import me.crazycranberry.streamcraft.actions.randomitemremoval.RandomItemRemovalExecutor;
import me.crazycranberry.streamcraft.actions.rotatinghotbar.RotatingHotbar;
import me.crazycranberry.streamcraft.actions.rotatinghotbar.RotatingHotbarExecutor;
import me.crazycranberry.streamcraft.actions.sendtonether.SendToNether;
import me.crazycranberry.streamcraft.actions.sendtonether.SendToNetherExecutor;
import me.crazycranberry.streamcraft.actions.soupman.SoupMan;
import me.crazycranberry.streamcraft.actions.soupman.SoupManExecutor;
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
import me.crazycranberry.streamcraft.actions.weepingangel.WeepingAngel;
import me.crazycranberry.streamcraft.actions.weepingangel.WeepingAngelExecutor;

@AllArgsConstructor
public enum ActionType {
    BUILD_A_HOUSE("BUILD_A_HOUSE", BuildAHouse.class, BuildAHouseExecutor.class),
    CANT_STOP_WONT_STOP("CANT_STOP_WONT_STOP", CantStopWontStop.class, CantStopWontStopExecutor.class),
    CHEST_OF_GOODIES("CHEST_OF_GOODIES", ChestOfGoodies.class, ChestOfGoodiesExecutor.class),
    CUSTOM_COMMAND("CUSTOM_COMMAND", CustomCommand.class, CustomCommandExecutor.class),
    DROP_ALL_ITEMS("DROP_ALL_ITEMS", DropAllItems.class, DropAllItemsExecutor.class),
    ENTITY_SPAWN("ENTITY_SPAWN", EntitySpawn.class, EntitySpawnExecutor.class),
    EXPLOSION("EXPLOSION", Explosion.class, ExplosionExecutor.class),
    FLYING_COW("FLYING_COW", FlyingCow.class, FlyingCowExecutor.class),
    GIVE_ITEM("GIVE_ITEM", GiveItem.class, GiveItemExecutor.class),
    MEGA_JUMP("MEGA_JUMP", MegaJump.class, MegaJumpExecutor.class),
    NO_JUMPING("NO_JUMPING", NoJumping.class, NoJumpingExecutor.class),
    PINATA_CHICKENS("PINATA_CHICKENS", PinataChickens.class, PinataChickensExecutor.class),
    POTION_EFFECT("POTION_EFFECT", PotionEffect.class, PotionEffectExecutor.class),
    RAID("RAID", Raid.class, RaidExecutor.class),
    RANDOM_ITEM_REMOVAL("RANDOM_ITEM_REMOVAL", RandomItemRemoval.class, RandomItemRemovalExecutor.class),
    ROTATING_HOTBAR("ROTATING_HOTBAR", RotatingHotbar.class, RotatingHotbarExecutor.class),
    SEND_TO_NETHER("SEND_TO_NETHER", SendToNether.class, SendToNetherExecutor.class),
    SOUP_MAN("SOUP_MAN", SoupMan.class, SoupManExecutor.class),
    WATERLOG("WATERLOG", WaterLog.class, WaterLogExecutor.class),
    WEEPING_ANGEL("WEEPING_ANGEL", WeepingAngel.class, WeepingAngelExecutor.class);

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
