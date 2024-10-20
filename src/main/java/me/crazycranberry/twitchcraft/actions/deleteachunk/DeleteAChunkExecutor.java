package me.crazycranberry.twitchcraft.actions.deleteachunk;

import me.crazycranberry.twitchcraft.actions.Executor;
import me.crazycranberry.twitchcraft.config.Action;
import me.crazycranberry.twitchcraft.twitch.websocket.model.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import static me.crazycranberry.twitchcraft.TwitchCraft.getPlugin;
import static me.crazycranberry.twitchcraft.TwitchCraft.logger;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.getPossibleSpawnLocations;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.getTargetedPlayers;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.maybeSendPlayerMessage;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.triggerer;

public class DeleteAChunkExecutor implements Executor {
    private Integer mainTaskId;
    private static double startY;
    private static double endY;

    @Override
    public void execute(Message twitchMessage, Action action) {
        if (!(action instanceof DeleteAChunk dac)) {
            logger().warning("Somehow the following action was passed to " + this.getClass().getName() + ": " + action + "\nAborting execution.");
            return;
        }
        if (mainTaskId != null) {
            logger().warning("Tried to delete a chunk while another chunk was being deleted. Skipping.");
        }
        for (Player p : getTargetedPlayers(dac)) {
            maybeSendPlayerMessage(p, twitchMessage, String.format("A random chunk nearby is being removed! Courtesy of %s%s%s", ChatColor.GOLD, triggerer(twitchMessage, action), ChatColor.RESET), action);
            int chunkOffsetX = (int) (Math.random() * (dac.getRadius() * 2 + 1)) - dac.getRadius();
            int chunkOffsetZ = (int) (Math.random() * (dac.getRadius() * 2 + 1)) - dac.getRadius();
            startY = p.getWorld().getMaxHeight();
            endY = p.getLocation().getY();
            Chunk toBeDeleted = p.getLocation().add(chunkOffsetX * 16, 0, chunkOffsetZ * 16).getChunk();
            mainTaskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
                if (!deleteAllAndReturnFalseIfAllBedrock(toBeDeleted, (int) startY, (int) endY)) {
                    Bukkit.getScheduler().cancelTask(mainTaskId);
                }
                startY = endY;
                endY -= dac.getRowsPerTick();
            }, 0 /*<-- the initial delay */, 1 /*<-- the interval */).getTaskId();
        }
    }

    private boolean deleteAllAndReturnFalseIfAllBedrock(Chunk toBeDeleted, int startingY, int stoppingY) {
        while (startingY > stoppingY) {
            int numBedrock = 0;
            for (int x = 0; x < 15; x++) {
                for (int z = 0; z < 15; z++) {
                    Block byebyeBlock = toBeDeleted.getBlock(x, startingY, z);
                    if (byebyeBlock.getType().equals(Material.BEDROCK)) {
                        numBedrock++;
                    } else {
                        byebyeBlock.setType(Material.AIR);
                    }
                }
            }
            startingY--;
            if (numBedrock >= 64) {
                return false;
            }
        }
        return true;
    }
}
