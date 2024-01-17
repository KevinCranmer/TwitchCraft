package me.crazycranberry.streamcraft.actions.megajump;

import me.crazycranberry.streamcraft.actions.Executor;
import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static me.crazycranberry.streamcraft.StreamCraft.getPlugin;
import static me.crazycranberry.streamcraft.StreamCraft.logger;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.TICKS_PER_SECOND;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.beautifyActionMessage;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.getTargetedPlayers;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.maybeSendPlayerMessage;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.maybeSendPlayerSecondaryMessage;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.triggerer;

public class MegaJumpExecutor implements Executor {
    private static Integer timeLeftTaskId;
    private static Map<Player, Integer> timeLeftMap = new HashMap<>();
    private static Map<Player, Integer> jumpsLeftMap = new HashMap<>();
    private static Set<Player> playersCurrentlyMegaJumping = new HashSet<>();

    @Override
    public void execute(Message twitchMessage, Action action) {
        if (!(action instanceof MegaJump)) {
            logger().warning("Somehow the following action was passed to " + this.getClass().getName() + ": " + action + "\nAborting execution.");
            return;
        }
        MegaJump mj = (MegaJump) action;
        if (timeLeftMap.isEmpty() && mj.getDurationSeconds() != null) {
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
                        sendEndMessage(p, twitchMessage, mj);
                        Bukkit.getScheduler().cancelTask(timeLeftTaskId);
                    }
                }
            }, TICKS_PER_SECOND /*<-- the initial delay */, TICKS_PER_SECOND /*<-- the interval */).getTaskId();
        }
        for (Player p : getTargetedPlayers(mj)) {
            if (mj.getDurationSeconds() != null) {
                maybeSendPlayerMessage(p, twitchMessage, String.format("You've been granted %s%s seconds of mega jump%s, courtesy of %s%s%s", ChatColor.GOLD, mj.getDurationSeconds(), ChatColor.RESET, ChatColor.GOLD, triggerer(twitchMessage, mj), ChatColor.RESET), action);
                timeLeftMap.put(p, mj.getDurationSeconds());
            } else {
                maybeSendPlayerMessage(p, twitchMessage, String.format("You've been granted %s%s mega jump%s%s, courtesy of %s%s%s", ChatColor.GOLD, mj.getNumJumps(), mj.getNumJumps() > 1 ? "s" : "", ChatColor.RESET, ChatColor.GOLD, triggerer(twitchMessage, mj), ChatColor.RESET), action);
                jumpsLeftMap.put(p, mj.getNumJumps());
            }
        }
    }

    private void sendEndMessage(Player p, Message twitchMessage, MegaJump mj) {
        String endMessage = "Mega Jump Ended.";
        if (mj.getEndMessage() != null) {
            endMessage = beautifyActionMessage(mj.getEndMessage(), twitchMessage, mj);
        }
        maybeSendPlayerSecondaryMessage(p, endMessage, mj);
    }

    public static boolean shouldPlayerMegaJump(Player p) {
        return timeLeftMap.containsKey(p) || jumpsLeftMap.containsKey(p);
    }

    public static void playerJumped(Player p) {
        if (!shouldPlayerMegaJump(p)) {
            return;
        }
        Integer jumpsLeft = jumpsLeftMap.get(p);
        if (jumpsLeft != null) {
            jumpsLeft--;
            if (jumpsLeft <= 0) {
                jumpsLeftMap.remove(p);
            } else {
                jumpsLeftMap.put(p, jumpsLeft);
            }
        }
        playersCurrentlyMegaJumping.add(p);
    }

    public static boolean shouldPlayerIgnoreFallDamage(Player p) {
        return playersCurrentlyMegaJumping.contains(p);
    }

    public static void playerIgnoredMegaJumpFallDamage(Player p) {
        playersCurrentlyMegaJumping.remove(p);
    }
}
