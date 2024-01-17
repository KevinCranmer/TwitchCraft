package me.crazycranberry.twitchcraft.actions.explosion;

import me.crazycranberry.twitchcraft.actions.Executor;
import me.crazycranberry.twitchcraft.config.Action;
import me.crazycranberry.twitchcraft.twitch.websocket.model.message.Message;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

import static me.crazycranberry.twitchcraft.TwitchCraft.logger;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.getTargetedPlayers;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.maybeSendPlayerMessage;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.triggerer;

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
            maybeSendPlayerMessage(p, twitchMessage, String.format("Boom! Courtesy of %s%s%s", ChatColor.GOLD, triggerer(twitchMessage, e), ChatColor.RESET), action);
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
