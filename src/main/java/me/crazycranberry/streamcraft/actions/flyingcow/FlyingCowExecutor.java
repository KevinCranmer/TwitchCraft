package me.crazycranberry.streamcraft.actions.flyingcow;

import me.crazycranberry.streamcraft.actions.Executor;
import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.crazycranberry.streamcraft.StreamCraft.getPlugin;
import static me.crazycranberry.streamcraft.StreamCraft.logger;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.TICKS_PER_SECOND;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.getTargetedPlayers;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.maybeSendPlayerMessage;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.randomFromList;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.triggerer;
import static org.bukkit.Sound.ENTITY_COW_AMBIENT;

public class FlyingCowExecutor implements Executor {
    private int ySpawnRange = 5;
    private int mainTaskId;
    private int cowsLeft;
    private Map<Cow, Integer> cowTaskIdMap = new HashMap<>();

    @Override
    public void execute(Message twitchMessage, Action action) {
        if (!(action instanceof FlyingCow)) {
            logger().warning("Somehow the following action was passed to " + this.getClass().getName() + ": " + action + "\nAborting execution.");
            return;
        }
        FlyingCow fc = (FlyingCow) action;
        cowsLeft = fc.getNumCows();
        for (Player p : getTargetedPlayers(fc)) {
            maybeSendPlayerMessage(p, String.format("You better Moooove away from the exploding cows! Courtesy of %s%s%s", ChatColor.GOLD, triggerer(twitchMessage, action), ChatColor.RESET), action);
            mainTaskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
                if (cowsLeft > 0) {
                    sendCow(p, fc);
                    cowsLeft--;
                } else {
                    maybeSendPlayerMessage(p, "No more cows", action);
                    Bukkit.getScheduler().cancelTask(mainTaskId);
                }
            }, 0 /*<-- the initial delay */, fc.getSecondsBetweenCows() * TICKS_PER_SECOND /*<-- the interval */).getTaskId();
        }
    }

    private void sendCow(Player p, FlyingCow fc) {
        p.getWorld().playSound(p.getLocation(), ENTITY_COW_AMBIENT, 1, 1);
        Location pLoc = p.getLocation().clone();
        Vector spawnOffset = randomFromList(possibleSpawnOffsets(fc.getDistanceFromPlayer(), p));
        if (spawnOffset == null) {
            return;
        }
        Location spawnLoc = pLoc.add(spawnOffset);
        Cow cow = (Cow) pLoc.getWorld().spawnEntity(spawnLoc, EntityType.COW);
        Vector cowVel = cowVelocity(spawnOffset, fc.getCowVelocity());
        cow.setVelocity(cowVel);
        cow.setMetadata("flyingcow", new FixedMetadataValue(getPlugin(), "true"));
        cowTaskIdMap.put(cow, Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
            if (blocksAroundEntity(cow, 0.75).stream().anyMatch(b -> b.getType() != Material.AIR && b.getType() != Material.WATER)) {
                Integer taskId = cowTaskIdMap.get(cow);
                cow.getWorld().createExplosion(cow.getLocation(), 4);
                if (!cow.isDead()) {
                    cow.damage(10000);
                }
                if (taskId != null) {
                    cowTaskIdMap.remove(cow);
                    Bukkit.getScheduler().cancelTask(taskId);
                }
            } else {
                cow.setVelocity(new Vector(cowVel.getX(), cow.getVelocity().getY(), cowVel.getZ()));
            }
            //Try to cancel any tasks that are lingering (shouldn't ever happen but just in case)
            cowTaskIdMap.entrySet().stream().filter(e -> e.getKey().isDead()).forEach(e -> Bukkit.getScheduler().cancelTask(e.getValue()));
        }, 3 /*<-- the initial delay */, 1 /*<-- the interval */).getTaskId());
    }

    private List<Block> blocksAroundEntity(Entity e, double offset) {
        List<Block> blocks = new ArrayList<>();
        for (int i = -1; i < 1; i++) {
            for (int j = -1; j < 1; j++) {
                for (int k = -1; k < 1; k++) {
                    blocks.add(e.getLocation().add(new Vector(offset * i, offset * j, offset * k)).getBlock());
                }
            }
        }
        return blocks;
    }

    private List<Vector> possibleSpawnOffsets(int distanceFromPlayer, Player p) {
        List<Vector> possibleSpawnOffsets = new ArrayList<>();
        for (int i = -distanceFromPlayer; i < distanceFromPlayer; i++) {
            for (int k = -ySpawnRange; k < ySpawnRange; k++) {
                Vector potentialOffset1 = new Vector(i, k, distanceFromPlayer);
                Vector potentialOffset2 = new Vector(i, k, -distanceFromPlayer);
                if (isValidSpawnBlock(p.getLocation().add(new Vector(potentialOffset1.getX(), potentialOffset1.getY(), potentialOffset1.getZ())))) {
                    possibleSpawnOffsets.add(potentialOffset1);
                }
                if (isValidSpawnBlock(p.getLocation().add(new Vector(potentialOffset2.getX(), potentialOffset2.getY(), potentialOffset2.getZ())))) {
                    possibleSpawnOffsets.add(potentialOffset2);
                }
            }
        }
        for (int j = -distanceFromPlayer; j < distanceFromPlayer; j++) {
            for (int k = -ySpawnRange; k < ySpawnRange; k++) {
                Vector potentialOffset1 = new Vector(distanceFromPlayer, k, j);
                Vector potentialOffset2 = new Vector(-distanceFromPlayer, k, j);
                if (isValidSpawnBlock(p.getLocation().add(new Vector(potentialOffset1.getX(), potentialOffset1.getY(), potentialOffset1.getZ())))) {
                    possibleSpawnOffsets.add(potentialOffset1);
                }
                if (isValidSpawnBlock(p.getLocation().add(new Vector(potentialOffset2.getX(), potentialOffset2.getY(), potentialOffset2.getZ())))) {
                    possibleSpawnOffsets.add(potentialOffset2);
                }
            }
        }
        return possibleSpawnOffsets;
    }

    private Vector cowVelocity(Vector spawnOffset, double xzVelocity) {
        double distanceFromPlayer = Math.sqrt(spawnOffset.getX() * spawnOffset.getX() + spawnOffset.getZ() * spawnOffset.getZ()); //good ol fashion trig
        double numTicksTillDestination = distanceFromPlayer / xzVelocity;
        double yVel = (0.045 * numTicksTillDestination * numTicksTillDestination - spawnOffset.getY()) / numTicksTillDestination;
        Vector xzVector = new Vector(spawnOffset.getX(), 0, spawnOffset.getZ());
        xzVector = xzVector.normalize().multiply(-xzVelocity);
        return new Vector(xzVector.getX(), yVel, xzVector.getZ());
    }

    /** Makes sure there is a 1x1x1 box open at the given location. */
    private boolean isValidSpawnBlock(Location loc) {
        Block blockAbove = loc.getBlock().getRelative(0, 1, 0);
        return blockAbove.getType().equals(Material.AIR) || blockAbove.getType().equals(Material.WATER);
    }
}
