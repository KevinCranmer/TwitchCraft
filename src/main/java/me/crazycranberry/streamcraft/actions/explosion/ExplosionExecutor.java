package me.crazycranberry.streamcraft.actions.explosion;

import me.crazycranberry.streamcraft.actions.Executor;
import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

import static me.crazycranberry.streamcraft.StreamCraft.logger;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.getTargetedPlayers;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.maybeSendPlayerMessage;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.triggerer;

public class ExplosionExecutor implements Executor {
    private static Set<Player> protectedPlayers = new HashSet<>();

    @Override
    public void execute(Message twitchMessage, Action action) {
        if (!(action instanceof Explosion)) {
            logger().warning("Somehow the following action was passed to " + this.getClass().getName() + ": " + action + "\nAborting execution.");
            return;
        }
        Explosion e = (Explosion) action;
        for (Player p : getTargetedPlayers(e)) {
            maybeSendPlayerMessage(p, String.format("Boom! courtesy of %s%s%s", ChatColor.GOLD, triggerer(twitchMessage, e), ChatColor.RESET));
            protectedPlayers.add(p);
            p.getWorld().createExplosion(p.getLocation(), e.getPower());
        }
    }

    public static boolean protectPlayer(Player p) {
        if (protectedPlayers.contains(p)) {
            protectedPlayers.remove(p);
            return true;
        }
        return false;
    }
}
