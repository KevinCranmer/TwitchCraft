package me.crazycranberry.twitchcraft.actions.pinatachickens;

import me.crazycranberry.twitchcraft.actions.Executor;
import me.crazycranberry.twitchcraft.config.Action;
import me.crazycranberry.twitchcraft.twitch.websocket.model.message.Message;
import me.crazycranberry.twitchcraft.twitch.websocket.model.message.MessageEvent;
import me.crazycranberry.twitchcraft.twitch.websocket.model.message.MessagePayload;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static me.crazycranberry.twitchcraft.TwitchCraft.logger;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.getPossibleSpawnLocations;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.getTargetedPlayers;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.maybeSendPlayerMessage;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.randomFromList;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.triggerer;

public class PinataChickensExecutor implements Executor {
    public static final List<Consumer<Location>> nonItemGoodies = List.of(
        Goodies::spawnZombie,
        Goodies::spawnHorseAndSaddle,
        Goodies::spawnWater,
        Goodies::spawnSuperChargedCreeper,
        Goodies::spawnLava,
        Goodies::doNothing,
        Goodies::spawnArmorStand
    );

    @Override
    public void execute(Message twitchMessage, Action action) {
        if (!(action instanceof PinataChickens)) {
            logger().warning("Somehow the following action was passed to " + this.getClass().getName() + ": " + action + "\nAborting execution.");
            return;
        }
        PinataChickens pc = (PinataChickens) action;
        Integer numChickens = pc.getNumChickens();
        if (pc.getUseTriggerQuantity()) {
            Integer numBits = Optional.ofNullable(twitchMessage.getPayload()).map(MessagePayload::getEvent).map(MessageEvent::getBits).orElse(-1);
            Integer numGifts = Optional.ofNullable(twitchMessage.getPayload()).map(MessagePayload::getEvent).map(MessageEvent::getTotal).orElse(-1);
            numChickens = (int) (Math.max(numBits, numGifts) * pc.getQuantityFactor());
        }
        for (Player p : getTargetedPlayers(pc)) {
            maybeSendPlayerMessage(p, twitchMessage, String.format("Piñata chickens! Courtesy of %s%s%s", ChatColor.GOLD, triggerer(twitchMessage, action), ChatColor.RESET), action);
            List<Location> possibleSpawnLocations = getPossibleSpawnLocations(p, 5);
            for (int l = 0; l < numChickens; l++) {
                Chicken chicken = (Chicken) p.getWorld().spawnEntity(randomFromList(possibleSpawnLocations), EntityType.CHICKEN);
                chicken.setCustomName("Piñata");
                chicken.setCustomNameVisible(true);
            }
        }
    }

    public static class Goodies {
        public static final List<ItemStack> droppableItems = List.of(
                new ItemStack(Material.DIAMOND),
                new ItemStack(Material.BREAD, 3),
                new ItemStack(Material.FLINT),
                new ItemStack(Material.BOW),
                new ItemStack(Material.TADPOLE_BUCKET),
                new ItemStack(Material.KELP, 6),
                new ItemStack(Material.CARROT, 5),
                new ItemStack(Material.EGG),
                new ItemStack(Material.ENDER_PEARL),
                new ItemStack(Material.ENDER_EYE),
                new ItemStack(Material.FISHING_ROD),
                new ItemStack(Material.CROSSBOW),
                new ItemStack(Material.GLOW_ITEM_FRAME),
                new ItemStack(Material.LEAD, 2),
                new ItemStack(Material.MELON_SEEDS),
                new ItemStack(Material.TNT_MINECART),
                new ItemStack(Material.NETHER_WART),
                new ItemStack(Material.PAINTING),
                new ItemStack(Material.POTATO, 10),
                new ItemStack(Material.REDSTONE, 4),
                new ItemStack(Material.TRIDENT),
                new ItemStack(Material.WHEAT_SEEDS),
                new ItemStack(Material.ARROW, 32),
                new ItemStack(Material.APPLE, 2),
                new ItemStack(Material.BONE),
                new ItemStack(Material.COOKIE, 16),
                new ItemStack(Material.GOLDEN_HOE),
                new ItemStack(Material.DIAMOND_HORSE_ARMOR),
                new ItemStack(Material.MUSIC_DISC_PIGSTEP),
                new ItemStack(Material.NAME_TAG),
                new ItemStack(Material.EXPERIENCE_BOTTLE),
                new ItemStack(Material.GLASS_BOTTLE),
                new ItemStack(Material.SHEARS),
                new ItemStack(Material.SADDLE),
                new ItemStack(Material.SPYGLASS),
                new ItemStack(Material.COOKED_BEEF, 3),
                new ItemStack(Material.TURTLE_HELMET),
                new ItemStack(Material.PAPER),
                new ItemStack(Material.FEATHER),
                new ItemStack(Material.EMERALD),
                new ItemStack(Material.RAW_GOLD, 3),
                new ItemStack(Material.SHULKER_SHELL, 2),
                new ItemStack(Material.SLIME_BALL),
                new ItemStack(Material.TORCH, 10),
                new ItemStack(Material.FROG_SPAWN_EGG),
                new ItemStack(Material.STICK),
                new ItemStack(Material.COMPOSTER),
                new ItemStack(Material.BLACK_BANNER),
                new ItemStack(Material.PURPLE_DYE),
                new ItemStack(Material.NETHER_BRICK_WALL),
                new ItemStack(Material.GRASS_BLOCK),
                new ItemStack(Material.CHERRY_BOAT)
        );

        public static void dropItem(Location loc) {
            loc.getWorld().dropItem(loc, randomFromList(droppableItems));
        }

        public static void spawnZombie(Location loc) {
            loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
        }

        public static void spawnHorseAndSaddle(Location loc) {
            loc.getWorld().spawnEntity(loc, EntityType.HORSE);
            loc.getWorld().dropItem(loc, new ItemStack(Material.SADDLE));
        }

        public static void spawnWater(Location loc) {
            loc.getBlock().setType(Material.WATER);
        }

        public static void spawnLava(Location loc) {
            loc.getBlock().setType(Material.LAVA);
        }

        public static void spawnSuperChargedCreeper(Location loc) {
            Creeper creeper = (Creeper) loc.getWorld().spawnEntity(loc, EntityType.CREEPER);
            creeper.setPowered(true);
        }

        public static void doNothing(Location loc) { }

        public static void spawnArmorStand(Location loc) {
            ArmorStand armorStand = loc.getWorld().spawn(loc, ArmorStand.class);
            armorStand.setItem(EquipmentSlot.HEAD, randomFromList(armorHeads));
            armorStand.setItem(EquipmentSlot.CHEST, randomFromList(armorChests));
            armorStand.setItem(EquipmentSlot.LEGS, randomFromList(armorLegs));
            armorStand.setItem(EquipmentSlot.FEET, randomFromList(armorBoots));
        }

        private static final List<ItemStack> armorHeads = Arrays.asList(
                new ItemStack(Material.LEATHER_HELMET),
                new ItemStack(Material.LEATHER_HELMET),
                new ItemStack(Material.LEATHER_HELMET),
                new ItemStack(Material.LEATHER_HELMET),
                new ItemStack(Material.IRON_HELMET),
                new ItemStack(Material.IRON_HELMET),
                new ItemStack(Material.IRON_HELMET),
                new ItemStack(Material.GOLDEN_HELMET),
                new ItemStack(Material.GOLDEN_HELMET),
                new ItemStack(Material.GOLDEN_HELMET),
                new ItemStack(Material.DIAMOND_HELMET),
                new ItemStack(Material.DIAMOND_HELMET),
                new ItemStack(Material.NETHERITE_HELMET),
                null,
                null,
                null,
                null
        );

        private static final List<ItemStack> armorChests = Arrays.asList(
                new ItemStack(Material.LEATHER_CHESTPLATE),
                new ItemStack(Material.LEATHER_CHESTPLATE),
                new ItemStack(Material.LEATHER_CHESTPLATE),
                new ItemStack(Material.LEATHER_CHESTPLATE),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.GOLDEN_CHESTPLATE),
                new ItemStack(Material.GOLDEN_CHESTPLATE),
                new ItemStack(Material.GOLDEN_CHESTPLATE),
                new ItemStack(Material.DIAMOND_CHESTPLATE),
                new ItemStack(Material.DIAMOND_CHESTPLATE),
                new ItemStack(Material.NETHERITE_CHESTPLATE),
                null,
                null,
                null,
                null
        );

        private static final List<ItemStack> armorLegs = Arrays.asList(
                new ItemStack(Material.LEATHER_LEGGINGS),
                new ItemStack(Material.LEATHER_LEGGINGS),
                new ItemStack(Material.LEATHER_LEGGINGS),
                new ItemStack(Material.LEATHER_LEGGINGS),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.GOLDEN_LEGGINGS),
                new ItemStack(Material.GOLDEN_LEGGINGS),
                new ItemStack(Material.GOLDEN_LEGGINGS),
                new ItemStack(Material.DIAMOND_LEGGINGS),
                new ItemStack(Material.DIAMOND_LEGGINGS),
                new ItemStack(Material.NETHERITE_LEGGINGS),
                null,
                null,
                null,
                null
        );

        private static final List<ItemStack> armorBoots = Arrays.asList(
                new ItemStack(Material.LEATHER_BOOTS),
                new ItemStack(Material.LEATHER_BOOTS),
                new ItemStack(Material.LEATHER_BOOTS),
                new ItemStack(Material.LEATHER_BOOTS),
                new ItemStack(Material.IRON_BOOTS),
                new ItemStack(Material.IRON_BOOTS),
                new ItemStack(Material.IRON_BOOTS),
                new ItemStack(Material.GOLDEN_BOOTS),
                new ItemStack(Material.GOLDEN_BOOTS),
                new ItemStack(Material.GOLDEN_BOOTS),
                new ItemStack(Material.DIAMOND_BOOTS),
                new ItemStack(Material.DIAMOND_BOOTS),
                new ItemStack(Material.NETHERITE_BOOTS),
                null,
                null,
                null,
                null
        );
    }
}
