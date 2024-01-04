package me.crazycranberry.streamcraft.actions.entityspawn;

import me.crazycranberry.streamcraft.actions.Executor;
import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.crazycranberry.streamcraft.StreamCraft.logger;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.getTargetedPlayers;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.maybeSendPlayerMessage;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.randomFromList;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.triggerer;

public class EntitySpawnExecutor implements Executor {
    @Override
    public void execute(Message twitchMessage, Action action) {
        if (!(action instanceof EntitySpawn)) {
            logger().warning("Somehow the following action was passed to " + this.getClass().getName() + ": " + action + "\nAborting execution.");
            return;
        }
        EntitySpawn es = (EntitySpawn) action;
        for (Player p : getTargetedPlayers(es)) {
            maybeSendPlayerMessage(p, String.format("Spawning %s%s's%s, courtesy of %s%s%s", ChatColor.GOLD, es.getEntity().name(), ChatColor.RESET, ChatColor.GOLD, triggerer(twitchMessage, action), ChatColor.RESET));
            double x = Math.floor(p.getLocation().getX()) + 0.5;
            double y = p.getLocation().getY();
            double z = Math.floor(p.getLocation().getZ()) + 0.5;
            List<Location> possibleSpawnLocations = new ArrayList<>();
            for (int i = -es.getRadiusFromPlayer(); i < es.getRadiusFromPlayer(); i++) {
                for (int j = -es.getRadiusFromPlayer(); j < es.getRadiusFromPlayer(); j++) {
                    for (int k = -es.getRadiusFromPlayer(); k < es.getRadiusFromPlayer(); k++) {
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
            possibleSpawnLocations.add(new Location(p.getWorld(), x, y, z));
            for (int l = 0; l < es.getQuantity(); l++) {
                Entity entity = p.getWorld().spawnEntity(randomFromList(possibleSpawnLocations), es.getEntity());
                if (twitchMessage.getPayload().getEvent().getUser_name() != null) {
                    entity.setCustomName(twitchMessage.getPayload().getEvent().getUser_name());
                    entity.setCustomNameVisible(true);
                }
            }
        }
    }

    /** Makes sure there is a 1x2x1 box open at the given location. */
    private boolean isValidSpawnBlock(Location loc) {
        Block blockAbove = loc.getBlock().getRelative(0, 1, 0);
        Block blockAboveAbove = loc.getBlock().getRelative(0, 2, 0);
        return (blockAbove.getType().equals(Material.AIR) || blockAbove.getType().equals(Material.WATER)) &&
                (blockAboveAbove.getType().equals(Material.AIR) || blockAboveAbove.getType().equals(Material.WATER));
    }
}
