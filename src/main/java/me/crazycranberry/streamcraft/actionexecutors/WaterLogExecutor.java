package me.crazycranberry.streamcraft.actionexecutors;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.crazycranberry.streamcraft.config.model.Action;
import me.crazycranberry.streamcraft.config.model.actions.WaterLog;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

import static me.crazycranberry.streamcraft.StreamCraft.getPlugin;
import static me.crazycranberry.streamcraft.StreamCraft.logger;
import static me.crazycranberry.streamcraft.actionexecutors.ExecutorUtils.TICKS_PER_SECOND;
import static me.crazycranberry.streamcraft.actionexecutors.ExecutorUtils.getTargetedPlayers;
import static me.crazycranberry.streamcraft.actionexecutors.ExecutorUtils.maybeSendPlayerMessage;
import static me.crazycranberry.streamcraft.actionexecutors.ExecutorUtils.triggerer;

public class WaterLogExecutor implements Executor {
    private final static long numTicksBetweenChecks = 2L;
    Map<Player, WaterLogStats> waterLogStats = new HashMap<>();

    @Override
    public void execute(Message twitchMessage, Action action) {
        if (!(action instanceof WaterLog)) {
            logger().warning("Somehow the following action was passed to " + this.getClass().getName() + ": " + action + "\nAborting execution.");
            return;
        }
        WaterLog wl = (WaterLog) action;
        for (Player p : getTargetedPlayers(wl)) {
            maybeSendPlayerMessage(p, String.format("You've been %swaterlogged!!%s Courtesy of %s%s%s", ChatColor.GOLD, ChatColor.RESET, ChatColor.GOLD, triggerer(twitchMessage, action), ChatColor.RESET));
            if (waterLogStats.containsKey(p)) {
                waterLogStats.get(p).setWaterLogTimeRemaining(wl.getDurationSeconds());
            } else {
                waterLogStats.put(p, WaterLogStats.builder()
                        .previousWaterBlock(null)
                        .waterLogTimeRemaining((int) (((double)TICKS_PER_SECOND / (double)numTicksBetweenChecks) * wl.getDurationSeconds()))
                        .taskId(null)
                        .build());
                int taskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
                    WaterLogStats wls = waterLogStats.get(p);
                    if (wls.getWaterLogTimeRemaining() >= 0) {
                        Block blockAtPlayerLegs = p.getLocation().getBlock();
                        if (blockAtPlayerLegs.getType().equals(Material.AIR)) {
                            blockAtPlayerLegs.setType(Material.WATER);
                            Location previousWaterLocation = wls.getPreviousWaterBlock();
                            if (previousWaterLocation != null && previousWaterLocation.getBlock().getType().equals(Material.WATER)) {
                                previousWaterLocation.getBlock().setType(Material.AIR);
                            }
                            wls.setPreviousWaterBlock(blockAtPlayerLegs.getLocation());
                        }
                        wls.setWaterLogTimeRemaining(wls.getWaterLogTimeRemaining() - 1);
                    } else {
                        Bukkit.getScheduler().cancelTask(wls.getTaskId());
                        if (wls.getPreviousWaterBlock() != null && wls.getPreviousWaterBlock().getBlock().getType().equals(Material.WATER)) {
                            wls.getPreviousWaterBlock().getBlock().setType(Material.AIR);
                        }
                        waterLogStats.remove(p);
                    }
                }, 0 /*<-- the initial delay */, numTicksBetweenChecks /*<-- the interval */).getTaskId();
                waterLogStats.get(p).setTaskId(taskId);
            }
        }
    }

    @Getter
    @Setter
    @Builder
    private static class WaterLogStats {
        private Location previousWaterBlock;
        private Integer waterLogTimeRemaining;
        private Integer taskId;
    }
}
