package me.crazycranberry.twitchcraft.actions.raid;

import me.crazycranberry.twitchcraft.actions.Executor;
import me.crazycranberry.twitchcraft.config.Action;
import me.crazycranberry.twitchcraft.twitch.websocket.model.message.Message;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static me.crazycranberry.twitchcraft.TwitchCraft.logger;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.getTargetedPlayers;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.maybeSendPlayerMessage;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.triggerer;

public class RaidExecutor implements Executor {
    @Override
    public void execute(Message twitchMessage, Action action) {
        if (!(action instanceof Raid)) {
            logger().warning("Somehow the following action was passed to " + this.getClass().getName() + ": " + action + "\nAborting execution.");
            return;
        }
        Raid r = (Raid) action;
        for (Player p : getTargetedPlayers(r)) {
            maybeSendPlayerMessage(p, twitchMessage, String.format("Incoming %sraid%s, courtesy of %s%s%s", ChatColor.GOLD, ChatColor.RESET, ChatColor.GOLD, triggerer(twitchMessage, action), ChatColor.RESET), action);
            Location playerLoc = p.getLocation();
            playerLoc.getBlock().getRelative(1, 0, 0).setType(Material.COMPOSTER);
            p.addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN, 1200, r.getBadOmenLevel()));
            p.getWorld().spawnEntity(playerLoc, EntityType.VILLAGER, false);
        }
    }
}
