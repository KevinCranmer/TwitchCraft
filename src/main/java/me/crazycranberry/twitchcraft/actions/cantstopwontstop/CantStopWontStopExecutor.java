package me.crazycranberry.twitchcraft.actions.cantstopwontstop;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.crazycranberry.twitchcraft.actions.Executor;
import me.crazycranberry.twitchcraft.config.Action;
import me.crazycranberry.twitchcraft.twitch.websocket.model.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

import static me.crazycranberry.twitchcraft.TwitchCraft.getPlugin;
import static me.crazycranberry.twitchcraft.TwitchCraft.logger;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.TICKS_PER_SECOND;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.getTargetedPlayers;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.maybeSendPlayerMessage;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.triggerer;

public class CantStopWontStopExecutor implements Executor {
    private final static long numTicksBetweenChecks = 2L;
    Map<Player, CantStopWontStopStats> cantStopWontStopStats = new HashMap<>();

    @Override
    public void execute(Message twitchMessage, Action action) {
        if (!(action instanceof CantStopWontStop)) {
            logger().warning("Somehow the following action was passed to " + this.getClass().getName() + ": " + action + "\nAborting execution.");
            return;
        }
        CantStopWontStop csws = (CantStopWontStop) action;
        for (Player p : getTargetedPlayers(csws)) {
            maybeSendPlayerMessage(p, twitchMessage, String.format("Can't Stop Won't Stop!!%s Courtesy of %s%s%s", ChatColor.GOLD, ChatColor.RESET, ChatColor.GOLD, triggerer(twitchMessage, action), ChatColor.RESET), action);
            if (cantStopWontStopStats.containsKey(p)) {
                cantStopWontStopStats.get(p).setTimeRemaining((int) (((double)TICKS_PER_SECOND / (double)numTicksBetweenChecks) * csws.getDurationSeconds()));
            } else {
                cantStopWontStopStats.put(p, CantStopWontStopStats.builder()
                        .timeRemaining((int) (((double)TICKS_PER_SECOND / (double)numTicksBetweenChecks) * csws.getDurationSeconds()))
                        .taskId(null)
                        .build());
                int taskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
                    CantStopWontStopStats cswss = cantStopWontStopStats.get(p);
                    if (cswss.getTimeRemaining() >= 0) {
                        p.setVelocity(p.getLocation().getDirection());
                        cswss.setTimeRemaining(cswss.getTimeRemaining() - 1);
                    } else {
                        Bukkit.getScheduler().cancelTask(cswss.getTaskId());
                        cantStopWontStopStats.remove(p);
                    }
                }, 0 /*<-- the initial delay */, numTicksBetweenChecks /*<-- the interval */).getTaskId();
                cantStopWontStopStats.get(p).setTaskId(taskId);
            }
        }
    }

    @Getter
    @Setter
    @Builder
    private static class CantStopWontStopStats {
        private Integer timeRemaining;
        private Integer taskId;
    }
}
