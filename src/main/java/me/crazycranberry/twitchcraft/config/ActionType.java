package me.crazycranberry.twitchcraft.config;

import lombok.AllArgsConstructor;
import me.crazycranberry.twitchcraft.actions.buildahouse.BuildAHouse;
import me.crazycranberry.twitchcraft.actions.buildahouse.BuildAHouseExecutor;
import me.crazycranberry.twitchcraft.actions.cantstopwontstop.CantStopWontStop;
import me.crazycranberry.twitchcraft.actions.cantstopwontstop.CantStopWontStopExecutor;
import me.crazycranberry.twitchcraft.actions.chestofgoodies.ChestOfGoodiesExecutor;
import me.crazycranberry.twitchcraft.actions.customcommand.CustomCommand;
import me.crazycranberry.twitchcraft.actions.customcommand.CustomCommandExecutor;
import me.crazycranberry.twitchcraft.actions.dropallitems.DropAllItemsExecutor;
import me.crazycranberry.twitchcraft.actions.entityspawn.EntitySpawnExecutor;
import me.crazycranberry.twitchcraft.actions.Executor;
import me.crazycranberry.twitchcraft.actions.explosion.ExplosionExecutor;
import me.crazycranberry.twitchcraft.actions.flyingcow.FlyingCowExecutor;
import me.crazycranberry.twitchcraft.actions.giveitem.GiveItem;
import me.crazycranberry.twitchcraft.actions.giveitem.GiveItemExecutor;
import me.crazycranberry.twitchcraft.actions.pinatachickens.PinataChickensExecutor;
import me.crazycranberry.twitchcraft.actions.potioneffect.PotionEffectExecutor;
import me.crazycranberry.twitchcraft.actions.raid.Raid;
import me.crazycranberry.twitchcraft.actions.raid.RaidExecutor;
import me.crazycranberry.twitchcraft.actions.randomitemremoval.RandomItemRemovalExecutor;
import me.crazycranberry.twitchcraft.actions.rotatinghotbar.RotatingHotbar;
import me.crazycranberry.twitchcraft.actions.rotatinghotbar.RotatingHotbarExecutor;
import me.crazycranberry.twitchcraft.actions.sendtonether.SendToNether;
import me.crazycranberry.twitchcraft.actions.sendtonether.SendToNetherExecutor;
import me.crazycranberry.twitchcraft.actions.waterlog.WaterLogExecutor;
import me.crazycranberry.twitchcraft.actions.chestofgoodies.ChestOfGoodies;
import me.crazycranberry.twitchcraft.actions.dropallitems.DropAllItems;
import me.crazycranberry.twitchcraft.actions.entityspawn.EntitySpawn;
import me.crazycranberry.twitchcraft.actions.explosion.Explosion;
import me.crazycranberry.twitchcraft.actions.flyingcow.FlyingCow;
import me.crazycranberry.twitchcraft.actions.pinatachickens.PinataChickens;
import me.crazycranberry.twitchcraft.actions.potioneffect.PotionEffect;
import me.crazycranberry.twitchcraft.actions.randomitemremoval.RandomItemRemoval;
import me.crazycranberry.twitchcraft.actions.waterlog.WaterLog;
import me.crazycranberry.twitchcraft.actions.weepingangel.WeepingAngel;
import me.crazycranberry.twitchcraft.actions.weepingangel.WeepingAngelExecutor;

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
    PINATA_CHICKENS("PINATA_CHICKENS", PinataChickens.class, PinataChickensExecutor.class),
    POTION_EFFECT("POTION_EFFECT", PotionEffect.class, PotionEffectExecutor.class),
    RAID("RAID", Raid.class, RaidExecutor.class),
    RANDOM_ITEM_REMOVAL("RANDOM_ITEM_REMOVAL", RandomItemRemoval.class, RandomItemRemovalExecutor.class),
    ROTATING_HOTBAR("ROTATING_HOTBAR", RotatingHotbar.class, RotatingHotbarExecutor.class),
    SEND_TO_NETHER("SEND_TO_NETHER", SendToNether.class, SendToNetherExecutor.class),
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
