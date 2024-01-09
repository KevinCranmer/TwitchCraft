package me.crazycranberry.streamcraft.actions;

import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.crazycranberry.streamcraft.StreamCraft.getPlugin;

public class ExecutorUtils {
    public static final long TICKS_PER_SECOND = 20L;

    public static List<Player> getTargetedPlayers(Action action) {
        List<String> targets = Arrays.asList(action.getTarget().split(","));
        return Bukkit.getOnlinePlayers().stream().filter(p -> targets.contains(p.getName()) || targets.contains("*")).map(OfflinePlayer::getPlayer).toList();
    }

    public static String triggerer(Message twitchMessage, Action action) {
        String userThatTriggered = twitchMessage.getPayload().getEvent().getUser_name();
        return userThatTriggered == null ? "a channel poll" : userThatTriggered + " triggering " + action.getTrigger().getType();
    }

    public static <T> T randomFromList(List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        int randomIndex = (int) (Math.random() * list.size());
        return list.get(randomIndex);
    }

    public static void maybeSendPlayerMessage(Player p, String message) {
        if (getPlugin().config().isSendMessageOnEvent()) {
            p.sendMessage(message);
        }
    }

    @NotNull
    public static List<Location> getPossibleSpawnLocations(Player p, int radius) {
        List<Location> possibleSpawnLocations = new ArrayList<>();
        double x = Math.floor(p.getLocation().getX()) + 0.5;
        double y = p.getLocation().getY();
        double z = Math.floor(p.getLocation().getZ()) + 0.5;
        for (int i = -radius; i < radius; i++) {
            for (int j = -radius; j < radius; j++) {
                for (int k = -radius; k < radius; k++) {
                    Location potentialLoc = new Location(p.getWorld(), x + i, y + j, z + k);
                    if (i == 0 && j == 0 && k == 0) {
                        continue; // We will always add the player location in the event that there are no valid spawns
                    }
                    if (isValidSpawnBlock(potentialLoc)) {
                        possibleSpawnLocations.add(potentialLoc);
                    }
                }
            }
        }
        possibleSpawnLocations.add(new Location(p.getWorld(), radius, y, z));
        return possibleSpawnLocations;
    }

    /** Makes sure there is a 1x2x1 box open at the given location. */
    public static boolean isValidSpawnBlock(Location loc) {
        Block block = loc.getBlock();
        Block blockAbove = loc.getBlock().getRelative(0, 1, 0);
        return (block.getType().equals(Material.AIR) || block.getType().equals(Material.WATER)) &&
                (blockAbove.getType().equals(Material.AIR) || blockAbove.getType().equals(Material.WATER));
    }
}
