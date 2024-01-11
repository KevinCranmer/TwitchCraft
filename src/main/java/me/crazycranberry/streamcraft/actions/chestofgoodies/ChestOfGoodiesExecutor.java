package me.crazycranberry.streamcraft.actions.chestofgoodies;

import me.crazycranberry.streamcraft.actions.Executor;
import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static me.crazycranberry.streamcraft.StreamCraft.getPlugin;
import static me.crazycranberry.streamcraft.StreamCraft.logger;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.getTargetedPlayers;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.maybeSendPlayerMessage;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.triggerer;

public class ChestOfGoodiesExecutor implements Executor {
    private static Set<Player> protectedPlayers = new HashSet<>();

    @Override
    public void execute(Message twitchMessage, Action action) {
        if (!(action instanceof ChestOfGoodies)) {
            logger().warning("Somehow the following action was passed to " + this.getClass().getName() + ": " + action + "\nAborting execution.");
            return;
        }
        ChestOfGoodies cog = (ChestOfGoodies) action;
        for (Player p : getTargetedPlayers(cog)) {
            maybeSendPlayerMessage(p, String.format("A chest of goodies! Courtesy of %s%s%s", ChatColor.GOLD, triggerer(twitchMessage, cog), ChatColor.RESET), action);
            protectedPlayers.add(p);
            removeFromProtectedPlayersIn5Seconds(p);
            Vector offset = getOffsetFromDirection(p.getLocation().getDirection());
            Location spawnLoc = p.getLocation().clone().add(offset);
            p.getWorld().strikeLightning(spawnLoc);
            Block chestBlock = spawnLoc.getBlock();
            chestBlock.setType(Material.CHEST);
            setChestContents((Chest) chestBlock.getState(), cog);
        }
    }

    private void removeFromProtectedPlayersIn5Seconds(Player p) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        Bukkit.getServer().getScheduler().callSyncMethod(getPlugin(), () -> {
                            protectedPlayers.remove(p);
                            return true;
                        });
                    }
                },
                5000
        );
    }

    private void setChestContents(Chest chest, ChestOfGoodies cog) {
        Inventory inv = chest.getInventory();
        List<Integer> availableChestSlots = new ArrayList<>(IntStream.range(0, inv.getSize()).boxed().toList());
        for (ChestItem chestItem : cog.getChestItems()) {
            double rand = Math.random();
            if (rand < chestItem.getChance()) {
                int quantity = (int)(Math.random() * (chestItem.getMax() - chestItem.getMin())) + chestItem.getMin();
                for (int i = quantity; i > 0; i = i - chestItem.getMaterial().getMaxStackSize()) {
                    int stackSize = Math.min(i, chestItem.getMaterial().getMaxStackSize());
                    if (availableChestSlots.size() == 0) {
                        return;
                    }
                    int availableChestSlotsIndex = (int)(Math.random() * availableChestSlots.size());
                    int chestIndex = availableChestSlots.get(availableChestSlotsIndex);
                    availableChestSlots.remove(availableChestSlotsIndex);
                    inv.setItem(chestIndex, new ItemStack(chestItem.getMaterial(), stackSize));
                }
            }
        }
    }

    private Vector getOffsetFromDirection(Vector direction) {
        double x = direction.getX();
        double z = direction.getZ();
        if (Math.abs(x) > Math.abs(z)) {
            if (x < 0) {
                return new Vector(-3, 1, 0);
            } else {
                return new Vector(3, 1, 0);
            }
        } else {
            if (z < 0) {
                return new Vector(0, 1, -3);
            } else {
                return new Vector(0, 1, 3);
            }
        }
    }

    public static boolean protectPlayer(Player p) {
        if (protectedPlayers.contains(p)) {
            return true;
        }
        return false;
    }
}
