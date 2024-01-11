package me.crazycranberry.streamcraft.actions.nojumping;

import me.crazycranberry.streamcraft.actions.Executor;
import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

import static me.crazycranberry.streamcraft.StreamCraft.getPlugin;
import static me.crazycranberry.streamcraft.StreamCraft.logger;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.TICKS_PER_SECOND;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.getTargetedPlayers;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.maybeSendPlayerMessage;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.triggerer;

public class NoJumpingExecutor implements Executor {
    private static Integer timeLeftTaskId;
    private static Map<Player, Integer> timeLeftMap = new HashMap<>();

    @Override
    public void execute(Message twitchMessage, Action action) {
        if (!(action instanceof NoJumping)) {
            logger().warning("Somehow the following action was passed to " + this.getClass().getName() + ": " + action + "\nAborting execution.");
            return;
        }
        NoJumping nj = (NoJumping) action;
        if (timeLeftMap.isEmpty()) {
            timeLeftTaskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    Integer secondsLeft = timeLeftMap.get(p);
                    if (secondsLeft == null) {
                        continue;
                    }
                    secondsLeft--;
                    if (secondsLeft <= 0) {
                        timeLeftMap.remove(p);
                    } else {
                        timeLeftMap.put(p, secondsLeft);
                    }
                    if (timeLeftMap.isEmpty() && timeLeftTaskId != null) {
                        maybeSendPlayerMessage(p, "You can jump again.", action);
                        Bukkit.getScheduler().cancelTask(timeLeftTaskId);
                    }
                }
            }, TICKS_PER_SECOND /*<-- the initial delay */, TICKS_PER_SECOND /*<-- the interval */).getTaskId();
        }
        for (Player p : getTargetedPlayers(nj)) {
            maybeSendPlayerMessage(p, String.format("You cannot jump for %s%s seconds%s, courtesy of %s%s%s", ChatColor.GOLD, nj.getDurationSeconds(), ChatColor.RESET, ChatColor.GOLD, triggerer(twitchMessage, nj), ChatColor.RESET), action);
            timeLeftMap.put(p, nj.getDurationSeconds());
        }
    }

    public static boolean playerCanJump(Player p) {
        return !timeLeftMap.containsKey(p);
    }
}
